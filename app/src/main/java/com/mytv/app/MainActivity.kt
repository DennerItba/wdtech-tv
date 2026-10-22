package com.mytv.app

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.adapter.CategorySidebarAdapter
import com.mytv.app.adapter.ChannelListAdapter
import com.mytv.app.data.ChannelRepository
import com.mytv.app.data.EpgRepository
import com.mytv.app.data.UniTvCatalogManager
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory

import android.widget.ImageView
import android.widget.ProgressBar
import coil.load
import coil.transform.RoundedCornersTransformation
import com.mytv.app.adapter.VodListAdapter
import com.mytv.app.adapter.SeasonAdapter
import com.mytv.app.adapter.EpisodeAdapter
import com.mytv.app.data.VodRepository
import com.mytv.app.model.VodItem
import com.mytv.app.model.VodSeason
import com.mytv.app.model.VodEpisode

enum class AppMode {
    LIVE_TV,
    MOVIES,
    SERIES
}

class MainActivity : AppCompatActivity() {

    private var currentAppMode = AppMode.LIVE_TV
    private lateinit var btnTabLive: Button
    private lateinit var btnTabMovies: Button
    private lateinit var btnTabSeries: Button
    private lateinit var rvVodList: RecyclerView
    private lateinit var vodAdapter: VodListAdapter
    private lateinit var layoutVodPreview: View
    private lateinit var imgVodPreviewPoster: ImageView
    private lateinit var txtVodPreviewTitle: TextView
    private lateinit var txtVodPreviewMeta: TextView
    private lateinit var txtVodPreviewPlot: TextView
    private lateinit var btnVodAction: Button
    private var currentVodItem: VodItem? = null


    private lateinit var rvCategorySidebar: RecyclerView
    private lateinit var rvChannelList: RecyclerView
    private lateinit var categoryAdapter: CategorySidebarAdapter
    private lateinit var channelAdapter: ChannelListAdapter

    private lateinit var playerView: PlayerView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var txtPreviewTitle: TextView
    private lateinit var txtPreviewInfo: TextView
    private lateinit var txtPreviewPlayback: TextView
    private lateinit var txtSelectedCategoryTitle: TextView
    private lateinit var txtCategoryBadge: TextView
    private lateinit var txtHeaderBreadcrumb: TextView
    private lateinit var btnFullscreen: ImageButton

    // Visualização EPG (Guia de Programação)
    private lateinit var txtEpgNowTitle: TextView
    private lateinit var txtEpgNowTime: TextView
    private lateinit var pbEpgProgress: android.widget.ProgressBar
    private lateinit var txtEpgSynopsis: TextView
    private lateinit var txtEpgNext: TextView

    private var exoPlayer: ExoPlayer? = null
    private var allCategories: List<ChannelCategory> = emptyList()
    private var currentChannel: Channel? = null
    private var selectedCategory: ChannelCategory? = null

    private var isAdultUnlocked: Boolean = false
    private val DEFAULT_PIN = "0000"

