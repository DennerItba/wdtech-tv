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
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.adapter.PlaybackHistoryAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import androidx.media3.common.C
import androidx.media3.ui.CaptionStyleCompat

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
    private lateinit var btnAspectRatio: Button

    private val bufferingHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val showBufferingRunnable = Runnable {
        bufferingProgress.visibility = android.view.View.VISIBLE
    }

    private fun showBufferingWithDebounce(delayMs: Long = 700L) {
        bufferingHandler.removeCallbacks(showBufferingRunnable)
        bufferingHandler.postDelayed(showBufferingRunnable, delayMs)
    }

    private fun hideBuffering() {
        bufferingHandler.removeCallbacks(showBufferingRunnable)
        bufferingProgress.visibility = android.view.View.GONE
    }

    // Navegacao por Canais (Zapping)
    private var channelList: ArrayList<Channel> = arrayListOf()
    private var currentChannelIndex: Int = -1
    private var currentResizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT

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
    private val maxAutoRetries = 6
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
        btnAspectRatio = findViewById(R.id.btn_player_aspect_ratio)
        btnAspectRatio.setOnClickListener {
            toggleAspectRatio()
        }
        val btnPlayerSettings = findViewById<Button>(R.id.btn_player_settings)
        btnPlayerSettings?.setOnClickListener {
            showPlaybackSettingsDialog()
        }

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

        // Recebe a lista completa de canais para zapping direto pelo controle remoto
        val extraList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_CHANNEL_LIST, ArrayList::class.java) as? ArrayList<Channel>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_CHANNEL_LIST) as? ArrayList<Channel>
        }
        if (!extraList.isNullOrEmpty()) {
            this.channelList = extraList
            this.currentChannelIndex = intent.getIntExtra(EXTRA_CHANNEL_INDEX, -1)
            if (this.currentChannelIndex == -1) {
                this.currentChannelIndex = extraList.indexOfFirst { it.id == channel.id }
            }
        }

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
            btnHistory.setOnClickListener {
                val pIntent = android.content.Intent(this, PlaybackActivity::class.java).apply {
                    putExtra(PlaybackActivity.EXTRA_SELECTED_CHANNEL, enriched)
                    putExtra(PlaybackActivity.EXTRA_CHANNEL_LIST, ArrayList(channelList))
                }
                startActivity(pIntent)
            }
        } else {
            txtPlaybackBadge.visibility = View.GONE
            layoutPlaybackControls.visibility = View.GONE
        }

        btnBack.setOnClickListener {
            finish()
        }

        val playbackOffset = intent.getLongExtra(EXTRA_PLAYBACK_OFFSET, 0L)
        val playbackTitle = intent.getStringExtra(EXTRA_PLAYBACK_TITLE)
        if (playbackOffset > 0L) {
            playTimeshift(playbackOffset, playbackTitle)
        } else {
            startStream(enriched, null)
        }
        showOverlayTemporarily()
    }

    private fun toggleAspectRatio() {
        currentResizeMode = when (currentResizeMode) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
        playerView.resizeMode = currentResizeMode
        val label = when (currentResizeMode) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> "Original (16:9)"
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> "Zoom (Sem Bordas)"
            else -> "Esticar (Preencher)"
        }
        btnAspectRatio.text = "📐 $label"
        Toast.makeText(this, "Formato: $label", Toast.LENGTH_SHORT).show()
        showOverlayTemporarily()
    }

    private fun zapChannel(direction: Int) {
        if (channelList.isEmpty()) return
        val newIndex = (currentChannelIndex + direction).coerceIn(0, channelList.size - 1)
        if (newIndex == currentChannelIndex) return
        currentChannelIndex = newIndex
        val nextChannel = channelList[newIndex]

        val enriched = if (nextChannel.currentProgram == null || !nextChannel.hasPlayback) {
            com.mytv.app.data.EpgRepository.enrichChannel(nextChannel)
        } else nextChannel
        this.currentChannel = enriched

        findViewById<TextView>(R.id.txt_player_channel_title).text = enriched.title
        findViewById<TextView>(R.id.txt_player_channel_category).text =
            "${enriched.category} • Transmissão em Alta Definição"

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

        // Atualiza controles de Playback
        if (enriched.hasPlayback) {
            txtPlaybackBadge.visibility = View.VISIBLE
            layoutPlaybackControls.visibility = View.VISIBLE
        } else {
            txtPlaybackBadge.visibility = View.GONE
            layoutPlaybackControls.visibility = View.GONE
        }

        // Inicia a nova transmissão imediatamente
        startStream(enriched, null)
        showOverlayTemporarily()
        Toast.makeText(this, "CH ${enriched.number} • ${enriched.title}", Toast.LENGTH_SHORT).show()
    }

    private fun startStream(channel: Channel, customUrl: String? = null) {
        val rawUrl = customUrl ?: channel.streamUrl
        val targetUrl = rawUrl
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
            .setKeepPostFor302Redirects(true)
            .setDefaultRequestProperties(mapOf(
                "Accept" to "*/*",
                "Connection" to "keep-alive"
            ))

        val dataSourceFactory = DefaultDataSource.Factory(this, httpFactory)

        // Controle avançado de buffer para eliminar travadinhas e oscilações de Wi-Fi na TV Box
        val bufferMode = com.mytv.app.utils.AppSettings.getBufferMode(this)
        val (minBufferMs, maxBufferMs, bufferForPlaybackMs, afterRebufferMs) = when (bufferMode) {
            com.mytv.app.utils.AppSettings.BUFFER_FAST -> listOf(10_000, 30_000, 1_000, 2_000)
            com.mytv.app.utils.AppSettings.BUFFER_STABLE -> listOf(25_000, 60_000, 2_500, 4_000)
            else -> listOf(15_000, 30_000, 2_500, 5_000) // BALANCED (15s minBuffer evita buffer starvation em HLS ao vivo)
        }
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                minBufferMs,
                maxBufferMs,
                bufferForPlaybackMs,
                afterRebufferMs
            )
            .setPrioritizeTimeOverSizeThresholds(false) // Permite reprodução imediata sem exigir buffer irreal
            .setBackBuffer(0, false) // 0s backBuffer: não retém vídeo passado em RAM, eliminando engasgos
            .build()

        // Seleção de trilhas com transição suave, evitando micro-travas na troca de bitrate HLS
        val trackSelector = androidx.media3.exoplayer.trackselection.DefaultTrackSelector(this).apply {
            setParameters(
                buildUponParameters()
                    .setAllowVideoMixedMimeTypeAdaptiveness(true)
                    .setAllowVideoNonSeamlessAdaptiveness(true)
                    .setExceedRendererCapabilitiesIfNecessary(true)
                    .setTunnelingEnabled(false) // Desativa tunneling que congela vídeo em chipsets Amlogic/Rockchip
            )
        }

        val renderersFactory = DefaultRenderersFactory(this).apply {
            setEnableDecoderFallback(true)
            setAllowedVideoJoiningTimeMs(5000)
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        }

        val exo = ExoPlayer.Builder(this, renderersFactory)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .build().apply {
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> {
                            showBufferingWithDebounce(700L)
                        }
                        Player.STATE_READY -> {
                            hideBuffering()
                            errorLayout.visibility = View.GONE
                            retryCount = 0
                            if (channel.resumePositionMs > 0L) {
                                val targetPos = channel.resumePositionMs
                                channel.resumePositionMs = 0L
                                this@apply.seekTo(targetPos)
                                Toast.makeText(this@PlayerActivity, "Continuando de onde parou", Toast.LENGTH_SHORT).show()
                            }
                            startProgressTracking()
                        }
                        Player.STATE_ENDED -> {
                            hideBuffering()
                            val dur = this@apply.duration
                            if (dur > 0) {
                                com.mytv.app.data.WatchProgressRepository.removeProgress(this@PlayerActivity, channel.id)
                            }
                            if (!isPlaybackMode) {
                                // Em transmissões ao vivo, STATE_ENDED indica queda de conexão / EOF no stream.
                                // Reconecta automaticamente sem congelar a imagem na tela!
                                handleLiveStreamEnded(channel)
                            }
                        }
                        Player.STATE_IDLE -> {
                            hideBuffering()
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    handlePlaybackError(error, channel)
                }
            })
        }

        val extractorsFactory = DefaultExtractorsFactory().apply {
            setTsExtractorMode(TsExtractor.MODE_SINGLE_PMT)
            setTsExtractorFlags(
                DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES or
                DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS or
                DefaultTsPayloadReaderFactory.FLAG_IGNORE_SPLICE_INFO_STREAM
            )
            setConstantBitrateSeekingEnabled(true)
        }

        val mediaItem = MediaItem.Builder()
            .setUri(targetUrl)
            .apply {
                if (targetUrl.endsWith(".ts") || targetUrl.contains(".ts?")) {
                    setMimeType(MimeTypes.VIDEO_MP2T)
                }
            }
            .setLiveConfiguration(
                MediaItem.LiveConfiguration.Builder()
                    .setTargetOffsetMs(22_000) // Folga de segurança de 22s (2 chunks HLS) evita buffer starvation
                    .setMinOffsetMs(10_000)
                    .setMaxOffsetMs(50_000)
                    .setMinPlaybackSpeed(0.97f) // Ajuste suave de velocidade (±3%) absorve oscilações sem pausar o vídeo
                    .setMaxPlaybackSpeed(1.03f)
                    .build()
            )
            .build()

        val mediaSource = if (targetUrl.contains(".m3u8")) {
            HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(false) // Garante decodificação e sincronismo precisos de áudio e vídeo
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

    private fun handleLiveStreamEnded(channel: Channel) {
        if (isFinishing || isDestroyed) return
        if (retryCount < maxAutoRetries) {
            retryCount++
            bufferingProgress.visibility = View.VISIBLE
            // Chaveia instantaneamente para o servidor de contingência
            val fallbackUrl = currentStreamUrl?.let { getFallbackUrl(it) }
            val nextUrl = fallbackUrl ?: currentStreamUrl ?: channel.streamUrl
            retryHandler.postDelayed({
                if (!isFinishing && !isDestroyed) {
                    startStream(channel, nextUrl)
                }
            }, 500)
        } else {
            handlePlaybackError(
                PlaybackException("Fim de transmissão ao vivo inesperado (EOF)", null, PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED),
                channel
            )
        }
    }

    private fun handlePlaybackError(error: PlaybackException, channel: Channel) {
        if (isFinishing || isDestroyed) return

        if (retryCount < maxAutoRetries) {
            retryCount++
            // NUNCA exibe popup bloqueante nas primeiras tentativas: recupera em silêncio!
            errorLayout.visibility = View.GONE
            bufferingProgress.visibility = View.VISIBLE

            // Imediatamente busca a melhor URL alternativa (backup oficial ou fallback cruzado)
            val backupFromChannel = channel.backupUrls.getOrNull(retryCount - 1)
            val fallbackUrl = backupFromChannel ?: (currentStreamUrl?.let { getFallbackUrl(it) })
            val streamToUse = fallbackUrl ?: currentStreamUrl ?: channel.streamUrl

            val runnable = Runnable {
                if (!isFinishing && !isDestroyed) {
                    startStream(channel, streamToUse)
                }
            }
            retryRunnable = runnable
            // Reconexão ultrarrápida (600ms) sem travar a tela do usuário
            retryHandler.postDelayed(runnable, 600)
        } else {
            // Apenas se TODAS as 6 tentativas em todas as fontes falharem
            bufferingProgress.visibility = View.GONE
            errorLayout.visibility = View.VISIBLE
            txtErrorTitle.text = "Sinal Instável: ${channel.title}"
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
        // Não força foco em btnBack para evitar que comandos do controle remoto sejam interceptados

        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 6000)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (overlayView.visibility == View.VISIBLE) {
                hideHandler.removeCallbacks(hideRunnable)
                hideHandler.postDelayed(hideRunnable, 6000)
            }
            when (event.keyCode) {
                KeyEvent.KEYCODE_MENU,
                KeyEvent.KEYCODE_SETTINGS -> {
                    showPlaybackSettingsDialog()
                    return true
                }
                KeyEvent.KEYCODE_BACK -> {
                    if (overlayView.visibility == View.VISIBLE) {
                        hideHandler.removeCallbacks(hideRunnable)
                        overlayView.visibility = View.GONE
                        return true
                    }
                    finish()
                    return true
                }
                KeyEvent.KEYCODE_CHANNEL_UP -> {
                    if (channelList.isNotEmpty()) {
                        zapChannel(-1)
                        return true
                    }
                }
                KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                    if (channelList.isNotEmpty()) {
                        zapChannel(1)
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (channelList.isNotEmpty()) {
                        zapChannel(-1)
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (channelList.isNotEmpty()) {
                        zapChannel(1)
                        return true
                    }
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

    // --- Rastreamento do Progresso de Visualização (Continue Assistindo) ---
    private val progressHandler = Handler(Looper.getMainLooper())
    private val progressRunnable = object : Runnable {
        override fun run() {
            saveCurrentWatchProgress()
            progressHandler.postDelayed(this, 5000L)
        }
    }

    private fun startProgressTracking() {
        progressHandler.removeCallbacks(progressRunnable)
        progressHandler.postDelayed(progressRunnable, 5000L)
    }

    private fun saveCurrentWatchProgress() {
        val ch = currentChannel ?: return
        val p = player ?: return
        val dur = p.duration
        val pos = p.currentPosition
        if (dur > 0 && pos > 0) {
            val pct = ((pos * 100) / dur).toInt().coerceIn(0, 100)
            com.mytv.app.data.WatchProgressRepository.saveProgress(
                context = this,
                mediaId = ch.id,
                title = ch.title,
                category = ch.category,
                progressPercent = pct,
                positionMs = pos,
                durationMs = dur,
                coverImage = ch.logoUrl,
                streamUrl = currentStreamUrl ?: ch.streamUrl,
                isSeries = ch.category.contains("Série", ignoreCase = true)
            )
        }
    }

    override fun onPause() {
        super.onPause()
        progressHandler.removeCallbacks(progressRunnable)
        saveCurrentWatchProgress()
    }

    override fun onStop() {
        super.onStop()
        progressHandler.removeCallbacks(progressRunnable)
        saveCurrentWatchProgress()
        player?.stop()
        player?.clearMediaItems()
    }

    override fun onResume() {
        super.onResume()
        startProgressTracking()
        if (player != null && player?.currentMediaItem == null && currentChannel != null) {
            currentChannel?.let { startStream(it, currentStreamUrl) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressHandler.removeCallbacks(progressRunnable)
        hideHandler.removeCallbacks(hideRunnable)
        retryHandler.removeCallbacksAndMessages(null)
        player?.release()
        player = null
    }

    private var playerSettingsDialog: AlertDialog? = null
    private var subtitleDelayOffsetSeconds: Float = 0.0f

    fun showPlaybackSettingsDialog() {
        if (isFinishing || isDestroyed) return
        playerSettingsDialog?.dismiss()

        val dialogView = layoutInflater.inflate(R.layout.dialog_player_settings, null)
        val dialog = AlertDialog.Builder(this, R.style.Theme_MyTvApp_Dialog)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val rbAudioPt = dialogView.findViewById<RadioButton>(R.id.rb_audio_pt)
        val rbAudioEn = dialogView.findViewById<RadioButton>(R.id.rb_audio_en)

        val rbSubOff = dialogView.findViewById<RadioButton>(R.id.rb_sub_off)
        val rbSubPt = dialogView.findViewById<RadioButton>(R.id.rb_sub_pt)
        val rbSubEn = dialogView.findViewById<RadioButton>(R.id.rb_sub_en)

        val btnDelayMinus = dialogView.findViewById<Button>(R.id.btn_sub_delay_minus)
        val btnDelayPlus = dialogView.findViewById<Button>(R.id.btn_sub_delay_plus)
        val txtDelayVal = dialogView.findViewById<TextView>(R.id.txt_sub_delay_value)

        val rbSizeNormal = dialogView.findViewById<RadioButton>(R.id.rb_size_normal)
        val rbSizeLarge = dialogView.findViewById<RadioButton>(R.id.rb_size_large)
        val rbSizeSmall = dialogView.findViewById<RadioButton>(R.id.rb_size_small)

        val rbStyleWhite = dialogView.findViewById<RadioButton>(R.id.rb_style_white)
        val rbStyleYellow = dialogView.findViewById<RadioButton>(R.id.rb_style_yellow)
        val rbStyleBox = dialogView.findViewById<RadioButton>(R.id.rb_style_box)

        val btnExit = dialogView.findViewById<Button>(R.id.btn_settings_exit)

        txtDelayVal?.text = String.format(Locale.US, "%.1f s", subtitleDelayOffsetSeconds)

        // Seleção de Áudio
        rbAudioPt?.setOnClickListener {
            player?.trackSelectionParameters = player?.trackSelectionParameters
                ?.buildUpon()
                ?.setPreferredAudioLanguage("por")
                ?.build() ?: return@setOnClickListener
            Toast.makeText(this, "Áudio: Português", Toast.LENGTH_SHORT).show()
        }
        rbAudioEn?.setOnClickListener {
            player?.trackSelectionParameters = player?.trackSelectionParameters
                ?.buildUpon()
                ?.setPreferredAudioLanguage("eng")
                ?.build() ?: return@setOnClickListener
            Toast.makeText(this, "Áudio: Inglês", Toast.LENGTH_SHORT).show()
        }

        // Seleção de Legenda
        rbSubOff?.setOnClickListener {
            player?.trackSelectionParameters = player?.trackSelectionParameters
                ?.buildUpon()
                ?.setIgnoredTextSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                ?.setPreferredTextLanguage(null)
                ?.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                ?.build() ?: return@setOnClickListener
            playerView.subtitleView?.visibility = View.GONE
            Toast.makeText(this, "Legendas desativadas", Toast.LENGTH_SHORT).show()
        }
        rbSubPt?.setOnClickListener {
            playerView.subtitleView?.visibility = View.VISIBLE
            player?.trackSelectionParameters = player?.trackSelectionParameters
                ?.buildUpon()
                ?.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                ?.setPreferredTextLanguage("por")
                ?.build() ?: return@setOnClickListener
            Toast.makeText(this, "Legenda: Português", Toast.LENGTH_SHORT).show()
        }
        rbSubEn?.setOnClickListener {
            playerView.subtitleView?.visibility = View.VISIBLE
            player?.trackSelectionParameters = player?.trackSelectionParameters
                ?.buildUpon()
                ?.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                ?.setPreferredTextLanguage("eng")
                ?.build() ?: return@setOnClickListener
            Toast.makeText(this, "Legenda: Inglês", Toast.LENGTH_SHORT).show()
        }

        // Sincronia de Legendas (Delay)
        btnDelayMinus?.setOnClickListener {
            subtitleDelayOffsetSeconds = (subtitleDelayOffsetSeconds - 0.5f).coerceAtLeast(-10.0f)
            txtDelayVal?.text = String.format(Locale.US, "%.1f s", subtitleDelayOffsetSeconds)
        }
        btnDelayPlus?.setOnClickListener {
            subtitleDelayOffsetSeconds = (subtitleDelayOffsetSeconds + 0.5f).coerceAtMost(10.0f)
            txtDelayVal?.text = String.format(Locale.US, "%.1f s", subtitleDelayOffsetSeconds)
        }

        // Tamanho da legenda
        rbSizeNormal?.setOnClickListener {
            playerView.subtitleView?.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }
        rbSizeLarge?.setOnClickListener {
            playerView.subtitleView?.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        }
        rbSizeSmall?.setOnClickListener {
            playerView.subtitleView?.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }

        // Estilo de cores da legenda
        rbStyleWhite?.setOnClickListener {
            playerView.subtitleView?.setStyle(
                CaptionStyleCompat(
                    Color.WHITE,
                    Color.TRANSPARENT,
                    Color.TRANSPARENT,
                    CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                    Color.BLACK,
                    null
                )
            )
        }
        rbStyleYellow?.setOnClickListener {
            playerView.subtitleView?.setStyle(
                CaptionStyleCompat(
                    Color.YELLOW,
                    Color.TRANSPARENT,
                    Color.TRANSPARENT,
                    CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                    Color.BLACK,
                    null
                )
            )
        }
        rbStyleBox?.setOnClickListener {
            playerView.subtitleView?.setStyle(
                CaptionStyleCompat(
                    Color.WHITE,
                    Color.parseColor("#CC000000"),
                    Color.TRANSPARENT,
                    CaptionStyleCompat.EDGE_TYPE_NONE,
                    Color.BLACK,
                    null
                )
            )
        }

        btnExit?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        btnExit?.requestFocus()
        playerSettingsDialog = dialog
    }

    companion object {
        const val EXTRA_CHANNEL = "extra_channel"
        const val EXTRA_CHANNEL_LIST = "extra_channel_list"
        const val EXTRA_CHANNEL_INDEX = "extra_channel_index"
        const val EXTRA_PLAYBACK_OFFSET = "extra_playback_offset"
        const val EXTRA_PLAYBACK_TITLE = "extra_playback_title"
        private val STREAM_FALLBACKS = mapOf(
            // Corruptx FHD <-> HD Pairs
            "8735" to "8736",   // Discovery Channel FHD -> HD
            "8755" to "8756",   // Discovery Turbo FHD -> HD
            "8739" to "8740",   // Discovery H&H FHD -> HD
            "8745" to "8746",   // Discovery Science FHD -> HD
            "8748" to "8751",   // Discovery Theater FHD -> HD
            "8758" to "8759",   // Discovery World FHD -> HD
            "8932" to "8933",   // ID Investigacao FHD -> HD
            "8698" to "8699",   // Cartoon Network FHD -> HD
            "8687" to "8688",   // Cartoonito FHD -> HD
            "8742" to "8743",   // Discovery Kids FHD -> HD
            "8887" to "8888",   // Gloob FHD -> HD
            "9126" to "9127",   // Telecine Action FHD -> HD
            "9135" to "9136",   // Telecine Touch FHD -> HD
            "9131" to "9132",   // Telecine Pipoca FHD -> HD
            "9130" to "30138",  // Telecine Fun FHD -> HD
            "24534" to "9128",  // Telecine Cult FHD -> HD
            "8902" to "8903",   // HBO FHD -> HD
            "8897" to "8898",   // HBO 2 FHD -> HD
            "24592" to "11771", // HBO Plus FHD -> HD
            "8901" to "8900",   // HBO Family FHD -> HD
            "8912" to "8914",   // HBO Signature FHD -> HD
            "8904" to "8905",   // HBO Mundi FHD -> HD
            "24593" to "8911",  // HBO Pop FHD -> HD
            "13535" to "8916",  // HBO Xtreme FHD -> HD
            "9107" to "9111",   // SporTV FHD -> HD
            "9103" to "30123",  // SporTV 2 FHD -> HD
            "30124" to "9104",  // SporTV 3 HD -> FHD
            "9037" to "94194",  // Premiere Clubes FHD -> HD
            "9022" to "69028",  // Premiere 2 FHD -> HD
            "13577" to "69031", // Premiere 3 FHD -> HD
            "108295" to "108296", // Premiere 4 FHD -> HD
            "108303" to "108304", // Premiere 5 HD -> HD
            "108307" to "108309", // Premiere 6 FHD -> HD
            "108313" to "108315", // Premiere 7 FHD -> HD
            "13582" to "9036",  // Premiere 8 FHD -> HD
            "8661" to "8662",   // BandSports FHD -> HD
            "8714" to "8716",   // Combate FHD -> HD
            "304494" to "304496", // History FHD -> HD
            "304498" to "304500", // History 2 FHD -> HD
            "304491" to "304492", // Animal Planet FHD -> HD
            "9219" to "9220",   // Warner FHD -> HD
            "21682" to "9212",  // Universal TV FHD -> HD
            "8961" to "8962",   // Megapix FHD -> HD
            "9098" to "9099",   // Sony FHD -> HD
            "9115" to "9117",   // Studio Universal FHD -> HD
            "8708" to "8709"    // Cinemax FHD -> HD
        )

        private val CROSS_CDN_FALLBACKS = mapOf(
            // Formula 1 (UniTV CDN Band SP HD <-> Corruptx BandSports)
            "band-sp-hd" to "http://64.31.23.207:80/live/59471019/60849037/3460153.m3u8",
            "8661.m3u8" to "https://cdn1.uniredes.com.br/hls/band-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",

            // UniTV Discovery <-> Corruptx Discovery
            "discovery-hd" to "http://64.31.23.207:80/live/59471019/60849037/297936.m3u8",
            "8736.m3u8" to "https://cdn1.uniredes.com.br/hls/discovery-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
            "discovery-turbo-hd" to "http://64.31.23.207:80/live/59471019/60849037/297943.m3u8",
            "8756.m3u8" to "https://cdn1.uniredes.com.br/hls/discovery-turbo-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
            "discovery-hh" to "http://64.31.23.207:80/live/59471019/60849037/297940.m3u8",
            "8740.m3u8" to "https://cdn1.uniredes.com.br/hls/discovery-hh/index.m3u8?token=2d3232fe89d2b637a2093703",
            "tlc-hd" to "http://64.31.23.207:80/live/59471019/60849037/5378655.m3u8",
            "8754.m3u8" to "https://cdn1.uniredes.com.br/hls/tlc-hd/index.m3u8?token=2d3232fe89d2b637a2093703",

            // UniTV ESPN <-> Corruptx ESPN
            "espn-hd" to "http://64.31.23.207:80/live/59471019/60849037/3460183.m3u8",
            "598914.m3u8" to "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",

            // UniTV Globo SP <-> Corruptx Globo SP
            "globo-sp-hd" to "http://64.31.23.207:80/live/59471019/60849037/5676596.m3u8",
            "1168918.m3u8" to "https://cdn1.uniredes.com.br/hls/globo-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",

            // Kids
            "x1colegal.com" to "https://stmv2.srvif.com/nicktoons/nicktoons/playlist.m3u8"
        )

        fun getFallbackUrl(url: String): String? {
            // 1. Tenta transição para versão HD/FHD alternativa do mesmo canal (reduz bitrate e alivia TV Box)
            for ((fhdId, hdId) in STREAM_FALLBACKS) {
                if (url.contains("/$fhdId.m3u8")) {
                    return url.replace("/$fhdId.m3u8", "/$hdId.m3u8")
                }
                if (url.contains("/$hdId.m3u8")) {
                    return url.replace("/$hdId.m3u8", "/$fhdId.m3u8")
                }
                if (url.contains("/$fhdId.ts")) {
                    return url.replace("/$fhdId.ts", "/$hdId.ts")
                }
                if (url.contains("/$hdId.ts")) {
                    return url.replace("/$hdId.ts", "/$fhdId.ts")
                }
            }
            // 2. Alternância entre CDNs cruzadas (UniTV <-> Corruptx)
            for ((key, target) in CROSS_CDN_FALLBACKS) {
                if (url.contains(key)) {
                    return target
                }
            }
            // 3. Fallback inteligente de extensao m3u8 <-> ts
            if (url.endsWith(".m3u8")) {
                return url.replace(".m3u8", ".ts")
            } else if (url.endsWith(".ts")) {
                return url.replace(".ts", ".m3u8")
            }
            return null
        }}
}
