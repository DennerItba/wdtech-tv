package com.mytv.app

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ts.TsExtractor
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.adapter.PlaybackHistoryAdapter
import com.mytv.app.data.PlaybackRepository
import com.mytv.app.model.Channel
import com.mytv.app.model.EpgProgram
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var overlayView: View
    private lateinit var bufferingProgress: ProgressBar
    private lateinit var btnBack: Button

    // Componentes de Playback / 7 Dias Gravados
    private lateinit var txtBadgeMode: TextView
    private lateinit var txtPlaybackBadge: TextView
    private lateinit var layoutPlaybackControls: View
    private lateinit var btnRewind: Button
    private lateinit var btnForward: Button
    private lateinit var btnRestart: Button
    private lateinit var btnLive: Button
    private lateinit var btnHistory: Button
    private lateinit var txtPlaybackStatus: TextView

    // Estado do Playback / Timeshift
    private var isPlaybackMode = false
    private var playbackOffsetSeconds: Long = 0L
    private var playbackProgramTitle: String? = null
    private var currentStreamUrl: String? = null

    // Componentes do Card de Erro / Reconexão
    private lateinit var errorLayout: View
    private lateinit var txtErrorTitle: TextView
    private lateinit var txtErrorMessage: TextView
    private lateinit var txtErrorCountdown: TextView
    private lateinit var btnErrorRetry: Button
    private lateinit var btnErrorBack: Button

    private var currentChannel: Channel? = null
    private var retryCount = 0
    private val maxAutoRetries = 2
    private val retryHandler = Handler(Looper.getMainLooper())
    private var retryRunnable: Runnable? = null

    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable {
        overlayView.animate().alpha(0f).setDuration(300).withEndAction {
            overlayView.visibility = View.GONE
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.player_view)
        overlayView = findViewById(R.id.player_overlay)
        bufferingProgress = findViewById(R.id.player_buffering)
        btnBack = findViewById(R.id.btn_player_back)

        // Controles de Playback
        txtBadgeMode = findViewById(R.id.txt_player_badge_mode)
        txtPlaybackBadge = findViewById(R.id.txt_player_playback_badge)
        layoutPlaybackControls = findViewById(R.id.layout_player_playback_controls)
        btnRewind = findViewById(R.id.btn_player_rewind)
        btnForward = findViewById(R.id.btn_player_forward)
        btnRestart = findViewById(R.id.btn_player_restart)
        btnLive = findViewById(R.id.btn_player_live)
        btnHistory = findViewById(R.id.btn_player_history)
        txtPlaybackStatus = findViewById(R.id.txt_player_playback_status)

        // Inicializa controles do Card de Erro
        errorLayout = findViewById(R.id.player_error_layout)
        txtErrorTitle = findViewById(R.id.txt_player_error_title)
        txtErrorMessage = findViewById(R.id.txt_player_error_message)
        txtErrorCountdown = findViewById(R.id.txt_player_error_countdown)
        btnErrorRetry = findViewById(R.id.btn_player_error_retry)
        btnErrorBack = findViewById(R.id.btn_player_error_back)

        btnErrorRetry.setOnClickListener {
            retryCount = 0
            currentChannel?.let { startStream(it, currentStreamUrl) }
        }

        btnErrorBack.setOnClickListener {
            finish()
        }

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_CHANNEL, Channel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_CHANNEL) as? Channel
        }

        if (channel == null || channel.streamUrl.isEmpty()) {
            Toast.makeText(this, R.string.error_stream, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Garante enriquecimento com Playback
        val enriched = if (channel.currentProgram == null || !channel.hasPlayback) {
            com.mytv.app.data.EpgRepository.enrichChannel(channel)
        } else {
            channel
        }

        this.currentChannel = enriched

        findViewById<TextView>(R.id.txt_player_channel_title).text = enriched.title
        findViewById<TextView>(R.id.txt_player_channel_category).text =
            "${enriched.category} • Transmissão em Alta Definição"

        // Exibe o EPG (Guia de Programação) no player
        val epg = enriched.currentProgram
        if (epg != null) {
            findViewById<TextView>(R.id.txt_player_epg_now_title).text = epg.title
            findViewById<TextView>(R.id.txt_player_epg_time).text = "${epg.startTime} - ${epg.endTime}"
            findViewById<ProgressBar>(R.id.pb_player_epg).progress = epg.getProgressPercent()
            findViewById<TextView>(R.id.txt_player_epg_synopsis).text = epg.synopsis.ifEmpty { "Transmissão ao vivo de alta definição." }
        }

        val nextEpg = enriched.nextProgram
        if (nextEpg != null) {
            findViewById<TextView>(R.id.txt_player_epg_next).text = "▶ A Seguir (${nextEpg.startTime}): ${nextEpg.title}"
        }

        // Busca assincronamente o EPG ao vivo no player
        lifecycleScope.launch {
            val livePrograms = com.mytv.app.data.EpgRepository.fetchLiveEpg(enriched)
            if (livePrograms != null && !isFinishing && !isDestroyed) {
                val liveNow = livePrograms.first
                val liveNext = livePrograms.second
                findViewById<TextView>(R.id.txt_player_epg_now_title).text = liveNow.title
                findViewById<TextView>(R.id.txt_player_epg_time).text = "${liveNow.startTime} - ${liveNow.endTime}"
                findViewById<ProgressBar>(R.id.pb_player_epg).progress = liveNow.getProgressPercent()
                findViewById<TextView>(R.id.txt_player_epg_synopsis).text = liveNow.synopsis.ifEmpty { "Transmissão ao vivo de alta definição." }
                if (liveNext != null) {
                    findViewById<TextView>(R.id.txt_player_epg_next).text = "▶ A Seguir (${liveNext.startTime}): ${liveNext.title}"
                }
            }
        }

        // Configuração visual dos recursos de Playback
        if (enriched.hasPlayback) {
            txtPlaybackBadge.visibility = View.VISIBLE
            layoutPlaybackControls.visibility = View.VISIBLE

            btnRewind.setOnClickListener { rewind(60) }
            btnForward.setOnClickListener { forward(60) }
            btnRestart.setOnClickListener { restartProgram() }
            btnLive.setOnClickListener { playLiveStream() }
            btnHistory.setOnClickListener { showPlaybackHistoryDialog(enriched) }
        } else {
            txtPlaybackBadge.visibility = View.GONE
            layoutPlaybackControls.visibility = View.GONE
        }

        btnBack.setOnClickListener {
            finish()
        }

        startStream(enriched, null)
        showOverlayTemporarily()
    }

    private fun startStream(channel: Channel, customUrl: String? = null) {
        val rawUrl = customUrl ?: channel.streamUrl
        val targetUrl = if (rawUrl.contains("vivlar.me") && rawUrl.endsWith(".m3u8")) {
            rawUrl.replace(".m3u8", ".ts")
        } else {
            rawUrl
        }
        this.currentStreamUrl = targetUrl

        // Limpa estado de erro anterior
        retryHandler.removeCallbacksAndMessages(null)
        errorLayout.visibility = View.GONE
        bufferingProgress.visibility = View.VISIBLE

        player?.release()
        player = null

        val httpFactory = androidx.media3.datasource.DefaultHttpDataSource.Factory()
            .setUserAgent("IPTVSmartersPro")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setDefaultRequestProperties(mapOf(
                "Accept" to "*/*",
                "Connection" to "keep-alive"
            ))

        val dataSourceFactory = DefaultDataSource.Factory(this, httpFactory)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15_000,
                30_000,
                1_500,
                2_500
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .setBackBuffer(10_000, false)
            .build()

        val exo = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .build().apply {
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_BUFFERING) {
                        bufferingProgress.visibility = View.VISIBLE
                    } else if (state == Player.STATE_READY) {
                        bufferingProgress.visibility = View.GONE
                        errorLayout.visibility = View.GONE
                        retryCount = 0
                    } else {
                        bufferingProgress.visibility = View.GONE
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    handlePlaybackError(error, channel)
                }
            })
        }

        val extractorsFactory = DefaultExtractorsFactory().apply {
            setTsExtractorMode(TsExtractor.MODE_SINGLE_PMT)
            setConstantBitrateSeekingEnabled(true)
        }

        val mediaItem = MediaItem.Builder()
            .setUri(targetUrl)
            .apply {
                if (targetUrl.endsWith(".ts") || targetUrl.contains(".ts?")) {
                    setMimeType(MimeTypes.VIDEO_MP2T)
                }
            }
            .build()

        val mediaSource = if (targetUrl.contains(".m3u8")) {
            HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(mediaItem)
        } else {
            DefaultMediaSourceFactory(dataSourceFactory, extractorsFactory)
                .createMediaSource(mediaItem)
        }

        exo.setMediaSource(mediaSource)
        exo.prepare()

        playerView.player = exo
        this.player = exo
    }

    private fun playTimeshift(offsetSeconds: Long, progTitle: String? = null) {
        val channel = currentChannel ?: return
        isPlaybackMode = true
        playbackOffsetSeconds = offsetSeconds.coerceAtLeast(60).coerceAtMost(7 * 24 * 3600)
        playbackProgramTitle = progTitle

        txtBadgeMode.text = "⟲ REPLAY"
        txtBadgeMode.setBackgroundResource(R.drawable.badge_playback)

        val offsetHours = playbackOffsetSeconds / 3600
        val offsetMinutes = (playbackOffsetSeconds % 3600) / 60
        val offsetText = if (offsetHours > 0) {
            "-${offsetHours}h ${offsetMinutes}m"
        } else {
            "-${offsetMinutes}m"
        }

        val status = if (!progTitle.isNullOrEmpty()) {
            "⟲ Gravado: $progTitle ($offsetText) • [Ao Vivo] p/ voltar"
        } else {
            "⟲ Replay: $offsetText atrás • [Ao Vivo] p/ voltar"
        }
        txtPlaybackStatus.text = status

        val timeshiftUrl = PlaybackRepository.getPlaybackUrl(channel, playbackOffsetSeconds)
        startStream(channel, timeshiftUrl)
        showOverlayTemporarily()
    }

    private fun playProgram(program: EpgProgram) {
        val now = System.currentTimeMillis()
        val offset = ((now - program.startTimestamp) / 1000).coerceAtLeast(60)
        Toast.makeText(this, "▶ Reproduzindo gravação: ${program.title}", Toast.LENGTH_SHORT).show()
        playTimeshift(offset, program.title)
    }

    private fun playLiveStream() {
        val channel = currentChannel ?: return
        isPlaybackMode = false
        playbackOffsetSeconds = 0
        playbackProgramTitle = null

        txtBadgeMode.text = "AO VIVO"
        txtBadgeMode.setBackgroundResource(R.drawable.badge_live)
        txtPlaybackStatus.text = "Controle remoto: ◀ e ▶ para retroceder / avançar"

        Toast.makeText(this, "🔴 Transmissão Ao Vivo", Toast.LENGTH_SHORT).show()
        startStream(channel, null)
        showOverlayTemporarily()
    }

    private fun rewind(seconds: Long) {
        val channel = currentChannel ?: return
        if (!channel.hasPlayback) {
            Toast.makeText(this, "Este canal não possui suporte a Playback gravado", Toast.LENGTH_SHORT).show()
            return
        }
        val newOffset = if (!isPlaybackMode) {
            seconds
        } else {
            playbackOffsetSeconds + seconds
        }
        Toast.makeText(this, "⏮ Retrocedendo ${seconds / 60}m...", Toast.LENGTH_SHORT).show()
        playTimeshift(newOffset)
    }

    private fun forward(seconds: Long) {
        val channel = currentChannel ?: return
        if (!channel.hasPlayback) return
        if (!isPlaybackMode) {
            Toast.makeText(this, "Você já está na transmissão Ao Vivo", Toast.LENGTH_SHORT).show()
            return
        }
        val newOffset = playbackOffsetSeconds - seconds
        if (newOffset <= 30) {
            playLiveStream()
        } else {
            Toast.makeText(this, "⏭ Avançando ${seconds / 60}m...", Toast.LENGTH_SHORT).show()
            playTimeshift(newOffset)
        }
    }

    private fun restartProgram() {
        val channel = currentChannel ?: return
        if (!channel.hasPlayback) {
            Toast.makeText(this, "Este canal não possui suporte a Playback", Toast.LENGTH_SHORT).show()
            return
        }
        val currentProg = channel.currentProgram
        if (currentProg != null) {
            val now = System.currentTimeMillis()
            val offset = ((now - currentProg.startTimestamp) / 1000).coerceAtLeast(60)
            Toast.makeText(this, "⟲ Reiniciando: ${currentProg.title}", Toast.LENGTH_SHORT).show()
            playTimeshift(offset, currentProg.title)
        } else {
            rewind(1800)
        }
    }

    private fun showPlaybackHistoryDialog(channel: Channel) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_playback_history)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.88).toInt(),
            (resources.displayMetrics.heightPixels * 0.85).toInt()
        )

        val txtTitle = dialog.findViewById<TextView>(R.id.txt_dialog_title)
        val txtSubtitle = dialog.findViewById<TextView>(R.id.txt_dialog_subtitle)
        val btnClose = dialog.findViewById<Button>(R.id.btn_dialog_close)
        val layoutDays = dialog.findViewById<LinearLayout>(R.id.layout_dialog_days)
        val rvPrograms = dialog.findViewById<RecyclerView>(R.id.rv_dialog_programs)
        val txtEmpty = dialog.findViewById<TextView>(R.id.txt_dialog_empty)

        txtTitle.text = "Guia Playback • ${channel.title}"
        txtSubtitle.text = "Exibindo os últimos 7 dias de gravações disponíveis na nuvem. Selecione um programa para assistir."

        btnClose.setOnClickListener {
            dialog.dismiss()
            showOverlayTemporarily()
        }

        rvPrograms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val historyAdapter = PlaybackHistoryAdapter(emptyList()) { selectedProgram ->
            dialog.dismiss()
            playProgram(selectedProgram)
        }
        rvPrograms.adapter = historyAdapter

        // Cria os botões para os 7 dias (0 = Hoje, 1 = Ontem, 2..6)
        val dayButtons = mutableListOf<Button>()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"))
        val dayFormat = SimpleDateFormat("dd/MM (EEE)", Locale("pt", "BR"))
        dayFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

        fun loadDay(dayOffset: Int) {
            val programs = PlaybackRepository.getProgramHistory(channel, dayOffset)
            historyAdapter.updateData(programs)
            txtEmpty.visibility = if (programs.isEmpty()) View.VISIBLE else View.GONE
            dayButtons.forEachIndexed { idx, btn ->
                btn.isSelected = (idx == dayOffset)
            }
        }

        val btnHeightPx = (38 * resources.displayMetrics.density).toInt()
        val marginPx = (6 * resources.displayMetrics.density).toInt()
        val paddingPx = (14 * resources.displayMetrics.density).toInt()

        for (i in 0 until 7) {
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, -i)
            val label = when (i) {
                0 -> "Hoje"
                1 -> "Ontem"
                else -> dayFormat.format(c.time).replaceFirstChar { it.uppercase() }
            }

            val btnDay = Button(this).apply {
                text = label
                textSize = 12f
                setTextColor(Color.WHITE)
                background = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.bg_tab_pill)
                isFocusable = true
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    btnHeightPx
                ).apply {
                    setMargins(marginPx, 0, marginPx, 0)
                }
                setPadding(paddingPx, 0, paddingPx, 0)
                setOnClickListener {
                    loadDay(i)
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        loadDay(i)
                    }
                }
            }
            dayButtons.add(btnDay)
            layoutDays.addView(btnDay)
        }

        // Carrega Hoje por padrão
        loadDay(0)
        dialog.show()
        if (dayButtons.isNotEmpty()) {
            dayButtons[0].requestFocus()
        }
    }

    private fun handlePlaybackError(error: PlaybackException, channel: Channel) {
        bufferingProgress.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        txtErrorTitle.text = "Sinal Instável: ${channel.title}"

        if (retryCount < maxAutoRetries) {
            retryCount++
            txtErrorMessage.text = "Detectada oscilação na fonte de transmissão (${error.errorCodeName}). Reconectando stream automaticamente..."
            txtErrorCountdown.text = "Tentativa $retryCount de $maxAutoRetries em 3 segundos..."
            txtErrorCountdown.visibility = View.VISIBLE

            val fallbackUrl = currentStreamUrl?.let { getFallbackUrl(it) }
            val streamToUse = if (fallbackUrl != null && retryCount > 1) fallbackUrl else currentStreamUrl
            val runnable = Runnable {
                if (!isFinishing && !isDestroyed) {
                    startStream(channel, streamToUse)
                }
            }
            retryRunnable = runnable
            retryHandler.postDelayed(runnable, 3000)
        } else {
            txtErrorMessage.text = if (isPlaybackMode) {
                "A gravação deste horário não pôde ser carregada. Você pode tentar novamente ou retornar à transmissão Ao Vivo."
            } else {
                "O sinal deste canal está temporariamente indisponível na operadora de origem. Você pode tentar novamente ou escolher outro canal na grade."
            }
            txtErrorCountdown.visibility = View.GONE
            btnErrorRetry.requestFocus()
        }
    }

    private fun showOverlayTemporarily() {
        overlayView.alpha = 1f
        overlayView.visibility = View.VISIBLE
        btnBack.requestFocus()

        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 5000)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    finish()
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                KeyEvent.KEYCODE_MEDIA_PLAY,
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    player?.let {
                        if (it.isPlaying) {
                            it.pause()
                            Toast.makeText(this, "⏸ Pausado", Toast.LENGTH_SHORT).show()
                        } else {
                            it.play()
                            Toast.makeText(this, "▶ Reproduzindo", Toast.LENGTH_SHORT).show()
                        }
                    }
                    showOverlayTemporarily()
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_REWIND,
                KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD -> {
                    currentChannel?.let { if (it.hasPlayback) rewind(60) }
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD,
                KeyEvent.KEYCODE_MEDIA_STEP_FORWARD -> {
                    currentChannel?.let { if (it.hasPlayback) forward(60) }
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    val isVod = currentChannel?.id?.startsWith("vod_") == true || currentStreamUrl?.contains("/movie/") == true || currentStreamUrl?.contains("/series/") == true
                    if (isVod) {
                        player?.let { p ->
                            val target = (p.currentPosition - 15000L).coerceAtLeast(0)
                            p.seekTo(target)
                            val sec = target / 1000
                            Toast.makeText(this, "⏮ ${sec / 60}:${String.format("%02d", sec % 60)}", Toast.LENGTH_SHORT).show()
                            showOverlayTemporarily()
                            return true
                        }
                    } else if (overlayView.visibility != View.VISIBLE) {
                        currentChannel?.let {
                            if (it.hasPlayback) {
                                rewind(60)
                                return true
                            }
                        }
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    val isVod = currentChannel?.id?.startsWith("vod_") == true || currentStreamUrl?.contains("/movie/") == true || currentStreamUrl?.contains("/series/") == true
                    if (isVod) {
                        player?.let { p ->
                            val dur = p.duration
                            val target = (p.currentPosition + 15000L).coerceAtMost(if (dur > 0) dur else Long.MAX_VALUE)
                            p.seekTo(target)
                            val sec = target / 1000
                            Toast.makeText(this, "⏭ ${sec / 60}:${String.format("%02d", sec % 60)}", Toast.LENGTH_SHORT).show()
                            showOverlayTemporarily()
                            return true
                        }
                    } else if (overlayView.visibility != View.VISIBLE) {
                        currentChannel?.let {
                            if (it.hasPlayback && isPlaybackMode) {
                                forward(60)
                                return true
                            }
                        }
                    }
                }
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_ENTER,
                KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                    if (overlayView.visibility != View.VISIBLE) {
                        player?.let { p ->
                            if (p.isPlaying) {
                                p.pause()
                                Toast.makeText(this, "⏸ Pausado", Toast.LENGTH_SHORT).show()
                            } else {
                                p.play()
                                Toast.makeText(this, "▶ Reproduzindo", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showOverlayTemporarily()
                        return true
                    }
                }
            }

            if (overlayView.visibility != View.VISIBLE) {
                showOverlayTemporarily()
                return true
            } else {
                showOverlayTemporarily()
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideHandler.removeCallbacks(hideRunnable)
        retryHandler.removeCallbacksAndMessages(null)
        player?.release()
        player = null
    }

    companion object {
        const val EXTRA_CHANNEL = "extra_channel"
        private val STREAM_FALLBACKS = mapOf(
            "71862" to "71865", // Discovery Channel FHD -> HD
            "71871" to "71873", // Discovery Kids FHD -> HD
            "71867" to "71869", // Discovery H&H FHD -> HD
            "71883" to "71885", // Discovery Turbo FHD -> HD
            "72150" to "72152", // ID FHD -> HD
            "71875" to "71877", // Discovery Science FHD -> HD
            "71879" to "71881", // Discovery Theater FHD -> HD
            "71887" to "71889", // Discovery World FHD -> HD
            "71772" to "71775", // Animal Planet FHD -> HD
            "72146" to "72148", // History FHD -> HD
            "72106" to "72108", // H2 FHD -> HD
            "72362" to "72364", // Telecine Premium FHD -> HD
            "72346" to "72348", // Telecine Action FHD -> HD
            "72366" to "72368", // Telecine Touch FHD -> HD
            "72358" to "72360", // Telecine Pipoca FHD -> HD
            "72354" to "72356", // Telecine Fun FHD -> HD
            "72350" to "72352", // Telecine Cult FHD -> HD
            "72118" to "72120", // HBO FHD -> HD
            "72110" to "72112", // HBO 2 FHD -> HD
            "72125" to "72127", // HBO Plus FHD -> HD
            "72114" to "72116", // HBO Family FHD -> HD
            "72134" to "72136", // HBO Signature FHD -> HD
            "72121" to "72123", // HBO Mundi FHD -> HD
            "72129" to "72131", // HBO Pop FHD -> HD
            "72138" to "72140"  // HBO Xtreme FHD -> HD
        )

        fun getFallbackUrl(url: String): String? {
            for ((fhdId, hdId) in STREAM_FALLBACKS) {
                if (url.contains("/$fhdId.m3u8")) {
                    return url.replace("/$fhdId.m3u8", "/$hdId.m3u8")
                }
                if (url.contains("/$fhdId.ts")) {
                    return url.replace("/$fhdId.ts", "/$hdId.ts")
                }
            }
            return null
        }
    }
}