    private val switchHandler = Handler(Looper.getMainLooper())
    private var pendingChannelSwitch: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Proteção global contra encerramentos inesperados em TV Boxes
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("WDTECH_TV", "Erro capturado com segurança [${thread.name}]: ${throwable.message}", throwable)
        }

        setContentView(R.layout.activity_main)

        rvCategorySidebar = findViewById(R.id.rv_category_sidebar)
        rvChannelList = findViewById(R.id.rv_channel_list)
        playerView = findViewById(R.id.player_preview_view)
        loadingLayout = findViewById(R.id.preview_loading_layout)
        txtPreviewTitle = findViewById(R.id.txt_preview_title)
        txtPreviewInfo = findViewById(R.id.txt_preview_info)
        txtPreviewPlayback = findViewById(R.id.txt_preview_playback)
        txtSelectedCategoryTitle = findViewById(R.id.txt_selected_category_title)
        txtCategoryBadge = findViewById(R.id.txt_category_badge)
        txtHeaderBreadcrumb = findViewById(R.id.txt_header_breadcrumb)
        btnFullscreen = findViewById(R.id.btn_fullscreen)

        btnTabLive = findViewById(R.id.btn_tab_live)
        btnTabMovies = findViewById(R.id.btn_tab_movies)
        btnTabSeries = findViewById(R.id.btn_tab_series)
        rvVodList = findViewById(R.id.rv_vod_list)
        layoutVodPreview = findViewById(R.id.layout_vod_preview)
        imgVodPreviewPoster = findViewById(R.id.img_vod_preview_poster)
        txtVodPreviewTitle = findViewById(R.id.txt_vod_preview_title)
        txtVodPreviewMeta = findViewById(R.id.txt_vod_preview_meta)
        txtVodPreviewPlot = findViewById(R.id.txt_vod_preview_plot)
        btnVodAction = findViewById(R.id.btn_vod_action)

        setupModeTabs()
        setupVodList()

        txtEpgNowTitle = findViewById(R.id.txt_epg_now_title)
        txtEpgNowTime = findViewById(R.id.txt_epg_now_time)
        pbEpgProgress = findViewById(R.id.pb_epg_progress)
        txtEpgSynopsis = findViewById(R.id.txt_epg_synopsis)
        txtEpgNext = findViewById(R.id.txt_epg_next)

        setupExitActions()
        setupPlayer()
                loadData()

        // Atualização dinâmica em tempo real da grade de Jogos do Dia com proteção de concorrência
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val freshJogos = ChannelRepository.fetchJogosDoDia()
                if (freshJogos.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        if (!isFinishing && !isDestroyed) {
                            val repoCategories = EpgRepository.enrichCategories(ChannelRepository.getCategories())
                            val safeChannels = repoCategories.filter { !it.isLocked }.flatMap { it.channels }
                            val masterCategory = ChannelCategory(
                                id = "cat_all",
                                name = "Todos os Canais",
                                isLocked = false,
                                channels = safeChannels
                            )
                            allCategories = listOf(masterCategory) + repoCategories
                            if (!rvCategorySidebar.isComputingLayout) {
                                categoryAdapter.updateCategories(allCategories)
                            }

                            if (selectedCategory?.id == "cat_jogos_dia") {
                                val curCat = allCategories.find { it.id == "cat_jogos_dia" }
                                if (curCat != null) {
                                    displayCategoryChannels(curCat)
                                }
                            } else if (selectedCategory?.id == "cat_all" && !rvChannelList.isComputingLayout) {
                                channelAdapter.updateChannels(safeChannels)
                            }
                        }
                    }
                }
            } catch (t: Throwable) {
                android.util.Log.e("WDTECH_TV", "Falha ao sincronizar Jogos do Dia", t)
            }
        }

        // Verificação automática de atualizações em segundo plano após inicialização do sistema
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                com.mytv.app.utils.AppUpdateManager.checkForUpdates(this, isManualCheck = false)
            }
        }, 3000)
    }

    private fun loadData() {
        val repoCategories = EpgRepository.enrichCategories(ChannelRepository.getCategories())
        
        // Filtra canais bloqueados da lista geral se não estiver desbloqueado
        val safeChannels = repoCategories.filter { !it.isLocked }.flatMap { it.channels }

        val masterCategory = ChannelCategory(
            id = "cat_all",
            name = "Todos os Canais",
            isLocked = false,
            channels = safeChannels
        )

        allCategories = listOf(masterCategory) + repoCategories

        // 1. Configura a Barra Lateral de Categorias
        rvCategorySidebar.setHasFixedSize(true)
        rvCategorySidebar.setItemViewCacheSize(20)
        rvCategorySidebar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        categoryAdapter = CategorySidebarAdapter(
            categories = allCategories,
            onCategoryFocused = { category ->
                handleCategorySelection(category, autoFocusChannels = false)
            },
            onCategoryClicked = { category ->
                handleCategorySelection(category, autoFocusChannels = true)
            },
            onNavigateUpToTabs = {
                focusActiveTab()
            }
        )
        rvCategorySidebar.adapter = categoryAdapter

        // 2. Configura a Lista de Canais do Centro
        rvChannelList.setHasFixedSize(true)
        rvChannelList.setItemViewCacheSize(20)
        rvChannelList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        channelAdapter = ChannelListAdapter(
            channels = safeChannels,
            onChannelSelected = { channel ->
                onChannelFocused(channel)
            },
            onChannelClicked = { channel ->
                openFullScreenPlayer(channel)
            },
            onNavigateToCategories = {
                focusCurrentCategory()
            },
            onNavigateToPlayer = {
                currentChannel?.let { openFullScreenPlayer(it) }
            },
            onNavigateUpToTabs = {
                focusActiveTab()
            }
        )
        rvChannelList.adapter = channelAdapter

        // Inicia na categoria "Todos os Canais"
        displayCategoryChannels(masterCategory)

        // Inicializa o primeiro canal
        safeChannels.firstOrNull()?.let { first ->
            currentChannel = first
            updateChannelInfo(first)
            playChannelPreview(first)
        }

        // Foco inicial garantido na primeira categoria ao abrir o aplicativo
        rvCategorySidebar.post {
            val vh = rvCategorySidebar.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus()
        }

        // 3. Sincronização Dinâmica em Segundo Plano via UNITV_BASE_URL
        lifecycleScope.launch {
            val updated = UniTvCatalogManager.syncCatalog(this@MainActivity)
            if (updated != null && updated.isNotEmpty()) {
                applyUpdatedCatalog(updated)
            }
        }
    }

    private fun applyUpdatedCatalog(newCategories: List<ChannelCategory>) {
        val enrichedCategories = EpgRepository.enrichCategories(newCategories)
        val safeChannels = enrichedCategories.filter { !it.isLocked }.flatMap { it.channels }
        val masterCategory = ChannelCategory(
            id = "cat_all",
            name = "Todos os Canais",
            isLocked = false,
            channels = safeChannels
        )

        allCategories = listOf(masterCategory) + enrichedCategories
        categoryAdapter.updateCategories(allCategories)

        val currentCat = selectedCategory
        if (currentCat != null) {
            val refreshed = allCategories.find { it.id == currentCat.id } ?: masterCategory
            displayCategoryChannels(refreshed)
        } else {
            displayCategoryChannels(masterCategory)
        }
    }

    private fun handleCategorySelection(category: ChannelCategory, autoFocusChannels: Boolean) {
        selectedCategory = category

        if (category.isLocked && !isAdultUnlocked) {
            // Categoria bloqueada por PIN (+18)
            txtSelectedCategoryTitle.text = "🔒 ${category.name}"
            txtCategoryBadge.text = "BLOQUEADO"
            txtHeaderBreadcrumb.text = "WDTECH TV • CONTEÚDO ADULTO (+18)"
            
            pendingChannelSwitch?.let { switchHandler.removeCallbacks(it) }
            exoPlayer?.stop()
            exoPlayer?.clearMediaItems()
            loadingLayout.visibility = View.GONE

            channelAdapter.updateChannels(emptyList())
            vodAdapter.updateItems(emptyList())
            txtPreviewTitle.text = "🔒 Conteúdo Adulto Bloqueado (+18)"
            txtPreviewInfo.text = "Pressione OK ou selecione para digitar o PIN de liberação"
            txtVodPreviewTitle.text = "🔒 Conteúdo Adulto Bloqueado (+18)"
            txtVodPreviewMeta.text = "PIN padrão: 0000"
            txtVodPreviewPlot.text = "Este conteúdo é protegido pelo controle dos pais. Pressione OK no botão abaixo ou no controle para desbloquear."
            btnVodAction.text = "🔒 Desbloquear com PIN (OK)"
            btnVodAction.setOnClickListener { showPinDialog(category) }

            if (autoFocusChannels) {
                showPinDialog(category)
            }
            return
        }

        when (currentAppMode) {
            AppMode.LIVE_TV -> {
                displayCategoryChannels(category)
                if (autoFocusChannels) {
                    focusFirstChannel()
                }
            }
            AppMode.MOVIES -> {
                txtSelectedCategoryTitle.text = category.name
                txtCategoryBadge.text = "..."
                txtHeaderBreadcrumb.text = "WDTECH TV • FILMES • ${category.name.uppercase()}"
                lifecycleScope.launch {
                    val movies = VodRepository.getMoviesByCategory(category.id, category.name)
                    if (currentAppMode == AppMode.MOVIES && selectedCategory?.id == category.id) {
                        txtCategoryBadge.text = movies.size.toString()
                        vodAdapter.updateItems(movies)
                        if (movies.isNotEmpty()) {
                            currentVodItem = movies.first()
                            updateVodPreview(movies.first())
                            if (autoFocusChannels) {
                                focusFirstVodItem()
                            }
                        }
                    }
                }
            }
            AppMode.SERIES -> {
                txtSelectedCategoryTitle.text = category.name
                txtCategoryBadge.text = "..."
                txtHeaderBreadcrumb.text = "WDTECH TV • SÉRIES • ${category.name.uppercase()}"
                lifecycleScope.launch {
                    val series = VodRepository.getSeriesByCategory(category.id, category.name)
                    if (currentAppMode == AppMode.SERIES && selectedCategory?.id == category.id) {
                        txtCategoryBadge.text = series.size.toString()
                        vodAdapter.updateItems(series)
                        if (series.isNotEmpty()) {
                            currentVodItem = series.first()
                            updateVodPreview(series.first())
                            if (autoFocusChannels) {
                                focusFirstVodItem()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun focusFirstChannel() {
        if (channelAdapter.itemCount == 0) return
        rvChannelList.stopScroll()
        rvChannelList.scrollToPosition(0)
        rvChannelList.post {
            val vh = rvChannelList.findViewHolderForAdapterPosition(0)
            if (vh != null) {
                vh.itemView.requestFocus()
            } else {
                val v = rvChannelList.layoutManager?.findViewByPosition(0)
                if (v != null) {
                    v.requestFocus()
                } else {
                    rvChannelList.postDelayed({
                        val retryVh = rvChannelList.findViewHolderForAdapterPosition(0)
                        retryVh?.itemView?.requestFocus()
                            ?: rvChannelList.layoutManager?.findViewByPosition(0)?.requestFocus()
                    }, 50)
                }
            }
        }
    }

    private fun focusCurrentCategory() {
        val pos = categoryAdapter.getSelectedPosition()
        rvCategorySidebar.scrollToPosition(pos)
        rvCategorySidebar.post {
            val vh = rvCategorySidebar.findViewHolderForAdapterPosition(pos)
            if (vh != null) {
                vh.itemView.requestFocus()
            } else {
                val v = rvCategorySidebar.layoutManager?.findViewByPosition(pos)
                if (v != null) {
                    v.requestFocus()
                } else {
                    rvCategorySidebar.postDelayed({
                        val retryVh = rvCategorySidebar.findViewHolderForAdapterPosition(pos)
                        retryVh?.itemView?.requestFocus()
                            ?: rvCategorySidebar.layoutManager?.findViewByPosition(pos)?.requestFocus()
                    }, 50)
                }
            }
        }
    }

    private fun displayCategoryChannels(category: ChannelCategory) {
        txtSelectedCategoryTitle.text = category.name
        txtCategoryBadge.text = category.channels.size.toString()
        txtHeaderBreadcrumb.text = "WDTECH TV • ${category.name.uppercase()}"

        channelAdapter.updateChannels(category.channels)
        rvChannelList.scrollToPosition(0)

        if (category.channels.isNotEmpty() && (currentChannel == null || !category.channels.contains(currentChannel))) {
            val first = category.channels[0]
            currentChannel = first
            updateChannelInfo(first)
            playChannelPreview(first)
        }
    }

    private fun showPinDialog(category: ChannelCategory) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_pin_lock)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dot1 = dialog.findViewById<TextView>(R.id.pin_dot_1)
        val dot2 = dialog.findViewById<TextView>(R.id.pin_dot_2)
        val dot3 = dialog.findViewById<TextView>(R.id.pin_dot_3)
        val dot4 = dialog.findViewById<TextView>(R.id.pin_dot_4)
        val txtError = dialog.findViewById<TextView>(R.id.txt_pin_error)
        val dots = listOf(dot1, dot2, dot3, dot4)

        var enteredPin = ""

        fun updateDots() {
            for (i in 0 until 4) {
                if (i < enteredPin.length) {
                    dots[i].text = "•"
                    dots[i].setTextColor(Color.parseColor("#00E5FF"))
                } else {
                    dots[i].text = "—"
                    dots[i].setTextColor(Color.parseColor("#5B6E8C"))
                }
            }
        }

        fun checkPin() {
            val prefs = getSharedPreferences("mytv_prefs", Context.MODE_PRIVATE)
            val correctPin = prefs.getString("adult_pin", DEFAULT_PIN) ?: DEFAULT_PIN

            if (enteredPin == correctPin) {
                isAdultUnlocked = true
                txtError.visibility = View.INVISIBLE
                Toast.makeText(this, "Acesso aos Canais e Conteúdos Adultos Liberado!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                handleCategorySelection(category, autoFocusChannels = true)
            } else {
                txtError.visibility = View.VISIBLE
                enteredPin = ""
                updateDots()
            }
        }

        fun appendDigit(digit: String) {
            if (enteredPin.length < 4) {
                enteredPin += digit
                updateDots()
                if (enteredPin.length == 4) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        checkPin()
                    }, 150)
                }
            }
        }

        fun removeDigit() {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.substring(0, enteredPin.length - 1)
                txtError.visibility = View.INVISIBLE
                updateDots()
            }
        }

        // Mapeia cliques nos botões numéricos
        dialog.findViewById<Button>(R.id.btn_num_1).setOnClickListener { appendDigit("1") }
        dialog.findViewById<Button>(R.id.btn_num_2).setOnClickListener { appendDigit("2") }
        dialog.findViewById<Button>(R.id.btn_num_3).setOnClickListener { appendDigit("3") }
        dialog.findViewById<Button>(R.id.btn_num_4).setOnClickListener { appendDigit("4") }
        dialog.findViewById<Button>(R.id.btn_num_5).setOnClickListener { appendDigit("5") }
        dialog.findViewById<Button>(R.id.btn_num_6).setOnClickListener { appendDigit("6") }
        dialog.findViewById<Button>(R.id.btn_num_7).setOnClickListener { appendDigit("7") }
        dialog.findViewById<Button>(R.id.btn_num_8).setOnClickListener { appendDigit("8") }
        dialog.findViewById<Button>(R.id.btn_num_9).setOnClickListener { appendDigit("9") }
        dialog.findViewById<Button>(R.id.btn_num_0).setOnClickListener { appendDigit("0") }
        dialog.findViewById<Button>(R.id.btn_num_clear).setOnClickListener { removeDigit() }
        dialog.findViewById<Button>(R.id.btn_pin_cancel).setOnClickListener { dialog.dismiss() }

        // Suporte para pressionar números direto no teclado do controle remoto
        dialog.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_0 -> { appendDigit("0"); true }
                    KeyEvent.KEYCODE_1 -> { appendDigit("1"); true }
                    KeyEvent.KEYCODE_2 -> { appendDigit("2"); true }
                    KeyEvent.KEYCODE_3 -> { appendDigit("3"); true }
                    KeyEvent.KEYCODE_4 -> { appendDigit("4"); true }
                    KeyEvent.KEYCODE_5 -> { appendDigit("5"); true }
                    KeyEvent.KEYCODE_6 -> { appendDigit("6"); true }
                    KeyEvent.KEYCODE_7 -> { appendDigit("7"); true }
                    KeyEvent.KEYCODE_8 -> { appendDigit("8"); true }
                    KeyEvent.KEYCODE_9 -> { appendDigit("9"); true }
                    KeyEvent.KEYCODE_DEL -> { removeDigit(); true }
                    else -> false
                }
            } else {
                false
            }
        }

        dialog.show()
    }

    private fun onChannelFocused(channel: Channel) {
        currentChannel = channel
        updateChannelInfo(channel)

        pendingChannelSwitch?.let { switchHandler.removeCallbacks(it) }
        
        // Interrompe imediatamente decodificação anterior para evitar gargalo e congelamento na TV Box
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        loadingLayout.visibility = View.GONE

        val runnable = Runnable {
            if (!isFinishing && !isDestroyed) {
                playChannelPreview(channel)
            }
        }
        pendingChannelSwitch = runnable
        // Debounce de 1200ms: navegação ultra suave pelo controle remoto sem sobrecarregar o hardware
        switchHandler.postDelayed(runnable, 1200)
    }

    private fun updateChannelInfo(channel: Channel) {
        txtPreviewTitle.text = "${channel.number} • ${channel.title}"
        txtPreviewInfo.text = "${channel.category} • ${channel.quality} • Ao Vivo"

        val enriched = if (channel.currentProgram == null) {
            EpgRepository.enrichChannel(channel)
        } else {
            channel
        }

        txtPreviewPlayback.visibility = if (enriched.hasPlayback) View.VISIBLE else View.GONE

        val epg = enriched.currentProgram
        if (epg != null) {
            txtEpgNowTitle.text = epg.title
            txtEpgNowTime.text = "${epg.startTime} - ${epg.endTime} (${epg.getRemainingMinutes()}m rest.)"
            pbEpgProgress.progress = epg.getProgressPercent()
            txtEpgSynopsis.text = epg.synopsis.ifEmpty { "Transmissão contínua em alta definição." }
        } else {
            txtEpgNowTitle.text = "Transmissão Ao Vivo"
            txtEpgNowTime.text = "Programação Regular"
            pbEpgProgress.progress = 50
            txtEpgSynopsis.text = "Acompanhe a programação oficial em tempo real."
        }

        val nextEpg = enriched.nextProgram
        if (nextEpg != null) {
            txtEpgNext.text = "▶ A Seguir (${nextEpg.startTime}): ${nextEpg.title}"
        } else {
            txtEpgNext.text = "▶ A Seguir: Grade Noturna"
        }

        // Busca assincronamente o EPG ao vivo direto do servidor para máxima precisão
        lifecycleScope.launch {
            val livePrograms = EpgRepository.fetchLiveEpg(enriched)
            if (livePrograms != null && currentChannel?.id == enriched.id && !isFinishing && !isDestroyed) {
                val liveNow = livePrograms.first
                val liveNext = livePrograms.second
                txtEpgNowTitle.text = liveNow.title
                txtEpgNowTime.text = "${liveNow.startTime} - ${liveNow.endTime} (${liveNow.getRemainingMinutes()}m rest.)"
                pbEpgProgress.progress = liveNow.getProgressPercent()
                txtEpgSynopsis.text = liveNow.synopsis.ifEmpty { "Transmissão contínua em alta definição." }
                if (liveNext != null) {
                    txtEpgNext.text = "▶ A Seguir (${liveNext.startTime}): ${liveNext.title}"
                }
                val selPos = channelAdapter.getSelectedPosition()
                if (selPos in 0 until channelAdapter.itemCount && !rvChannelList.isComputingLayout) {
                    channelAdapter.notifyItemChanged(selPos)
                }
            }
        }
    }

    private fun setupPlayer() {
        val player = ExoPlayer.Builder(this).build()
        player.playWhenReady = true
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_BUFFERING) {
                    loadingLayout.visibility = View.VISIBLE
                } else if (state == Player.STATE_READY) {
                    loadingLayout.visibility = View.GONE
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                loadingLayout.visibility = View.GONE
            }
        })

        playerView.player = player
        this.exoPlayer = player

        btnFullscreen.setOnClickListener {
            currentChannel?.let { openFullScreenPlayer(it) }
        }
        findViewById<View>(R.id.player_preview_container).setOnClickListener {
            currentChannel?.let { openFullScreenPlayer(it) }
        }
    }

    private fun playChannelPreview(channel: Channel) {
        val player = exoPlayer ?: return
        if (isFinishing || isDestroyed) return
        
        loadingLayout.visibility = View.VISIBLE

        try {
            player.stop()
            player.clearMediaItems()

            val httpFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("IPTVSmartersPro")
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(10000)
                .setReadTimeoutMs(10000)
                .setDefaultRequestProperties(mapOf(
                    "Accept" to "*/*",
                    "Connection" to "keep-alive"
                ))

            val dataSourceFactory = DefaultDataSource.Factory(this, httpFactory)
            val mediaItem = MediaItem.fromUri(channel.streamUrl)

            val mediaSource = if (channel.streamUrl.contains(".m3u8")) {
                HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(mediaItem)
            } else {
                androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory)
                    .createMediaSource(mediaItem)
            }

            player.setMediaSource(mediaSource)
            player.prepare()
        } catch (t: Throwable) {
            loadingLayout.visibility = View.GONE
        }
    }

    private fun openFullScreenPlayer(channel: Channel) {
        exoPlayer?.pause()
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_CHANNEL, channel)
        }
        startActivity(intent)
    }

    private fun setupExitActions() {
        findViewById<Button>(R.id.btn_top_exit).setOnClickListener {
            showExitDialog()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (rvChannelList.hasFocus()) {
                    focusCurrentCategory()
                } else {
                    showExitDialog()
                }
            }
        })
    }

    private fun showExitDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_exit)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<Button>(R.id.btn_cancel_exit).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_confirm_exit).setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        exoPlayer?.play()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        pendingChannelSwitch?.let { switchHandler.removeCallbacks(it) }
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun setupModeTabs() {
        btnTabLive.isSelected = true
        btnTabMovies.isSelected = false
        btnTabSeries.isSelected = false

        btnTabLive.setOnClickListener { switchAppMode(AppMode.LIVE_TV) }
        btnTabMovies.setOnClickListener { switchAppMode(AppMode.MOVIES) }
        btnTabSeries.setOnClickListener { switchAppMode(AppMode.SERIES) }

        val tabKeyListener = View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        focusCurrentCategory()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        v.performClick()
                        true
                    }
                    else -> false
                }
            } else false
        }
        btnTabLive.setOnKeyListener(tabKeyListener)
        btnTabMovies.setOnKeyListener(tabKeyListener)
        btnTabSeries.setOnKeyListener(tabKeyListener)
    }

    private fun setupVodList() {
        rvVodList.setHasFixedSize(true)
        rvVodList.setItemViewCacheSize(20)
        rvVodList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        vodAdapter = VodListAdapter(
            items = emptyList(),
            onItemSelected = { item ->
                currentVodItem = item
                updateVodPreview(item)
            },
            onItemClicked = { item ->
                handleVodClick(item)
            },
            onNavigateToCategories = {
                focusCurrentCategory()
            },
            onNavigateToDetails = {
                btnVodAction.requestFocus()
            },
            onNavigateUpToTabs = {
                focusActiveTab()
            }
        )
        rvVodList.adapter = vodAdapter

        btnVodAction.setOnClickListener {
            currentVodItem?.let { handleVodClick(it) }
        }
    }

    private fun switchAppMode(mode: AppMode) {
        if (currentAppMode == mode) return
        currentAppMode = mode

        btnTabLive.isSelected = (mode == AppMode.LIVE_TV)
        btnTabMovies.isSelected = (mode == AppMode.MOVIES)
        btnTabSeries.isSelected = (mode == AppMode.SERIES)

        when (mode) {
            AppMode.LIVE_TV -> {
                rvChannelList.visibility = View.VISIBLE
                rvVodList.visibility = View.GONE
                layoutVodPreview.visibility = View.GONE
                playerView.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "WDTECH TV • AO VIVO"

                categoryAdapter.updateCategories(allCategories)
                val cat = selectedCategory ?: allCategories.firstOrNull()
                cat?.let { displayCategoryChannels(it) }
                currentChannel?.let { playChannelPreview(it) }
            }
            AppMode.MOVIES -> {
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
                rvChannelList.visibility = View.GONE
                rvVodList.visibility = View.VISIBLE
                layoutVodPreview.visibility = View.VISIBLE
                playerView.visibility = View.GONE
                txtHeaderBreadcrumb.text = "WDTECH TV • CATÁLOGO DE FILMES"
                txtSelectedCategoryTitle.text = "Carregando Filmes..."
                txtCategoryBadge.text = "..."

                lifecycleScope.launch {
                    val vodCats = VodRepository.getMovieCategories()
                    val catList = vodCats.map { vc ->
                        ChannelCategory(
                            id = vc.id,
                            name = vc.name,
                            isLocked = vc.isAdult,
                            channels = emptyList()
                        )
                    }
                    categoryAdapter.updateCategories(catList)
                    if (catList.isNotEmpty()) {
                        handleCategorySelection(catList.first(), autoFocusChannels = false)
                    }
                }
            }
            AppMode.SERIES -> {
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
                rvChannelList.visibility = View.GONE
                rvVodList.visibility = View.VISIBLE
                layoutVodPreview.visibility = View.VISIBLE
                playerView.visibility = View.GONE
                txtHeaderBreadcrumb.text = "WDTECH TV • CATÁLOGO DE SÉRIES"
                txtSelectedCategoryTitle.text = "Carregando Séries..."
                txtCategoryBadge.text = "..."

                lifecycleScope.launch {
                    val seriesCats = VodRepository.getSeriesCategories()
                    val catList = seriesCats.map { sc ->
                        ChannelCategory(
                            id = sc.id,
                            name = sc.name,
                            isLocked = sc.isAdult,
                            channels = emptyList()
                        )
                    }
                    categoryAdapter.updateCategories(catList)
                    if (catList.isNotEmpty()) {
                        handleCategorySelection(catList.first(), autoFocusChannels = false)
                    }
                }
            }
        }
    }

    private fun updateVodPreview(item: VodItem) {
        txtVodPreviewTitle.text = item.name
        val meta = buildString {
            append(item.getFormattedRating())
            if (!item.year.isNullOrEmpty()) append(" • ").append(item.year)
            if (!item.genre.isNullOrEmpty()) append(" • ").append(item.genre)
            if (item.isSeries) append(" • Série Completa") else append(" • Filme FHD")
        }
        txtVodPreviewMeta.text = meta
        txtVodPreviewPlot.text = item.plot ?: "Sem sinopse disponível para este título."
        btnVodAction.text = if (item.isSeries) "📺 Ver Temporadas & Episódios (OK)" else "▶ Assistir Filme (OK)"

        if (!item.posterUrl.isNullOrEmpty()) {
            imgVodPreviewPoster.load(item.posterUrl) {
                crossfade(true)
                transformations(RoundedCornersTransformation(10f))
            }
        } else {
            imgVodPreviewPoster.setImageResource(R.drawable.logo)
        }
    }

    private fun handleVodClick(item: VodItem) {
        if (item.isSeries) {
            showSeriesEpisodesDialog(item)
        } else {
            openFullScreenPlayer(item.toChannel())
        }
    }

    private fun focusFirstVodItem() {
        if (vodAdapter.itemCount == 0) return
        rvVodList.stopScroll()
        rvVodList.scrollToPosition(0)
        rvVodList.post {
            val vh = rvVodList.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus()
                ?: rvVodList.layoutManager?.findViewByPosition(0)?.requestFocus()
        }
    }

    private fun showSeriesEpisodesDialog(series: VodItem) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_series_episodes)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            (resources.displayMetrics.heightPixels * 0.88).toInt()
        )

        val txtTitle = dialog.findViewById<TextView>(R.id.txt_dialog_series_title)
        val txtGenre = dialog.findViewById<TextView>(R.id.txt_dialog_series_genre)
        val txtPlot = dialog.findViewById<TextView>(R.id.txt_dialog_series_plot)
        val imgCover = dialog.findViewById<ImageView>(R.id.img_dialog_series_cover)
        val btnClose = dialog.findViewById<Button>(R.id.btn_dialog_close)
        val pbLoading = dialog.findViewById<ProgressBar>(R.id.pb_dialog_loading)
        val rvSeasons = dialog.findViewById<RecyclerView>(R.id.rv_dialog_seasons)
        val rvEpisodes = dialog.findViewById<RecyclerView>(R.id.rv_dialog_episodes)

        txtTitle.text = series.name
        txtGenre.text = "${series.getFormattedRating()} • ${series.genre ?: "Série Completa"}"
        txtPlot.text = series.plot ?: "Sinopse não disponível."

        if (!series.posterUrl.isNullOrEmpty()) {
            imgCover.load(series.posterUrl) {
                crossfade(true)
                transformations(RoundedCornersTransformation(8f))
            }
        }

        btnClose.setOnClickListener { dialog.dismiss() }

        rvSeasons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEpisodes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val episodeAdapter = EpisodeAdapter(emptyList()) { ep ->
            dialog.dismiss()
            openFullScreenPlayer(ep.toChannel(series.name, series.posterUrl))
        }
        rvEpisodes.adapter = episodeAdapter

        val seasonAdapter = SeasonAdapter(emptyList()) { season ->
            episodeAdapter.updateEpisodes(season.episodes)
            rvEpisodes.scrollToPosition(0)
        }
        rvSeasons.adapter = seasonAdapter

        pbLoading.visibility = View.VISIBLE
        lifecycleScope.launch {
            val seasons = VodRepository.getSeriesInfo(series.id)
            pbLoading.visibility = View.GONE
            if (seasons.isNotEmpty()) {
                seasonAdapter.updateSeasons(seasons)
                episodeAdapter.updateEpisodes(seasons.first().episodes)
            } else {
                Toast.makeText(this@MainActivity, "Nenhum episódio encontrado para esta série.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }


    private fun focusActiveTab() {
        when (currentAppMode) {
            AppMode.LIVE_TV -> btnTabLive.requestFocus()
            AppMode.MOVIES -> btnTabMovies.requestFocus()
            AppMode.SERIES -> btnTabSeries.requestFocus()
        }
    }

}
