package com.mytv.app

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import com.mytv.app.model.Channel

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var overlayView: View
    private lateinit var bufferingProgress: ProgressBar
    private lateinit var btnBack: Button

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

        // Inicializa controles do Card de Erro
        errorLayout = findViewById(R.id.player_error_layout)
        txtErrorTitle = findViewById(R.id.txt_player_error_title)
        txtErrorMessage = findViewById(R.id.txt_player_error_message)
        txtErrorCountdown = findViewById(R.id.txt_player_error_countdown)
        btnErrorRetry = findViewById(R.id.btn_player_error_retry)
        btnErrorBack = findViewById(R.id.btn_player_error_back)

        btnErrorRetry.setOnClickListener {
            retryCount = 0
            currentChannel?.let { startStream(it) }
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

        this.currentChannel = channel

        findViewById<TextView>(R.id.txt_player_channel_title).text = channel.title
        findViewById<TextView>(R.id.txt_player_channel_category).text =
            "${channel.category} • Transmissão em Alta Definição"

        // Exibe o EPG (Guia de Programação) no player
        val enriched = if (channel.currentProgram == null) {
            com.mytv.app.data.EpgRepository.enrichChannel(channel)
        } else {
            channel
        }

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

        btnBack.setOnClickListener {
            finish()
        }

        startStream(channel)
        showOverlayTemporarily()
    }

    private fun startStream(channel: Channel) {
        // Limpa estado de erro anterior
        retryHandler.removeCallbacksAndMessages(null)
        errorLayout.visibility = View.GONE
        bufferingProgress.visibility = View.VISIBLE

        player?.release()
        player = null

        val httpFactory = androidx.media3.datasource.DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setDefaultRequestProperties(mapOf(
                "Accept" to "*/*",
                "Connection" to "keep-alive"
            ))

        val dataSourceFactory = DefaultDataSource.Factory(this, httpFactory)

        val exo = ExoPlayer.Builder(this).build().apply {
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

        val mediaSource = if (channel.streamUrl.contains(".m3u8")) {
            HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(MediaItem.fromUri(channel.streamUrl))
        } else {
            androidx.media3.exoplayer.source.ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(channel.streamUrl))
        }

        exo.setMediaSource(mediaSource)
        exo.prepare()

        playerView.player = exo
        this.player = exo
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

            val runnable = Runnable {
                if (!isFinishing && !isDestroyed) {
                    startStream(channel)
                }
            }
            retryRunnable = runnable
            retryHandler.postDelayed(runnable, 3000)
        } else {
            txtErrorMessage.text = "O sinal deste canal está temporariamente indisponível na operadora de origem. Você pode tentar novamente ou escolher outro canal na grade."
            txtErrorCountdown.visibility = View.GONE
            btnErrorRetry.requestFocus()
        }
    }

    private fun showOverlayTemporarily() {
        overlayView.alpha = 1f
        overlayView.visibility = View.VISIBLE
        btnBack.requestFocus()

        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 4500)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
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
    }
}
