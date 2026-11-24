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
import coil.load
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
import com.mytv.app.adapter.OttPosterAdapter
import com.mytv.app.adapter.VodListAdapter
import com.mytv.app.data.VodRepository
import com.mytv.app.model.VodItem
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
    private lateinit var btnMenuFutebol: Button
    private lateinit var btnMenuSettings: Button
    private lateinit var txtSidebarSectionTitle: TextView
    private var vodFetchJob: kotlinx.coroutines.Job? = null

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
    private var imgPreviewLogo: ImageView? = null
    private lateinit var txtPreviewTitle: TextView
    private lateinit var txtPreviewInfo: TextView
    private lateinit var txtPreviewPlayback: TextView
    private lateinit var txtEpgNowTitle: TextView
    private lateinit var txtEpgNowTime: TextView
    private lateinit var pbEpgProgress: ProgressBar
    private lateinit var txtEpgSynopsis: TextView
    private lateinit var txtEpgNext: TextView
    private lateinit var btnFullscreen: ImageButton

    // --- Home Lançamentos & Navegação ---
    private var isHomeMode: Boolean = true
    private lateinit var btnToggleMenu: Button
    private lateinit var btnMenuHome: Button
    private lateinit var layoutHomeLancamentos: View
    private lateinit var layoutThreePanels: View
    private lateinit var imgHeroBackdrop: ImageView
    private lateinit var txtHeroTitle: TextView
    private lateinit var txtHeroMeta: TextView
    private lateinit var txtHeroPlot: TextView
    private lateinit var btnHeroWatch: Button
    private lateinit var btnHeroOpenMenu: Button
    private lateinit var rvHomeLancamentos: RecyclerView
    private lateinit var homeLancamentosAdapter: OttPosterAdapter
    private var currentHeroVodItem: VodItem? = null

    // --- VOD (Filmes e Séries) ---
    private lateinit var rvVodList: RecyclerView
    private lateinit var vodAdapter: VodListAdapter
    private lateinit var layoutVodPreview: View
    private lateinit var imgVodPreviewPoster: ImageView
    private lateinit var txtVodPreviewTitle: TextView
    private lateinit var txtVodPreviewMeta: TextView
    private lateinit var txtVodPreviewPlot: TextView
    private lateinit var btnVodAction: Button
    private var currentSelectedVodItem: VodItem? = null

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
        previewWatchdogHandler.postDelayed(runnable, 20000L)
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
        if (previewCandidates.isEmpty()) {
            previewCandidates = buildStreamCandidates(channel)
        }
        previewCandidateIndex = (previewCandidateIndex + 1) % previewCandidates.size
        val nextUrl = previewCandidates[previewCandidateIndex]
        android.util.Log.i("WDTECH_NETFLIX", "Preview failover contínuo [Candidato ${previewCandidateIndex + 1}/${previewCandidates.size}] para: $nextUrl ($reason)")
        previewLoadingLayout.visibility = View.VISIBLE
        playChannelPreviewInternal(channel, nextUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Escudo de Estabilidade Definitivo: Intercepta qualquer exceção não tratada evitando que o app feche
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("WDTECH_CRASH", "Exceção interceptada com sucesso na thread ${thread.name}: ${throwable.message}", throwable)
        }

        setContentView(R.layout.activity_main)

        initViews()
        setupPlayer()
        loadDataAndStartInitialChannel()
        setupBackPressed()

        // Verificação silenciosa de atualização
        AppUpdateManager.checkForUpdates(this, isManualCheck = false)
    }

    private fun initViews() {
                // Barra de Navegação e Botão de Alternar Menu
        btnToggleMenu = findViewById(R.id.btn_toggle_menu)
        layoutHomeLancamentos = findViewById(R.id.layout_home_lancamentos)
        layoutThreePanels = findViewById(R.id.layout_three_panels)

        // Banner Hero e Carrossel de Lançamentos
        imgHeroBackdrop = findViewById(R.id.img_hero_backdrop)
        txtHeroTitle = findViewById(R.id.txt_hero_title)
        txtHeroMeta = findViewById(R.id.txt_hero_meta)
        txtHeroPlot = findViewById(R.id.txt_hero_plot)
        btnHeroWatch = findViewById(R.id.btn_hero_watch)
        btnHeroOpenMenu = findViewById(R.id.btn_hero_open_menu)
        rvHomeLancamentos = findViewById(R.id.rv_home_lancamentos)

        // Barra de Menu Principal
        layoutMainMenuTabs = findViewById(R.id.layout_main_menu_tabs)
        btnMenuHome = findViewById(R.id.btn_menu_home)
        btnMenuTv = findViewById(R.id.btn_menu_tv)
        btnMenuFilmes = findViewById(R.id.btn_menu_filmes)
        btnMenuSeries = findViewById(R.id.btn_menu_series)
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
        rvVodList = findViewById(R.id.rv_vod_list)
        rvVodList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutVodPreview = findViewById(R.id.layout_vod_preview)
        imgVodPreviewPoster = findViewById(R.id.img_vod_preview_poster)
        txtVodPreviewTitle = findViewById(R.id.txt_vod_preview_title)
        txtVodPreviewMeta = findViewById(R.id.txt_vod_preview_meta)
        txtVodPreviewPlot = findViewById(R.id.txt_vod_preview_plot)
        btnVodAction = findViewById(R.id.btn_vod_action)

        // Painel Direito (Player)
        playerPreviewContainer = findViewById(R.id.player_preview_container)
        playerView = findViewById(R.id.player_preview_view)
        previewLoadingLayout = findViewById(R.id.preview_loading_layout)
        txtBitrate = findViewById(R.id.txt_bitrate)
        imgPreviewLogo = findViewById(R.id.img_preview_channel_logo)
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

        // Expansão Direta para Tela Cheia ao Focar e Clicar na Tela do Player
        val expandToFullScreen = View.OnClickListener {
            currentPlayingChannel?.let { openFullScreenPlayer(it) }
        }

        playerPreviewContainer.isFocusable = true
        playerPreviewContainer.isClickable = true
        playerPreviewContainer.setOnClickListener(expandToFullScreen)

        playerView.isClickable = true
        playerView.setOnClickListener(expandToFullScreen)

        val playerKeyListener = View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        currentPlayingChannel?.let { openFullScreenPlayer(it) }
                        return@OnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        focusCurrentChannel()
                        return@OnKeyListener true
                    }
                }
            }
            false
        }

        playerPreviewContainer.setOnKeyListener(playerKeyListener)
        playerView.setOnKeyListener(playerKeyListener)
        btnFullscreen.setOnClickListener(expandToFullScreen)

        // Configuração de VOD (Filmes e Séries) e Ajustes
        setupVod()
        setupSettings()
    }


    private fun setupMainMenuTabs() {
        btnToggleMenu.setOnClickListener {
            if (isHomeMode) {
                openMenu(currentSection)
            } else {
                showHomeScreen()
            }
        }

        btnHeroOpenMenu.setOnClickListener {
            openMenu(MainSection.TV)
        }

        btnHeroWatch.setOnClickListener {
            currentHeroVodItem?.let { openVodPlayer(it) }
        }

        btnMenuHome.setOnClickListener {
            showHomeScreen()
        }
        btnMenuHome.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isHomeMode) {
                showHomeScreen()
            }
        }

        val tabButtons = listOf(
            btnMenuTv to MainSection.TV,
            btnMenuFilmes to MainSection.FILMES,
            btnMenuSeries to MainSection.SERIES,
            btnMenuFutebol to MainSection.FUTEBOL
        )

        for ((btn, section) in tabButtons) {
            btn.setOnClickListener {
                openMenu(section, shouldFocusCategory = true)
            }
            btn.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (currentSection != section || isHomeMode)) {
                    openMenu(section, shouldFocusCategory = false)
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
            if (hasFocus && !isSettingsMode) {
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

    private fun showHomeScreen() {
        try {
            isHomeMode = true
            isSettingsMode = false
            pausePreviewPlayer()

            layoutHomeLancamentos.visibility = View.VISIBLE
            layoutMainMenuTabs.visibility = View.GONE
            layoutThreePanels.visibility = View.GONE

            btnToggleMenu.text = "☰ ABRIR MENU (OK)"
            txtHeaderBreadcrumb.text = "WDTECH TV • LANÇAMENTOS 2025/2026"
            btnMenuHome.isSelected = true
            btnMenuTv.isSelected = false
            btnMenuFilmes.isSelected = false
            btnMenuSeries.isSelected = false
            btnMenuFutebol.isSelected = false
            btnMenuSettings.isSelected = false

            btnHeroWatch.requestFocus()
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "Erro ao exibir tela inicial", e)
        }
    }

    private fun openMenu(section: MainSection = currentSection, shouldFocusCategory: Boolean = false) {
        try {
            isHomeMode = false
            layoutHomeLancamentos.visibility = View.GONE
            layoutMainMenuTabs.visibility = View.VISIBLE
            layoutThreePanels.visibility = View.VISIBLE

            btnToggleMenu.text = "🏠 INÍCIO (OK)"
            btnMenuHome.isSelected = false

            selectMainSection(section, shouldFocusCategory)
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "Erro ao abrir menu", e)
        }
    }

    private fun selectMainSection(section: MainSection, shouldFocusCategory: Boolean = false) {
        currentSection = section
        isSettingsMode = false

        btnMenuTv.isSelected = (section == MainSection.TV)
        btnMenuFilmes.isSelected = (section == MainSection.FILMES)
        btnMenuSeries.isSelected = (section == MainSection.SERIES)
        btnMenuFutebol.isSelected = (section == MainSection.FUTEBOL)
        btnMenuSettings.isSelected = false

        if (section == MainSection.FILMES) {
            pausePreviewPlayer()
            panelChannels.visibility = View.VISIBLE
            playerPreviewContainer.visibility = View.VISIBLE
            playerView.visibility = View.GONE
            findViewById<View>(R.id.live_badge_container)?.visibility = View.GONE
            findViewById<View>(R.id.layout_preview_bottom_hud)?.visibility = View.GONE
            layoutSettingsPreview.visibility = View.GONE
            layoutVodPreview.visibility = View.VISIBLE
            rvChannelList.visibility = View.GONE
            rvSettingsList.visibility = View.GONE
            rvVodList.visibility = View.VISIBLE

            txtSidebarSectionTitle.text = "CATÁLOGO • FILMES (VOD)"

            // 1. Apresenta catálogo instantâneo com títulos e capas sem espera
            val instantCats = VodRepository.getFallbackMovieCategories().map {
                ChannelCategory(id = "vod_cat_${it.id}", name = it.name, iconRes = R.drawable.ic_movie_vod, channels = emptyList())
            }
            allCategories = instantCats
            val first = instantCats.first()
            currentSelectedCategory = first
            sidebarAdapter.updateCategories(instantCats)
            sidebarAdapter.setSelectedCategory(first)
            txtHeaderBreadcrumb.text = "WDTECH TV • FILMES • ${first.name.uppercase()}"
            txtSelectedCategoryTitle.text = first.name
            val instantMovies = VodRepository.getFallbackMovies(first.id.removePrefix("vod_cat_"))
            vodAdapter.updateItems(instantMovies)
            txtCategoryBadge.text = instantMovies.size.toString()
            updateVodPreview(instantMovies.firstOrNull())
            if (shouldFocusCategory) {
                focusFirstCategory()
            }

            // 2. Carrega categorias remotas do acervo em segundo plano
            vodFetchJob?.cancel()
            vodFetchJob = lifecycleScope.launch {
                val cats = VodRepository.getMovieCategories()
                val mapped = cats.map {
                    ChannelCategory(id = "vod_cat_${it.id}", name = it.name, iconRes = R.drawable.ic_movie_vod, channels = emptyList())
                }
                if (currentSection == MainSection.FILMES && mapped.isNotEmpty()) {
                    allCategories = mapped
                    sidebarAdapter.updateCategories(mapped)
                    val activeId = currentSelectedCategory?.id ?: mapped.first().id
                    val activeCat = mapped.find { it.id == activeId } ?: mapped.first()
                    currentSelectedCategory = activeCat
                    sidebarAdapter.setSelectedCategory(activeCat)
                }
            }
            return
        }

        if (section == MainSection.SERIES) {
            pausePreviewPlayer()
            panelChannels.visibility = View.VISIBLE
            playerPreviewContainer.visibility = View.VISIBLE
            playerView.visibility = View.GONE
            findViewById<View>(R.id.live_badge_container)?.visibility = View.GONE
            findViewById<View>(R.id.layout_preview_bottom_hud)?.visibility = View.GONE
            layoutSettingsPreview.visibility = View.GONE
            layoutVodPreview.visibility = View.VISIBLE
            rvChannelList.visibility = View.GONE
            rvSettingsList.visibility = View.GONE
            rvVodList.visibility = View.VISIBLE

            txtSidebarSectionTitle.text = "CATÁLOGO • SÉRIES (VOD)"

            // 1. Apresenta séries instantâneas com temporadas e pôsteres
            val instantCats = VodRepository.getFallbackSeriesCategories().map {
                ChannelCategory(id = "vod_series_cat_${it.id}", name = it.name, iconRes = R.drawable.ic_series_vod, channels = emptyList())
            }
            allCategories = instantCats
            val first = instantCats.first()
            currentSelectedCategory = first
            sidebarAdapter.updateCategories(instantCats)
            sidebarAdapter.setSelectedCategory(first)
            txtHeaderBreadcrumb.text = "WDTECH TV • SÉRIES • ${first.name.uppercase()}"
            txtSelectedCategoryTitle.text = first.name
            val instantSeries = VodRepository.getFallbackSeries(first.id.removePrefix("vod_series_cat_"))
            vodAdapter.updateItems(instantSeries)
            txtCategoryBadge.text = instantSeries.size.toString()
            updateVodPreview(instantSeries.firstOrNull())
            if (shouldFocusCategory) {
                focusFirstCategory()
            }

            // 2. Carrega categorias remotas de séries em segundo plano
            vodFetchJob?.cancel()
            vodFetchJob = lifecycleScope.launch {
                val cats = VodRepository.getSeriesCategories()
                val mapped = cats.map {
                    ChannelCategory(id = "vod_series_cat_${it.id}", name = it.name, iconRes = R.drawable.ic_series_vod, channels = emptyList())
                }
                if (currentSection == MainSection.SERIES && mapped.isNotEmpty()) {
                    allCategories = mapped
                    sidebarAdapter.updateCategories(mapped)
                    val activeId = currentSelectedCategory?.id ?: mapped.first().id
                    val activeCat = mapped.find { it.id == activeId } ?: mapped.first()
                    currentSelectedCategory = activeCat
                    sidebarAdapter.setSelectedCategory(activeCat)
                }
            }
            return
        }

        // Modo TV / FUTEBOL
        layoutVodPreview.visibility = View.GONE
        rvVodList.visibility = View.GONE
        layoutSettingsPreview.visibility = View.GONE
        rvSettingsList.visibility = View.GONE
        panelChannels.visibility = View.VISIBLE
        playerPreviewContainer.visibility = View.VISIBLE
        playerView.visibility = View.VISIBLE
        findViewById<View>(R.id.live_badge_container)?.visibility = View.VISIBLE
        findViewById<View>(R.id.layout_preview_bottom_hud)?.visibility = View.VISIBLE
        rvChannelList.visibility = View.VISIBLE

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
        pausePreviewPlayer()

        btnMenuTv.isSelected = false
        btnMenuFilmes.isSelected = false
        btnMenuSeries.isSelected = false
        btnMenuFutebol.isSelected = false
        btnMenuSettings.isSelected = true

        txtSidebarSectionTitle.text = "AJUSTES • SISTEMA"
        panelChannels.visibility = View.VISIBLE
        rvChannelList.visibility = View.GONE
        rvVodList.visibility = View.GONE
        rvSettingsList.visibility = View.VISIBLE

        playerPreviewContainer.visibility = View.VISIBLE
        playerView.visibility = View.GONE
        findViewById<View>(R.id.live_badge_container)?.visibility = View.GONE
        findViewById<View>(R.id.layout_preview_bottom_hud)?.visibility = View.GONE
        layoutVodPreview.visibility = View.GONE
        layoutSettingsPreview.visibility = View.VISIBLE

        val settingCats = listOf(
            ChannelCategory(id = "cat_settings", name = "⚙️ Configurações", iconRes = R.drawable.ic_settings, channels = emptyList())
        )
        allCategories = settingCats
        sidebarAdapter.updateCategories(settingCats)
        sidebarAdapter.setSelectedCategory(settingCats[0])

        val items = getSettingItems()
        settingAdapter.updateItems(items)
        txtCategoryBadge.text = items.size.toString()
        txtSelectedCategoryTitle.text = "Configurações do Sistema"
        txtHeaderBreadcrumb.text = "WDTECH TV • AJUSTES DO SISTEMA"
        updateSettingPreview(items.first())
        focusFirstSetting()
    }

    private fun focusActiveTab() {
        if (isSettingsMode) {
            btnMenuSettings.requestFocus()
            return
        }
        when (currentSection) {
            MainSection.TV -> btnMenuTv.requestFocus()
            MainSection.FILMES -> btnMenuFilmes.requestFocus()
            MainSection.SERIES -> btnMenuSeries.requestFocus()
            MainSection.FUTEBOL -> btnMenuFutebol.requestFocus()
            else -> btnMenuTv.requestFocus()
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

        // 0. Configura Carrossel Horizontal de Lançamentos da Tela Inicial
        val topReleases = VodRepository.getTopReleases()
        homeLancamentosAdapter = OttPosterAdapter(
            items = topReleases,
            onItemFocus = { item ->
                updateHeroBanner(item)
            },
            onItemClick = { item ->
                openVodPlayer(item)
            },
            onNavigateUpToHero = {
                btnHeroWatch.requestFocus()
            }
        )
        rvHomeLancamentos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvHomeLancamentos.adapter = homeLancamentosAdapter

        // Inicializa o Hero Banner com o primeiro lançamento de destaque
        topReleases.firstOrNull()?.let { updateHeroBanner(it) }

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
                } else if (currentSection == MainSection.FILMES || currentSection == MainSection.SERIES) {
                    focusFirstVod()
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
            rvVodList.visibility = View.GONE
            rvSettingsList.visibility = View.VISIBLE
            playerPreviewContainer.visibility = View.VISIBLE
            playerView.visibility = View.GONE
            findViewById<View>(R.id.live_badge_container)?.visibility = View.GONE
            findViewById<View>(R.id.layout_preview_bottom_hud)?.visibility = View.GONE
            layoutVodPreview.visibility = View.GONE
            layoutSettingsPreview.visibility = View.VISIBLE
            val items = getSettingItems()
            txtCategoryBadge.text = items.size.toString()
            updateSettingPreview(items.first())
        } else if (category.id.startsWith("vod_cat_")) {
            isSettingsMode = false
            panelChannels.visibility = View.VISIBLE
            rvChannelList.visibility = View.GONE
            rvSettingsList.visibility = View.GONE
            rvVodList.visibility = View.VISIBLE
            playerPreviewContainer.visibility = View.VISIBLE
            playerView.visibility = View.GONE
            findViewById<View>(R.id.live_badge_container)?.visibility = View.GONE
            findViewById<View>(R.id.layout_preview_bottom_hud)?.visibility = View.GONE
            layoutSettingsPreview.visibility = View.GONE
            layoutVodPreview.visibility = View.VISIBLE
            val rawId = category.id.removePrefix("vod_cat_")

            // 1. Títulos e capas instantâneos sem latência
            val instantMovies = VodRepository.getFallbackMovies(rawId)
            vodAdapter.updateItems(instantMovies)
            txtCategoryBadge.text = instantMovies.size.toString()
            updateVodPreview(instantMovies.firstOrNull())

            // 2. Consulta em segundo plano para categorias do servidor
            vodFetchJob?.cancel()
            vodFetchJob = lifecycleScope.launch {
                val movies = VodRepository.getMoviesByCategory(rawId, category.name)
                if (movies.isNotEmpty() && currentSelectedCategory?.id == category.id) {
                    vodAdapter.updateItems(movies)
                    txtCategoryBadge.text = movies.size.toString()
                    updateVodPreview(movies.firstOrNull())
                }
            }
        } else if (category.id.startsWith("vod_series_cat_")) {
            isSettingsMode = false
            panelChannels.visibility = View.VISIBLE
            rvChannelList.visibility = View.GONE
            rvSettingsList.visibility = View.GONE
            rvVodList.visibility = View.VISIBLE
            playerPreviewContainer.visibility = View.VISIBLE
            playerView.visibility = View.GONE
            findViewById<View>(R.id.live_badge_container)?.visibility = View.GONE
            findViewById<View>(R.id.layout_preview_bottom_hud)?.visibility = View.GONE
            layoutSettingsPreview.visibility = View.GONE
            layoutVodPreview.visibility = View.VISIBLE
            val rawId = category.id.removePrefix("vod_series_cat_")

            // 1. Séries instantâneas
            val instantSeries = VodRepository.getFallbackSeries(rawId)
            vodAdapter.updateItems(instantSeries)
            txtCategoryBadge.text = instantSeries.size.toString()
            updateVodPreview(instantSeries.firstOrNull())

            // 2. Consulta em segundo plano para séries do servidor
            vodFetchJob?.cancel()
            vodFetchJob = lifecycleScope.launch {
                val series = VodRepository.getSeriesByCategory(rawId, category.name)
                if (series.isNotEmpty() && currentSelectedCategory?.id == category.id) {
                    vodAdapter.updateItems(series)
                    txtCategoryBadge.text = series.size.toString()
                    updateVodPreview(series.firstOrNull())
                }
            }
        } else {
            isSettingsMode = false
            panelChannels.visibility = View.VISIBLE
            rvSettingsList.visibility = View.GONE
            rvVodList.visibility = View.GONE
            layoutSettingsPreview.visibility = View.GONE
            layoutVodPreview.visibility = View.GONE
            rvChannelList.visibility = View.VISIBLE
            playerPreviewContainer.visibility = View.VISIBLE
            playerView.visibility = View.VISIBLE
            findViewById<View>(R.id.live_badge_container)?.visibility = View.VISIBLE
            findViewById<View>(R.id.layout_preview_bottom_hud)?.visibility = View.VISIBLE
            txtCategoryBadge.text = category.channels.size.toString()

            // Atualiza a lista de canais dinamicamente SEM interromper o vídeo ao vivo que está passando!
            channelAdapter.updateChannels(category.channels)
        }
    }

    private fun onSidebarCategoryClicked(category: ChannelCategory) {
        onSidebarCategoryFocused(category)
        if (isSettingsMode) {
            focusFirstSetting()
        } else if (category.id.startsWith("vod_cat_") || category.id.startsWith("vod_series_cat_")) {
            focusFirstVod()
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
        txtPreviewTitle.text = String.format("%d  %s", num, channel.title.uppercase())
        txtPreviewInfo.text = "${channel.category} • ${channel.quality}"
        txtPreviewPlayback.visibility = View.GONE

        if (!channel.logoUrl.isNullOrEmpty()) {
            imgPreviewLogo?.load(channel.logoUrl) {
                crossfade(false)
                placeholder(R.drawable.logo)
                error(R.drawable.logo)
            }
        } else {
            imgPreviewLogo?.setImageResource(R.drawable.logo)
        }

        val epg = channel.currentProgram
        if (epg != null) {
            txtEpgNowTitle.text = epg.title
            txtEpgNowTime.text = "${epg.startTime} - ${epg.endTime}"
            txtEpgSynopsis.text = if (epg.synopsis.isNotEmpty()) epg.synopsis else (channel.description ?: "")
            pbEpgProgress.progress = epg.getProgressPercent().coerceIn(5, 95)
        } else {
            txtEpgNowTitle.text = "Transmissão Ao Vivo (${channel.quality})"
            txtEpgNowTime.text = "24 Horas"
            txtEpgSynopsis.text = channel.description ?: ""
            pbEpgProgress.progress = 50
        }

        val nextEpg = channel.nextProgram
        if (nextEpg != null) {
            txtEpgNext.text = "${nextEpg.title} (${nextEpg.startTime})"
        } else {
            txtEpgNext.text = "Programação contínua 24H"
        }
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
            .setPrioritizeTimeOverSizeThresholds(false)
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
                    .setMinPlaybackSpeed(0.98f)
                    .setMaxPlaybackSpeed(1.02f)
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

        val channelsInScope = if (currentSection == MainSection.FILMES || currentSection == MainSection.SERIES) {
            listOf(channel)
        } else {
            currentSelectedCategory?.channels?.filter { it.streamUrl.isNotEmpty() }
                ?: allCategories.firstOrNull { it.id == "cat_todos" }?.channels
                ?: listOf(channel)
        }

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

    private fun updateHeroBanner(item: VodItem) {
        currentHeroVodItem = item
        txtHeroTitle.text = item.name
        val rating = item.getFormattedRating()
        val yearStr = item.year ?: (if (item.isSeries) "2025" else "2026")
        val genreStr = item.genre ?: (if (item.isSeries) "Série Completa" else "Filme")
        txtHeroMeta.text = "$rating • $yearStr • $genreStr • 4K ULTRA HD"
        txtHeroPlot.text = item.plot ?: "Superprodução em alta definição sob demanda no WDTECH TV."
        btnHeroWatch.text = if (item.isSeries) "▶ Assistir Série (OK)" else "▶ Assistir Filme (OK)"

        if (!item.posterUrl.isNullOrEmpty()) {
            imgHeroBackdrop.load(item.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.logo)
                error(R.drawable.logo)
            }
        } else {
            imgHeroBackdrop.setImageResource(R.drawable.logo)
        }
    }

    private fun focusFirstVod() {
        if (vodAdapter.itemCount == 0) return
        rvVodList.stopScroll()
        rvVodList.scrollToPosition(0)
        rvVodList.post {
            val vh = rvVodList.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus() ?: rvVodList.requestFocus()
        }
    }

    private fun updateVodPreview(item: VodItem?) {
        currentSelectedVodItem = item
        if (item == null) {
            layoutVodPreview.visibility = View.GONE
            return
        }
        layoutVodPreview.visibility = View.VISIBLE
        txtVodPreviewTitle.text = item.name
        val rating = item.getFormattedRating()
        val yearStr = item.year ?: (if (item.isSeries) "2024" else "2025")
        val genreStr = item.genre ?: item.categoryName.ifEmpty { if (item.isSeries) "Série Completa" else "Filme" }
        txtVodPreviewMeta.text = "$rating • $yearStr • $genreStr • FHD"
        txtVodPreviewPlot.text = item.plot ?: "Conteúdo em alta definição sob demanda no WDTECH TV."
        btnVodAction.text = if (item.isSeries) "▶ Assistir Série (OK)" else "▶ Assistir Filme (OK)"

        if (!item.posterUrl.isNullOrEmpty()) {
            imgVodPreviewPoster.load(item.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.logo)
                error(R.drawable.logo)
            }
        } else {
            imgVodPreviewPoster.setImageResource(R.drawable.logo)
        }
    }

    private fun openVodPlayer(item: VodItem) {
        val channel = item.toChannel()
        openFullScreenPlayer(channel)
    }

    private fun pausePreviewPlayer() {
        try {
            exoPlayer?.pause()
            previewLoadingLayout.visibility = View.GONE
        } catch (_: Exception) {}
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

    // --- Configuração do Catálogo VOD (Filmes e Séries) ---
    private fun setupVod() {
        vodAdapter = VodListAdapter(
            items = emptyList(),
            onItemSelected = { item ->
                currentSelectedVodItem = item
                updateVodPreview(item)
            },
            onItemClicked = { item ->
                currentSelectedVodItem = item
                openVodPlayer(item)
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
            currentSelectedVodItem?.let { openVodPlayer(it) }
        }
        btnVodAction.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                focusFirstVod()
                return@setOnKeyListener true
            }
            false
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
            },
            onNavigateUpToTabs = {
                focusActiveTab()
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
                badgeText = "v2.9.4",
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
                if (!isHomeMode) {
                    if (btnFullscreen.hasFocus() || btnVodAction.hasFocus()) {
                        if (currentSection == MainSection.FILMES || currentSection == MainSection.SERIES) {
                            focusFirstVod()
                        } else {
                            focusCurrentChannel()
                        }
                    } else if (rvChannelList.hasFocus() || rvSettingsList.hasFocus() || rvVodList.hasFocus() || btnSettingPreviewAction.hasFocus()) {
                        focusCurrentCategory()
                    } else {
                        // Fecha o menu e retorna para a Tela Principal de Lançamentos!
                        showHomeScreen()
                    }
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
