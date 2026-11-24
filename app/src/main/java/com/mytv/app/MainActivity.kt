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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory
import androidx.media3.extractor.ts.TsExtractor
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.adapter.CategorySidebarAdapter
import com.mytv.app.adapter.ChannelListAdapter
import com.mytv.app.adapter.SettingListAdapter
import com.mytv.app.data.ChannelRepository
import com.mytv.app.data.EpgRepository
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.MainSection
import com.mytv.app.model.SettingItem
import com.mytv.app.utils.AppSettings
import com.mytv.app.utils.AppUpdateManager
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

        // --- Barra de Menu Principal (5 Menus Separados) ---
    private lateinit var layoutMainMenuTabs: LinearLayout
    private lateinit var btnMenuTv: Button
    private lateinit var btnMenuFilmes: Button
    private lateinit var btnMenuSeries: Button
    private lateinit var btnMenuInfantil: Button
    private lateinit var btnMenuFutebol: Button
    private lateinit var btnMenuSettings: ImageButton
    private lateinit var txtSidebarSectionTitle: TextView

    private var currentSection: MainSection = MainSection.TV

    // --- Painel Esquerdo (Categorias) ---
    private lateinit var rvSidebar: RecyclerView
    private lateinit var sidebarAdapter: CategorySidebarAdapter
    private lateinit var txtHeaderBreadcrumb: TextView

    // --- Painel Central (Lista de Canais / Ajustes) ---
    private lateinit var panelChannels: LinearLayout
    private lateinit var txtSelectedCategoryTitle: TextView
    private lateinit var txtCategoryBadge: TextView
    private lateinit var rvChannelList: RecyclerView
    private lateinit var channelAdapter: ChannelListAdapter
    private lateinit var rvSettingsList: RecyclerView
    private lateinit var settingAdapter: SettingListAdapter

    // --- Painel Direito (Player Preview e Detalhes) ---
    private lateinit var playerPreviewContainer: View
    private lateinit var playerView: PlayerView
    private lateinit var previewLoadingLayout: View
    private lateinit var txtBitrate: TextView
    private lateinit var txtPreviewTitle: TextView
    private lateinit var txtPreviewInfo: TextView
    private lateinit var txtPreviewPlayback: TextView
    private lateinit var txtEpgNowTitle: TextView
    private lateinit var txtEpgNowTime: TextView
    private lateinit var pbEpgProgress: ProgressBar
    private lateinit var txtEpgSynopsis: TextView
    private lateinit var txtEpgNext: TextView
    private lateinit var btnFullscreen: ImageButton

    // --- Preview de Ajustes ---
    private lateinit var layoutSettingsPreview: View
    private lateinit var imgSettingPreviewIcon: ImageView
    private lateinit var txtSettingPreviewTitle: TextView
    private lateinit var txtSettingPreviewBadge: TextView
    private lateinit var txtSettingPreviewDesc: TextView
    private lateinit var btnSettingPreviewAction: Button

    // --- Estado do Player & Navegação ---
    private var exoPlayer: ExoPlayer? = null
    private var currentPlayingChannel: Channel? = null
    private var currentSelectedCategory: ChannelCategory? = null
    private var currentSettingItem: SettingItem? = null
    private var allCategories: List<ChannelCategory> = emptyList()
    private val zapHandler = Handler(Looper.getMainLooper())
    private var isSettingsMode = false

    // --- Tecnologia Netflix: Stall Watchdog & Multi-CDN Auto-Failover no Preview ---
    private val previewWatchdogHandler = Handler(Looper.getMainLooper())
    private var previewWatchdogRunnable: Runnable? = null
    private var previewCandidates: List<String> = emptyList()
    private var previewCandidateIndex: Int = 0

    private fun cancelPreviewWatchdog() {
        previewWatchdogRunnable?.let { previewWatchdogHandler.removeCallbacks(it) }
        previewWatchdogRunnable = null
    }

    private fun startPreviewWatchdog(channel: Channel) {
        cancelPreviewWatchdog()
        val runnable = Runnable {
            if (!isFinishing && !isDestroyed && (exoPlayer?.playbackState == Player.STATE_BUFFERING || exoPlayer?.playbackState == Player.STATE_IDLE)) {
                android.util.Log.w("WDTECH_NETFLIX", "Preview stall detectado (8.0s) no canal ${channel.title}. Acionando auto-failover...")
                triggerPreviewFailover(channel, "Stall de buffer no preview (8.0s)")
            }
        }
        previewWatchdogRunnable = runnable
        previewWatchdogHandler.postDelayed(runnable, 5000L)
    }

    private fun buildStreamCandidates(channel: Channel): List<String> {
        val list = mutableListOf<String>()
        val primary = channel.streamUrl
        if (primary.isNotEmpty()) {
            list.add(primary)
            if (primary.contains("64.31.23.207")) {
                if (primary.endsWith(".m3u8")) list.add(primary.replace(".m3u8", ".ts"))
                else if (primary.endsWith(".ts")) list.add(primary.replace(".ts", ".m3u8"))
            }
        }

        for (b in channel.backupUrls) {
            if (b.isNotEmpty() && !list.contains(b)) {
                list.add(b)
                if (b.contains("64.31.23.207")) {
                    val alt = if (b.endsWith(".m3u8")) b.replace(".m3u8", ".ts") else if (b.endsWith(".ts")) b.replace(".ts", ".m3u8") else null
                    if (alt != null && !list.contains(alt)) list.add(alt)
                }
            }
        }
        return list
    }

    private fun triggerPreviewFailover(channel: Channel, reason: String) {
        if (isFinishing || isDestroyed) return
        cancelPreviewWatchdog()
        previewCandidateIndex++

        if (previewCandidateIndex < previewCandidates.size) {
            val nextUrl = previewCandidates[previewCandidateIndex]
            previewLoadingLayout.visibility = View.VISIBLE
            txtBitrate.text = "RECONECTANDO..."
            playChannelPreviewInternal(channel, nextUrl)
        } else {
            previewLoadingLayout.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupPlayer()
        loadDataAndStartInitialChannel()
        setupBackPressed()

        // Verificação silenciosa de atualização
        AppUpdateManager.checkForUpdates(this, isManualCheck = false)
    }

    private fun initViews() {
                // Barra de Menu Principal (5 Menus Separados)
        layoutMainMenuTabs = findViewById(R.id.layout_main_menu_tabs)
        btnMenuTv = findViewById(R.id.btn_menu_tv)
        btnMenuFilmes = findViewById(R.id.btn_menu_filmes)
        btnMenuSeries = findViewById(R.id.btn_menu_series)
        btnMenuInfantil = findViewById(R.id.btn_menu_infantil)
        btnMenuFutebol = findViewById(R.id.btn_menu_futebol)
        btnMenuSettings = findViewById(R.id.btn_menu_settings)
        txtSidebarSectionTitle = findViewById(R.id.txt_sidebar_section_title)

        setupMainMenuTabs()

        // Barra Superior
        txtHeaderBreadcrumb = findViewById(R.id.txt_header_breadcrumb)

        // Painel Lateral Esquerdo
        rvSidebar = findViewById(R.id.rv_category_sidebar)
        rvSidebar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Painel Central
        panelChannels = findViewById(R.id.panel_channels)
        txtSelectedCategoryTitle = findViewById(R.id.txt_selected_category_title)
        txtCategoryBadge = findViewById(R.id.txt_category_badge)
        rvChannelList = findViewById(R.id.rv_channel_list)
        rvChannelList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSettingsList = findViewById(R.id.rv_settings_list)
        rvSettingsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Painel Direito (Player)
        playerPreviewContainer = findViewById(R.id.player_preview_container)
        playerView = findViewById(R.id.player_preview_view)
        previewLoadingLayout = findViewById(R.id.preview_loading_layout)
        txtBitrate = findViewById(R.id.txt_bitrate)
        txtPreviewTitle = findViewById(R.id.txt_preview_title)
        txtPreviewInfo = findViewById(R.id.txt_preview_info)
        txtPreviewPlayback = findViewById(R.id.txt_preview_playback)
        txtEpgNowTitle = findViewById(R.id.txt_epg_now_title)
        txtEpgNowTime = findViewById(R.id.txt_epg_now_time)
        pbEpgProgress = findViewById(R.id.pb_epg_progress)
        txtEpgSynopsis = findViewById(R.id.txt_epg_synopsis)
        txtEpgNext = findViewById(R.id.txt_epg_next)
        btnFullscreen = findViewById(R.id.btn_fullscreen)

        // Preview Ajustes
        layoutSettingsPreview = findViewById(R.id.layout_settings_preview)
        imgSettingPreviewIcon = findViewById(R.id.img_setting_preview_icon)
        txtSettingPreviewTitle = findViewById(R.id.txt_setting_preview_title)
        txtSettingPreviewBadge = findViewById(R.id.txt_setting_preview_badge)
        txtSettingPreviewDesc = findViewById(R.id.txt_setting_preview_desc)
        btnSettingPreviewAction = findViewById(R.id.btn_setting_preview_action)

        // Botão Tela Cheia
        btnFullscreen.setOnClickListener {
            currentPlayingChannel?.let { openFullScreenPlayer(it) }
        }
        btnFullscreen.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                focusCurrentChannel()
                return@setOnKeyListener true
            }
            false
        }

        // Configuração dos Ajustes
        setupSettings()
    }


    private fun setupMainMenuTabs() {
        val tabButtons = listOf(
            btnMenuTv to MainSection.TV,
            btnMenuFilmes to MainSection.FILMES,
            btnMenuSeries to MainSection.SERIES,
            btnMenuInfantil to MainSection.INFANTIL,
            btnMenuFutebol to MainSection.FUTEBOL
        )

        for ((btn, section) in tabButtons) {
            btn.setOnClickListener {
                selectMainSection(section, shouldFocusCategory = true)
            }
            btn.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && currentSection != section) {
                    selectMainSection(section, shouldFocusCategory = false)
                }
            }
            btn.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    focusFirstCategory()
                    return@setOnKeyListener true
                }
                false
            }
        }

        btnMenuSettings.setOnClickListener {
            openSettingsMode()
        }
        btnMenuSettings.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                openSettingsMode()
            }
        }
        btnMenuSettings.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                focusFirstCategory()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun selectMainSection(section: MainSection, shouldFocusCategory: Boolean = false) {
        currentSection = section
        isSettingsMode = false

        btnMenuTv.isSelected = (section == MainSection.TV)
        btnMenuFilmes.isSelected = (section == MainSection.FILMES)
        btnMenuSeries.isSelected = (section == MainSection.SERIES)
        btnMenuInfantil.isSelected = (section == MainSection.INFANTIL)
        btnMenuFutebol.isSelected = (section == MainSection.FUTEBOL)
        btnMenuSettings.isSelected = false

        txtSidebarSectionTitle.text = "CATEGORIAS • ${section.title}"

        val sectionCats = EpgRepository.enrichCategories(
            ChannelRepository.getCategoriesForSection(section)
        )
        allCategories = sectionCats

        val firstCat = sectionCats.firstOrNull() ?: return
        currentSelectedCategory = firstCat

        sidebarAdapter.updateCategories(sectionCats)
        sidebarAdapter.setSelectedCategory(firstCat)

        channelAdapter.updateChannels(firstCat.channels)
        txtHeaderBreadcrumb.text = "WDTECH TV • ${section.title} • ${firstCat.name.uppercase()}"
        txtSelectedCategoryTitle.text = firstCat.name
        txtCategoryBadge.text = firstCat.channels.size.toString()

        panelChannels.visibility = View.VISIBLE
        playerPreviewContainer.visibility = View.VISIBLE
        layoutSettingsPreview.visibility = View.GONE
        rvChannelList.visibility = View.VISIBLE
        rvSettingsList.visibility = View.GONE

        val firstChannel = firstCat.channels.firstOrNull()
        if (firstChannel != null && firstChannel.id != currentPlayingChannel?.id) {
            playChannelPreview(firstChannel)
        }

        if (shouldFocusCategory) {
            focusFirstCategory()
        }
    }

    private fun openSettingsMode() {
        isSettingsMode = true
        btnMenuTv.isSelected = false
        btnMenuFilmes.isSelected = false
        btnMenuSeries.isSelected = false
        btnMenuInfantil.isSelected = false
        btnMenuFutebol.isSelected = false
        btnMenuSettings.isSelected = true

        txtSidebarSectionTitle.text = "AJUSTES • SISTEMA"
        panelChannels.visibility = View.VISIBLE
        rvChannelList.visibility = View.GONE
        rvSettingsList.visibility = View.VISIBLE
        playerPreviewContainer.visibility = View.GONE
        layoutSettingsPreview.visibility = View.VISIBLE
        txtCategoryBadge.text = "AJUSTES"
        txtSelectedCategoryTitle.text = "Ajustes do Aplicativo"
        txtHeaderBreadcrumb.text = "WDTECH TV • AJUSTES DO SISTEMA"
        updateSettingPreview(getSettingItems().first())
    }

    private fun focusActiveTab() {
        when (currentSection) {
            MainSection.TV -> btnMenuTv.requestFocus()
            MainSection.FILMES -> btnMenuFilmes.requestFocus()
            MainSection.SERIES -> btnMenuSeries.requestFocus()
            MainSection.INFANTIL -> btnMenuInfantil.requestFocus()
            MainSection.FUTEBOL -> btnMenuFutebol.requestFocus()
        }
    }

    private fun loadDataAndStartInitialChannel() {
        val initialSection = MainSection.TV
        currentSection = initialSection

        val sectionCats = EpgRepository.enrichCategories(
            ChannelRepository.getCategoriesForSection(initialSection)
        )
        allCategories = sectionCats

        val firstCat = sectionCats.firstOrNull() ?: return
        currentSelectedCategory = firstCat

        // 1. Configura Adaptador da Barra Lateral
        sidebarAdapter = CategorySidebarAdapter(
            categories = sectionCats,
            onCategoryFocused = { cat ->
                onSidebarCategoryFocused(cat)
            },
            onCategoryClicked = { cat ->
                onSidebarCategoryClicked(cat)
            },
            onNavigateRightToContent = {
                if (isSettingsMode) {
                    focusFirstSetting()
                } else {
                    focusCurrentChannel()
                }
            },
            onNavigateUpToTabs = {
                focusActiveTab()
            }
        )
        sidebarAdapter.setSelectedCategory(firstCat)
        rvSidebar.adapter = sidebarAdapter

        // 2. Configura Adaptador de Canais
        channelAdapter = ChannelListAdapter(
            channels = firstCat.channels,
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
                btnFullscreen.requestFocus()
            }
        )
        rvChannelList.adapter = channelAdapter

        // Atualiza textos do cabeçalho
        btnMenuTv.isSelected = true
        txtSidebarSectionTitle.text = "CATEGORIAS • TV"
        txtHeaderBreadcrumb.text = "WDTECH TV • TV • ${firstCat.name.uppercase()}"
        txtSelectedCategoryTitle.text = firstCat.name
        txtCategoryBadge.text = firstCat.channels.size.toString()

        // 3. REQUISITO CRÍTICO: Inicia reprodução imediata do primeiro canal ao vivo com imagem!
        val initialChannel = firstCat.channels.firstOrNull()
        if (initialChannel != null) {
            playChannelPreview(initialChannel)
        }

        // 4. Posiciona o foco no canal inicial
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                rvChannelList.scrollToPosition(0)
                rvChannelList.postDelayed({
                    val vh = rvChannelList.findViewHolderForAdapterPosition(0)
                    vh?.itemView?.requestFocus() ?: rvChannelList.requestFocus()
                }, 100)
            }
        }, 150)
    }

    private fun onSidebarCategoryFocused(category: ChannelCategory) {
        currentSelectedCategory = category
        txtHeaderBreadcrumb.text = "WDTECH TV • ${category.name.uppercase()}"
        txtSelectedCategoryTitle.text = category.name

        if (category.id == "cat_settings") {
            isSettingsMode = true
            panelChannels.visibility = View.VISIBLE
            rvChannelList.visibility = View.GONE
            rvSettingsList.visibility = View.VISIBLE
            playerPreviewContainer.visibility = View.GONE
            layoutSettingsPreview.visibility = View.VISIBLE
            txtCategoryBadge.text = "AJUSTES"
            updateSettingPreview(getSettingItems().first())
        } else {
            isSettingsMode = false
            panelChannels.visibility = View.VISIBLE
            rvSettingsList.visibility = View.GONE
            rvChannelList.visibility = View.VISIBLE
            layoutSettingsPreview.visibility = View.GONE
            playerPreviewContainer.visibility = View.VISIBLE
            txtCategoryBadge.text = category.channels.size.toString()

            // Atualiza a lista de canais dinamicamente SEM interromper o vídeo ao vivo que está passando!
            channelAdapter.updateChannels(category.channels)
        }
    }

    private fun onSidebarCategoryClicked(category: ChannelCategory) {
        onSidebarCategoryFocused(category)
        if (isSettingsMode) {
            focusFirstSetting()
        } else {
            focusFirstChannel()
        }
    }

    private fun onChannelFocused(channel: Channel) {
        // Atualiza imediatamente informações visuais no HUD inferior
        updatePreviewHUD(channel)

        // Debounce de 350ms para troca de canal no preview (evita disparar requisições em navegação rápida)
        zapHandler.removeCallbacksAndMessages(null)
        zapHandler.postDelayed({
            if (currentPlayingChannel?.id != channel.id) {
                playChannelPreview(channel)
            }
        }, 350)
    }

    private fun updatePreviewHUD(channel: Channel) {
        val num = channel.number.toIntOrNull() ?: 1
        txtPreviewTitle.text = String.format("%03d - %s", num, channel.title)
        txtPreviewInfo.text = "${channel.category} • ${channel.quality}"
        txtPreviewPlayback.visibility = if (channel.hasPlayback) View.VISIBLE else View.GONE

        val epg = channel.currentProgram
        if (epg != null) {
            txtEpgNowTitle.text = epg.title
            txtEpgNowTime.text = "${epg.startTime} - ${epg.endTime}"
            txtEpgSynopsis.text = if (epg.synopsis.isNotEmpty()) epg.synopsis else (channel.description ?: "")
            pbEpgProgress.progress = epg.getProgressPercent().coerceIn(5, 95)
        } else {
            txtEpgNowTitle.text = "Transmissão Ao Vivo (${channel.quality})"
            txtEpgNowTime.text = "24 Horas no Ar"
            txtEpgSynopsis.text = channel.description ?: ""
            pbEpgProgress.progress = 50
        }

        txtEpgNext.text = "Próximo: Programação contínua 24H"
    }

    private fun setupPlayer() {
        val bufCfg = AppSettings.getBufferConfig(this)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                bufCfg.minBufferMs,
                bufCfg.maxBufferMs,
                bufCfg.bufferForPlaybackMs,
                bufCfg.afterRebufferMs
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .setBackBuffer(0, false)
            .build()

        val renderersFactory = DefaultRenderersFactory(this).apply {
            setEnableDecoderFallback(true)
            setAllowedVideoJoiningTimeMs(4000)
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        }

        exoPlayer?.release()
        val player = ExoPlayer.Builder(this, renderersFactory)
            .setLoadControl(loadControl)
            .build()
        player.playWhenReady = true
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        previewLoadingLayout.visibility = View.VISIBLE
                        txtBitrate.text = "SINTONIZANDO..."
                    }
                    Player.STATE_READY -> {
                        cancelPreviewWatchdog()
                        previewLoadingLayout.visibility = View.GONE
                    }
                    Player.STATE_ENDED -> {
                        previewLoadingLayout.visibility = View.GONE
                    }
                    Player.STATE_IDLE -> {
                    }
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                cancelPreviewWatchdog()
                currentPlayingChannel?.let { triggerPreviewFailover(it, "Erro no player de preview: ${error.errorCodeName}") }
            }
        })

        playerView.player = player
        exoPlayer = player
    }

    private fun playChannelPreview(channel: Channel) {
        cancelPreviewWatchdog()
        previewCandidates = buildStreamCandidates(channel)
        previewCandidateIndex = 0
        val targetUrl = previewCandidates.firstOrNull() ?: channel.streamUrl
        playChannelPreviewInternal(channel, targetUrl)
    }

    private fun playChannelPreviewInternal(channel: Channel, targetUrl: String) {
        val player = exoPlayer ?: return
        if (isFinishing || isDestroyed) return

        currentPlayingChannel = channel
        updatePreviewHUD(channel)

        // Salva canal nas preferências como último assistido
        getSharedPreferences("wdtechtv_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("last_played_channel_id", channel.id)
            .apply()

        previewLoadingLayout.visibility = View.VISIBLE
        txtBitrate.text = "SINTONIZANDO..."

        try {
            player.stop()
            player.clearMediaItems()

            val dataSourceFactory = com.mytv.app.network.TurboNetworkFactory.createDataSourceFactory(this)

            val extractorsFactory = DefaultExtractorsFactory().apply {
                setTsExtractorMode(TsExtractor.MODE_SINGLE_PMT)
                setTsExtractorFlags(
                    DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES or
                    DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS
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
                        .setTargetOffsetMs(25_000)
                        .setMinOffsetMs(10_000)
                        .setMaxOffsetMs(50_000)
                        .setMinPlaybackSpeed(0.97f)
                        .setMaxPlaybackSpeed(1.03f)
                        .build()
                )
                .build()

            val mediaSource = if (targetUrl.contains(".m3u8")) {
                HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(mediaItem)
            } else {
                DefaultMediaSourceFactory(dataSourceFactory, extractorsFactory)
                    .createMediaSource(mediaItem)
            }

            startPreviewWatchdog(channel)
            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        } catch (t: Throwable) {
            cancelPreviewWatchdog()
            previewLoadingLayout.visibility = View.GONE
        }
    }

    private fun openFullScreenPlayer(channel: Channel) {
        zapHandler.removeCallbacksAndMessages(null)
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        previewLoadingLayout.visibility = View.GONE

        val channelsInScope = currentSelectedCategory?.channels?.filter { it.streamUrl.isNotEmpty() }
            ?: allCategories.firstOrNull { it.id == "cat_todos" }?.channels
            ?: listOf(channel)

        val currentIndex = channelsInScope.indexOfFirst { it.id == channel.id }.coerceAtLeast(0)

        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_CHANNEL, channel)
            putExtra(PlayerActivity.EXTRA_CHANNEL_LIST, ArrayList(channelsInScope))
            putExtra(PlayerActivity.EXTRA_CHANNEL_INDEX, currentIndex)
        }
        startActivity(intent)
    }

    private fun focusFirstCategory() {
        if (!isFinishing && !isDestroyed) {
            rvSidebar.post {
                val vh = rvSidebar.findViewHolderForAdapterPosition(0)
                vh?.itemView?.requestFocus() ?: rvSidebar.requestFocus()
            }
        }
    }

    private fun focusCurrentCategory() {
        val selIndex = sidebarAdapter.getSelectedPosition()
        rvSidebar.stopScroll()
        rvSidebar.scrollToPosition(selIndex)
        rvSidebar.post {
            val vh = rvSidebar.findViewHolderForAdapterPosition(selIndex)
            vh?.itemView?.requestFocus() ?: rvSidebar.requestFocus()
        }
    }

    private fun focusFirstChannel() {
        if (channelAdapter.itemCount == 0) return
        rvChannelList.stopScroll()
        rvChannelList.scrollToPosition(0)
        rvChannelList.post {
            val vh = rvChannelList.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus() ?: rvChannelList.requestFocus()
        }
    }

    private fun focusCurrentChannel() {
        val curr = currentPlayingChannel
        val idx = if (curr != null) {
            channelAdapter.getChannels().indexOfFirst { it.id == curr.id }.coerceAtLeast(0)
        } else {
            0
        }
        rvChannelList.stopScroll()
        rvChannelList.scrollToPosition(idx)
        rvChannelList.post {
            val vh = rvChannelList.findViewHolderForAdapterPosition(idx)
            vh?.itemView?.requestFocus() ?: rvChannelList.requestFocus()
        }
    }

    private fun focusFirstSetting() {
        if (settingAdapter.itemCount == 0) return
        rvSettingsList.stopScroll()
        rvSettingsList.scrollToPosition(0)
        rvSettingsList.post {
            val vh = rvSettingsList.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus() ?: rvSettingsList.requestFocus()
        }
    }

    // --- Configuração dos Ajustes ---
    private fun setupSettings() {
        val items = getSettingItems()
        settingAdapter = SettingListAdapter(
            items = items,
            onSettingFocused = { item ->
                currentSettingItem = item
                updateSettingPreview(item)
            },
            onSettingClicked = { item ->
                currentSettingItem = item
                executeSettingAction(item)
            },
            onNavigateToCategories = {
                focusCurrentCategory()
            },
            onNavigateToPreview = {
                btnSettingPreviewAction.requestFocus()
            }
        )
        rvSettingsList.adapter = settingAdapter

        btnSettingPreviewAction.setOnClickListener {
            currentSettingItem?.let { executeSettingAction(it) }
        }
        btnSettingPreviewAction.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                focusFirstSetting()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun getSettingItems(): List<SettingItem> {
        val bufferLabel = AppSettings.getBufferLabel(AppSettings.getBufferMode(this))
        val aspectLabel = AppSettings.getAspectRatioLabel(AppSettings.getAspectRatio(this))
        val isTurboActive = AppSettings.isTurboDohEnabled(this)
        return listOf(
            SettingItem(
                id = "setting_update",
                title = "Atualizações do App",
                subtitle = "Verificar novas versões e novidades",
                iconRes = R.drawable.ic_check_update,
                badgeText = "V2.8.8",
                description = "Verifica se há novas atualizações disponíveis no GitHub oficial da WDTECH TV. Atualizações automáticas trazem novos canais, correções de travamento e melhorias no player.",
                actionButtonText = "Verificar Atualizações Agora (OK)"
            ),
            SettingItem(
                id = "setting_turbo_doh",
                title = "Aceleração Cloudflare & Anti-Trava",
                subtitle = "DNS Seguro DoH: ${AppSettings.getTurboDohLabel(isTurboActive)}",
                iconRes = R.drawable.ic_speed,
                badgeText = if (isTurboActive) "ATIVO" else "OFF",
                description = "Ignora bloqueios e Traffic Shaping da sua operadora (Claro, Vivo, Oi) usando criptografia Cloudflare 1.1.1.1 e múltiplos canais de conexão TCP.\n\n• Ativado: Conexão direta aos servidores Cloudflare sem restrição de velocidade.\n• Desativado: Usa o DNS padrão do provedor de internet.",
                actionButtonText = "Alternar Aceleração Cloudflare (OK)"
            ),
            SettingItem(
                id = "setting_network",
                title = "Teste de Conexão & Ping",
                subtitle = "Medir latência e velocidade com o servidor CDN",
                iconRes = R.drawable.ic_wifi_tv,
                badgeText = "TESTAR",
                description = "Mede a resposta e estabilidade da sua conexão com o servidor de transmissão WDTECH TV (64.31.23.207:80). Pressione para medir a latência em tempo real.",
                actionButtonText = "Testar Ping com o Servidor (OK)"
            ),
            SettingItem(
                id = "setting_buffer",
                title = "Buffer de Reprodução",
                subtitle = "Modo ativo: $bufferLabel",
                iconRes = R.drawable.ic_speed,
                badgeText = bufferLabel.split(" ")[0].uppercase(),
                description = "Ajusta a memória de buffer do player para evitar travamentos:\n\n• Rápido (10s): Troca de canais instantânea.\n• Equilibrado (25s): Recomendado padrão, alta estabilidade com troca rápida.\n• Anti-Travamento (45s): Máxima proteção contra oscilações de Wi-Fi.",
                actionButtonText = "Alternar Modo de Buffer (OK)"
            ),
            SettingItem(
                id = "setting_aspect",
                title = "Formato de Tela (Aspect Ratio)",
                subtitle = "Modo ativo: $aspectLabel",
                iconRes = R.drawable.ic_aspect_ratio,
                badgeText = aspectLabel.split(" ")[0].uppercase(),
                description = "Ajusta o enquadramento do vídeo na sua televisão:\n\n• Original (16:9): Mantém a proporção exata da transmissão original.\n• Zoom: Remove possíveis faixas pretas preenchendo a tela.\n• Esticar: Expande o vídeo para ocupar 100% da sua TV.",
                actionButtonText = "Alternar Formato de Tela (OK)"
            ),
            SettingItem(
                id = "setting_cache",
                title = "Limpar Cache & Memória",
                subtitle = "Liberar memória RAM e espaço em disco",
                iconRes = R.drawable.ic_clean,
                badgeText = "OTIMIZAR",
                description = "Limpa arquivos temporários de imagens, logos de canais e buffers acumulados em memória para acelerar a resposta do app.",
                actionButtonText = "Limpar Cache Agora (OK)"
            ),
            SettingItem(
                id = "setting_exit",
                title = "Sair do Aplicativo",
                subtitle = "Encerrar WDTECH TV com segurança",
                iconRes = R.drawable.ic_exit,
                badgeText = "ENCERRAR",
                description = "Fecha o aplicativo com segurança e salva todas as suas preferências e histórico.",
                actionButtonText = "Confirmar Saída (OK)"
            )
        )
    }

    private fun updateSettingPreview(item: SettingItem) {
        txtSettingPreviewTitle.text = item.title
        txtSettingPreviewBadge.text = item.badgeText
        txtSettingPreviewDesc.text = item.description
        btnSettingPreviewAction.text = item.actionButtonText
        imgSettingPreviewIcon.setImageResource(item.iconRes)
    }

    private fun executeSettingAction(item: SettingItem) {
        when (item.id) {
            "setting_turbo_doh" -> {
                val newState = AppSettings.toggleTurboDoh(this)
                val label = AppSettings.getTurboDohLabel(newState)
                item.badgeText = if (newState) "ATIVO" else "OFF"
                item.subtitle = "DNS Seguro DoH: $label"
                item.actionButtonText = "Aceleração: ${if (newState) "LIGADA" else "DESLIGADA"} (OK)"
                updateSettingPreview(item)
                settingAdapter.notifyDataSetChanged()
                setupPlayer()
                currentPlayingChannel?.let { playChannelPreview(it) }
                Toast.makeText(this, "Aceleração Cloudflare: $label", Toast.LENGTH_SHORT).show()
            }
            "setting_update" -> {
                Toast.makeText(this, "Verificando atualizações no servidor...", Toast.LENGTH_SHORT).show()
                AppUpdateManager.checkForUpdates(this, isManualCheck = true)
            }
            "setting_network" -> {
                txtSettingPreviewBadge.text = "MEDINDO..."
                btnSettingPreviewAction.isEnabled = false
                lifecycleScope.launch {
                    val result = AppSettings.testConnectionLatency()
                    btnSettingPreviewAction.isEnabled = true
                    if (result.first) {
                        val ping = result.second
                        val quality = if (ping < 60) "Excelente" else if (ping < 120) "Boa" else "Instável"
                        txtSettingPreviewBadge.text = "${ping}ms • $quality"
                        txtSettingPreviewDesc.text = "Servidor CDN WDTECH TV (64.31.23.207:80) online!\n\n• Latência: ${ping}ms ($quality)\n• Status: Online e Transmitindo\n• Conexão pronta para alta definição."
                        Toast.makeText(this@MainActivity, "Ping com Servidor: ${ping}ms ($quality)", Toast.LENGTH_LONG).show()
                    } else {
                        txtSettingPreviewBadge.text = "FALHA"
                        txtSettingPreviewDesc.text = "Não foi possível conectar ao servidor.\nVerifique a sua internet."
                        Toast.makeText(this@MainActivity, "Erro ao testar conexão.", Toast.LENGTH_SHORT).show()
                    }
                    settingAdapter.notifyDataSetChanged()
                }
            }
            "setting_buffer" -> {
                val nextMode = AppSettings.cycleBufferMode(this)
                val label = AppSettings.getBufferLabel(nextMode)
                item.badgeText = label.split(" ")[0].uppercase()
                item.actionButtonText = "Modo Ativo: $label (OK)"
                updateSettingPreview(item)
                settingAdapter.notifyDataSetChanged()
                setupPlayer()
                currentPlayingChannel?.let { playChannelPreview(it) }
                Toast.makeText(this, "Buffer configurado para: $label", Toast.LENGTH_SHORT).show()
            }
            "setting_aspect" -> {
                val nextAspect = AppSettings.cycleAspectRatio(this)
                val label = AppSettings.getAspectRatioLabel(nextAspect)
                playerView.resizeMode = nextAspect
                item.badgeText = label.split(" ")[0].uppercase()
                item.actionButtonText = "Formato: $label (OK)"
                updateSettingPreview(item)
                settingAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Formato alterado: $label", Toast.LENGTH_SHORT).show()
            }
            "setting_cache" -> {
                btnSettingPreviewAction.isEnabled = false
                txtSettingPreviewBadge.text = "LIMPANDO..."
                lifecycleScope.launch {
                    val bytesFreed = AppSettings.clearCache(this@MainActivity)
                    btnSettingPreviewAction.isEnabled = true
                    val mbFreed = String.format("%.2f", bytesFreed / (1024.0 * 1024.0))
                    txtSettingPreviewBadge.text = "LIMPO"
                    txtSettingPreviewDesc.text = "Memória limpa com sucesso!\n\n• Liberado: ${mbFreed} MB\n• Sistema otimizado para reprodução contínua."
                    Toast.makeText(this@MainActivity, "Memória limpa! ${mbFreed} MB liberados.", Toast.LENGTH_LONG).show()
                    settingAdapter.notifyDataSetChanged()
                }
            }
            "setting_exit" -> {
                showExitConfirmation()
            }
        }
    }

    // --- Controle do Botão Voltar (D-Pad Back) e Diálogo de Saída ---
    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (btnFullscreen.hasFocus()) {
                    focusCurrentChannel()
                } else if (rvChannelList.hasFocus() || rvSettingsList.hasFocus() || btnSettingPreviewAction.hasFocus()) {
                    focusCurrentCategory()
                } else {
                    showExitConfirmation()
                }
            }
        })
    }

    private fun showExitConfirmation() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_simple_exit)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.75f)

        val btnSim = dialog.findViewById<Button>(R.id.btn_dialog_exit_sim)
        val btnNao = dialog.findViewById<Button>(R.id.btn_dialog_exit_nao)

        btnSim.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }

        btnNao.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            btnNao.requestFocus()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (exoPlayer == null) {
            setupPlayer()
        }
        currentPlayingChannel?.let { playChannelPreview(it) }
    }

    override fun onPause() {
        super.onPause()
        cancelPreviewWatchdog()
        zapHandler.removeCallbacksAndMessages(null)
        exoPlayer?.pause()
    }

    override fun onStop() {
        super.onStop()
        zapHandler.removeCallbacksAndMessages(null)
        exoPlayer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelPreviewWatchdog()
        zapHandler.removeCallbacksAndMessages(null)
        exoPlayer?.release()
        exoPlayer = null
    }
}
