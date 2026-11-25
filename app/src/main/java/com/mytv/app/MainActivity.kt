package com.mytv.app

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.adapter.CategorySidebarAdapter
import com.mytv.app.adapter.ChannelListAdapter
import com.mytv.app.adapter.SettingListAdapter
import com.mytv.app.adapter.OttPosterAdapter
import com.mytv.app.adapter.VodListAdapter
import com.mytv.app.adapter.EpisodeAdapter
import com.mytv.app.adapter.SeasonAdapter
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.ArrayList
import kotlin.math.abs

/**
 * MainActivity - Interface Vivaon Moderna (Modelos das Imagens 1, 2 e 3)
 * Hub Central + TV Ao Vivo 3 Painéis + Catálogo VOD 4 Colunas + Ajustes Estáveis.
 */
class MainActivity : AppCompatActivity() {

    enum class ScreenMode {
        HOME_HUB,
        TV,
        VOD_FILMES,
        VOD_SERIES,
        SETTINGS
    }

    private var currentMode: ScreenMode = ScreenMode.HOME_HUB

    // --- 1. TELA HOME HUB (Imagem 2) ---
    private lateinit var layoutHomeHub: View
    private lateinit var hubTv: LinearLayout
    private lateinit var hubFilmes: LinearLayout
    private lateinit var hubSeries: LinearLayout
    private lateinit var hubAjustes: LinearLayout
    private lateinit var btnHomeReload: Button
    private lateinit var txtDeviceKey: TextView
    private lateinit var txtHomeVersion: TextView

    // --- Container Geral de Conteúdo ---
    private lateinit var layoutMainContent: View
    private lateinit var btnToggleMenu: Button
    private lateinit var txtHeaderBreadcrumb: TextView

    // --- 2. TELA TV AO VIVO (Imagem 3) ---
    private lateinit var layoutTvMode: View
    private lateinit var btnRailSearch: ImageView
    private lateinit var btnRailFavorites: ImageView
    private lateinit var btnRailRecents: ImageView
    private lateinit var btnRailCategories: ImageView
    private lateinit var txtTvCategoryTitle: TextView
    private lateinit var txtCategoryBadge: TextView
    private var layoutTvCategoryHeader: LinearLayout? = null
    private lateinit var rvChannelList: RecyclerView
    private lateinit var channelAdapter: ChannelListAdapter

    // Painel Preview do Player & EPG 3 Linhas
    private lateinit var playerPreviewContainer: View
    private lateinit var playerView: PlayerView
    private lateinit var previewLoadingLayout: View
    private lateinit var txtBitrate: TextView
    private lateinit var txtEpgNowTitle: TextView
    private lateinit var txtEpgNowTime: TextView
    private lateinit var txtEpgNextTitle: TextView
    private lateinit var txtEpgNextTime: TextView
    private lateinit var txtEpgThirdTitle: TextView
    private lateinit var txtEpgThirdTime: TextView

    // --- 3. TELA CATÁLOGO VOD FILMES & SÉRIES (Imagem 1) ---
    private lateinit var layoutVodCatalogMode: View
    private lateinit var btnVodSearch: Button
    private lateinit var rvCategorySidebar: RecyclerView
    private lateinit var sidebarAdapter: CategorySidebarAdapter
    private lateinit var btnVodActionMenu: View
    private lateinit var btnVodActionSort: View
    private lateinit var btnVodActionFav: View
    private lateinit var txtVodCategoryTitle: TextView
    private lateinit var txtVodItemsCount: TextView
    private lateinit var rvVodGrid: RecyclerView
    private lateinit var ottVodPosterAdapter: OttPosterAdapter
    private var vodFetchJob: Job? = null

    // --- 4. TELA DE AJUSTES ---
    private lateinit var layoutSettingsMode: View
    private lateinit var rvSettingsList: RecyclerView
    private lateinit var settingAdapter: SettingListAdapter
    private lateinit var layoutSettingsPreview: View
    private lateinit var txtSettingPreviewTitle: TextView
    private lateinit var txtSettingPreviewBadge: TextView
    private lateinit var txtSettingPreviewDesc: TextView
    private lateinit var btnSettingPreviewAction: Button
    private var currentSettingItem: SettingItem? = null

    // --- Variáveis de Estado & Dados ---
    private var tvCategories: List<ChannelCategory> = emptyList()
    private var currentTvCategory: ChannelCategory? = null
    private var currentPlayingChannel: Channel? = null
    private var exoPlayer: ExoPlayer? = null
    private val zapHandler = Handler(Looper.getMainLooper())

    // Watchdog de Buffer & Failover
    private val previewWatchdogHandler = Handler(Looper.getMainLooper())
    private var previewWatchdogRunnable: Runnable? = null
    private var previewCandidates: List<String> = emptyList()
    private var previewCandidateIndex: Int = 0

    // VOD Selection
    private var currentVodCategories: List<ChannelCategory> = emptyList()
    private var currentSelectedVodCategory: ChannelCategory? = null
    private var currentSelectedVodItem: VodItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            initViews()
        } catch (t: Throwable) {
            android.util.Log.e("WDTECH_INIT", "Erro ao inicializar views", t)
        }

        try {
            setupPlayer()
        } catch (t: Throwable) {
            android.util.Log.e("WDTECH_INIT", "Erro ao inicializar player", t)
        }

        setupHubListeners()
        setupTvModeListeners()
        setupVodModeListeners()
        setupSettingsModeListeners()
        setupBackPressed()

        // Inicializa dados locais de canais imediatamente na memória (zero espera)
        try {
            val localCats = ChannelRepository.getCategoriesForSection(MainSection.TV)
            if (localCats.isNotEmpty()) {
                tvCategories = localCats
                currentTvCategory = localCats.first()
            }
        } catch (t: Throwable) {
            android.util.Log.e("WDTECH_INIT", "Erro ao carregar canais locais", t)
        }

        // Inicia na Tela Inicial (Home Hub - Imagem 2)
        showHomeHub()

        // Enriquece grade e EPG em segundo plano
        preloadTvData()

        // Verificação silenciosa de atualização
        AppUpdateManager.checkForUpdates(this, isManualCheck = false)
    }

    private fun initViews() {
        // 1. Home Hub
        layoutHomeHub = findViewById(R.id.layout_home_hub)
        hubTv = findViewById(R.id.hub_tv)
        hubFilmes = findViewById(R.id.hub_filmes)
        hubSeries = findViewById(R.id.hub_series)
        hubAjustes = findViewById(R.id.hub_ajustes)
        btnHomeReload = findViewById(R.id.btn_home_reload)
        txtDeviceKey = findViewById(R.id.txt_device_key)
        txtHomeVersion = findViewById(R.id.txt_home_version)

        // Topo / Breadcrumb
        layoutMainContent = findViewById(R.id.layout_main_content)
        btnToggleMenu = findViewById(R.id.btn_toggle_menu)
        txtHeaderBreadcrumb = findViewById(R.id.txt_header_breadcrumb)

        // 2. TV Ao Vivo Mode
        layoutTvMode = findViewById(R.id.layout_tv_mode)
        btnRailSearch = findViewById(R.id.btn_rail_search)
        btnRailFavorites = findViewById(R.id.btn_rail_favorites)
        btnRailRecents = findViewById(R.id.btn_rail_recents)
        btnRailCategories = findViewById(R.id.btn_rail_categories)
        txtTvCategoryTitle = findViewById(R.id.txt_tv_category_title)
        txtCategoryBadge = findViewById(R.id.txt_category_badge)
        layoutTvCategoryHeader = findViewById(R.id.layout_tv_category_header)
        rvChannelList = findViewById(R.id.rv_channel_list)
        rvChannelList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Player Preview e EPG
        playerPreviewContainer = findViewById(R.id.player_preview_container)
        playerView = findViewById(R.id.player_preview_view)
        previewLoadingLayout = findViewById(R.id.preview_loading_layout)
        txtBitrate = findViewById(R.id.txt_bitrate)
        txtEpgNowTitle = findViewById(R.id.txt_epg_now_title)
        txtEpgNowTime = findViewById(R.id.txt_epg_now_time)
        txtEpgNextTitle = findViewById(R.id.txt_epg_next_title)
        txtEpgNextTime = findViewById(R.id.txt_epg_next_time)
        txtEpgThirdTitle = findViewById(R.id.txt_epg_third_title)
        txtEpgThirdTime = findViewById(R.id.txt_epg_third_time)

        // 3. VOD Catalog Mode
        layoutVodCatalogMode = findViewById(R.id.layout_vod_catalog_mode)
        btnVodSearch = findViewById(R.id.btn_vod_search)
        rvCategorySidebar = findViewById(R.id.rv_category_sidebar)
        rvCategorySidebar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        btnVodActionMenu = findViewById(R.id.btn_vod_action_menu)
        btnVodActionSort = findViewById(R.id.btn_vod_action_sort)
        btnVodActionFav = findViewById(R.id.btn_vod_action_fav)
        txtVodCategoryTitle = findViewById(R.id.txt_vod_category_title)
        txtVodItemsCount = findViewById(R.id.txt_vod_items_count)
        rvVodGrid = findViewById(R.id.rv_vod_grid)
        rvVodGrid.layoutManager = GridLayoutManager(this, 4)

        // 4. Settings Mode
        layoutSettingsMode = findViewById(R.id.layout_settings_mode)
        rvSettingsList = findViewById(R.id.rv_settings_list)
        rvSettingsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutSettingsPreview = findViewById(R.id.layout_settings_preview)
        txtSettingPreviewTitle = findViewById(R.id.txt_setting_preview_title)
        txtSettingPreviewBadge = findViewById(R.id.txt_setting_preview_badge)
        txtSettingPreviewDesc = findViewById(R.id.txt_setting_preview_desc)
        btnSettingPreviewAction = findViewById(R.id.btn_setting_preview_action)

        // Adapters Setup
        setupAdapters()
    }

    private fun setupAdapters() {
        // Channel Adapter
        channelAdapter = ChannelListAdapter(
            channels = emptyList(),
            onChannelSelected = { channel ->
                onChannelFocused(channel)
            },
            onChannelClicked = { channel ->
                openFullScreenPlayer(channel)
            },
            onNavigateToCategories = {
                btnRailCategories.requestFocus()
            },
            onNavigateToPlayer = {
                playerPreviewContainer.requestFocus()
            }
        )
        rvChannelList.adapter = channelAdapter

        // VOD Sidebar Adapter
        sidebarAdapter = CategorySidebarAdapter(
            categories = emptyList(),
            onCategoryFocused = { cat ->
                onVodCategorySelected(cat)
            },
            onCategoryClicked = { cat ->
                onVodCategorySelected(cat)
                focusFirstVodGrid()
            },
            onNavigateRightToContent = {
                focusFirstVodGrid()
            },
            onNavigateUpToTabs = {
                btnVodSearch.requestFocus()
            }
        )
        rvCategorySidebar.adapter = sidebarAdapter

        // VOD Posters Grid Adapter
        ottVodPosterAdapter = OttPosterAdapter(
            items = emptyList(),
            onItemFocus = { item ->
                currentSelectedVodItem = item
            },
            onItemClick = { item ->
                currentSelectedVodItem = item
                if (item.isSeries) {
                    showSeriesEpisodesDialog(item)
                } else {
                    openVodPlayer(item)
                }
            },
            onNavigateUpToTabs = {
                btnVodActionMenu.requestFocus()
            }
        )
        rvVodGrid.adapter = ottVodPosterAdapter

        // Settings Adapter
        settingAdapter = SettingListAdapter(
            items = emptyList(),
            onSettingFocused = { item ->
                currentSettingItem = item
                updateSettingPreview(item)
            },
            onSettingClicked = { item ->
                currentSettingItem = item
                executeSettingAction(item)
            },
            onNavigateToCategories = {
                showHomeHub()
            },
            onNavigateToPreview = {
                btnSettingPreviewAction.requestFocus()
            },
            onNavigateUpToTabs = {
                btnToggleMenu.requestFocus()
            }
        )
        rvSettingsList.adapter = settingAdapter
    }

    // =========================================================================
    // 1. HOME HUB (Imagem 2)
    // =========================================================================
    fun showHomeHub() {
        currentMode = ScreenMode.HOME_HUB
        pausePreviewPlayer()

        layoutHomeHub.visibility = View.VISIBLE
        layoutMainContent.visibility = View.GONE
        layoutTvMode.visibility = View.GONE
        layoutVodCatalogMode.visibility = View.GONE
        layoutSettingsMode.visibility = View.GONE

        txtHomeVersion.text = "Versão 3.1.0"
        txtDeviceKey.text = "Chave do dispositivo: " + getDeviceKey(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                hubTv.requestFocus()
            }
        }, 100)
    }

    private fun setupHubListeners() {
        hubTv.setOnClickListener {
            openTvMode()
        }
        hubFilmes.setOnClickListener {
            openVodMode(isSeries = false)
        }
        hubSeries.setOnClickListener {
            openVodMode(isSeries = true)
        }
        hubAjustes.setOnClickListener {
            openSettingsMode()
        }
        btnHomeReload.setOnClickListener {
            reloadApplicationData()
        }

        btnToggleMenu.setOnClickListener {
            showHomeHub()
        }
    }

    private fun reloadApplicationData() {
        Toast.makeText(this, "Recarregando canais e catálogos...", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            VodRepository.clearCache()
            preloadTvData()
            Toast.makeText(this@MainActivity, "WDTECH TV: Aplicativo recarregado com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDeviceKey(context: Context): String {
        return try {
            val prefs = context.getSharedPreferences("wdtech_tv_settings", Context.MODE_PRIVATE)
            var key = prefs.getString("device_key", null)
            if (key == null) {
                val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "WDTECH"
                val hash = (androidId + "WDTECH_TV_VIVAON").hashCode().toLong() and 0xFFFFFFL
                key = String.format("%06X", hash)
                prefs.edit().putString("device_key", key).apply()
            }
            key ?: "SS5A88"
        } catch (_: Exception) {
            "SS5A88"
        }
    }

    // =========================================================================
    // 2. TV AO VIVO (Imagem 3)
    // =========================================================================
    fun openTvMode(targetCategory: ChannelCategory? = null) {
        currentMode = ScreenMode.TV

        layoutHomeHub.visibility = View.GONE
        layoutMainContent.visibility = View.VISIBLE
        layoutTvMode.visibility = View.VISIBLE
        layoutVodCatalogMode.visibility = View.GONE
        layoutSettingsMode.visibility = View.GONE

        txtHeaderBreadcrumb.text = "WDTECH TV • TV AO VIVO"

        if (tvCategories.isNotEmpty()) {
            val cat = targetCategory ?: currentTvCategory ?: tvCategories.first()
            setTvCategory(cat)
        } else {
            preloadTvData {
                val cat = targetCategory ?: tvCategories.firstOrNull()
                cat?.let { setTvCategory(it) }
            }
        }
    }

    private fun preloadTvData(onLoaded: (() -> Unit)? = null) {
        lifecycleScope.launch {
            val loaded = EpgRepository.enrichCategories(
                ChannelRepository.getCategoriesForSection(MainSection.TV)
            )
            tvCategories = loaded
            if (currentTvCategory == null && loaded.isNotEmpty()) {
                currentTvCategory = loaded.first()
            }
            onLoaded?.invoke()
        }
    }

    private fun setTvCategory(category: ChannelCategory) {
        currentTvCategory = category
        txtTvCategoryTitle.text = category.name
        txtCategoryBadge.text = category.channels.size.toString()
        txtHeaderBreadcrumb.text = "WDTECH TV • TV AO VIVO • ${category.name.uppercase()}"

        channelAdapter.updateChannels(category.channels)

        val firstChannel = category.channels.firstOrNull()
        if (firstChannel != null) {
            updateEpgRows(firstChannel)
            if (firstChannel.id != currentPlayingChannel?.id) {
                playChannelPreview(firstChannel)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed && currentMode == ScreenMode.TV) {
                rvChannelList.scrollToPosition(0)
                rvChannelList.postDelayed({
                    val vh = rvChannelList.findViewHolderForAdapterPosition(0)
                    vh?.itemView?.requestFocus() ?: rvChannelList.requestFocus()
                }, 100)
            }
        }, 150)
    }

    private fun setupTvModeListeners() {
        btnRailSearch.setOnClickListener {
            Toast.makeText(this, "Navegue pela lista de canais", Toast.LENGTH_SHORT).show()
            focusFirstChannel()
        }
        btnRailFavorites.setOnClickListener {
            val favs = tvCategories.find { it.id == "cat_favoritos" } ?: tvCategories.firstOrNull()
            favs?.let { setTvCategory(it) }
        }
        btnRailRecents.setOnClickListener {
            val todos = tvCategories.find { it.id == "cat_tv_todos" } ?: tvCategories.firstOrNull()
            todos?.let { setTvCategory(it) }
        }

        val openCats = View.OnClickListener {
            showCategoryPickerDialog()
        }
        btnRailCategories.setOnClickListener(openCats)
        layoutTvCategoryHeader?.setOnClickListener(openCats)
        txtTvCategoryTitle.setOnClickListener(openCats)

        val railKeyListener = View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                focusCurrentChannel()
                return@OnKeyListener true
            }
            false
        }
        btnRailSearch.setOnKeyListener(railKeyListener)
        btnRailFavorites.setOnKeyListener(railKeyListener)
        btnRailRecents.setOnKeyListener(railKeyListener)
        btnRailCategories.setOnKeyListener(railKeyListener)

        // D-Pad Esquerda na lista de canais move foco para o botão de categorias
        rvChannelList.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                btnRailCategories.requestFocus()
                return@setOnKeyListener true
            }
            false
        }

        // Clique na tela do player expande para tela cheia (Requisito Explícito do Usuário)
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
    }

    private fun showCategoryPickerDialog() {
        if (tvCategories.isEmpty()) return

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_category_picker)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.52).toInt().coerceAtLeast(420),
            (resources.displayMetrics.heightPixels * 0.85).toInt()
        )

        val rv = dialog.findViewById<RecyclerView>(R.id.rv_dialog_categories)
        val btnClose = dialog.findViewById<Button>(R.id.btn_dialog_close_cats)

        btnClose?.setOnClickListener { dialog.dismiss() }

        rv?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = com.mytv.app.adapter.CategoryPickerAdapter(
            categories = tvCategories,
            currentSelectedId = currentTvCategory?.id,
            onCategorySelected = { selected ->
                dialog.dismiss()
                setTvCategory(selected)
            }
        )
        rv?.adapter = adapter

        val currentIdx = tvCategories.indexOfFirst { it.id == currentTvCategory?.id }.coerceAtLeast(0)
        rv?.scrollToPosition(currentIdx)

        dialog.show()

        rv?.postDelayed({
            val vh = rv.findViewHolderForAdapterPosition(currentIdx)
            vh?.itemView?.requestFocus() ?: rv.requestFocus()
        }, 150)
    }

    private fun cycleNextCategory() {
        if (tvCategories.isEmpty()) return
        val currentIdx = tvCategories.indexOfFirst { it.id == currentTvCategory?.id }.coerceAtLeast(0)
        val nextIdx = (currentIdx + 1) % tvCategories.size
        setTvCategory(tvCategories[nextIdx])
    }

    private fun onChannelFocused(channel: Channel) {
        updateEpgRows(channel)

        zapHandler.removeCallbacksAndMessages(null)
        zapHandler.postDelayed({
            if (currentPlayingChannel?.id != channel.id && currentMode == ScreenMode.TV) {
                playChannelPreview(channel)
            }
        }, 350)
    }

    private fun updateEpgRows(channel: Channel) {
        val curr = channel.currentProgram
        if (curr != null) {
            txtEpgNowTitle.text = curr.title
            txtEpgNowTime.text = "${curr.startTime}-${curr.endTime}"
        } else {
            txtEpgNowTitle.text = "Transmissão Ao Vivo (${channel.quality})"
            txtEpgNowTime.text = "Ao Vivo"
        }

        val next = channel.nextProgram
        if (next != null) {
            txtEpgNextTitle.text = next.title
            txtEpgNextTime.text = "${next.startTime}-${next.endTime}"
        } else {
            txtEpgNextTitle.text = "Programação Especial WDTECH"
            txtEpgNextTime.text = "A Seguir"
        }

        txtEpgThirdTitle.text = "Transmissão 24 Horas"
        txtEpgThirdTime.text = "Em Breve"
    }

    // =========================================================================
    // 3. CATÁLOGO VOD FILMES & SÉRIES (Imagem 1)
    // =========================================================================
    fun openVodMode(isSeries: Boolean) {
        currentMode = if (isSeries) ScreenMode.VOD_SERIES else ScreenMode.VOD_FILMES
        pausePreviewPlayer()

        layoutHomeHub.visibility = View.GONE
        layoutMainContent.visibility = View.VISIBLE
        layoutTvMode.visibility = View.GONE
        layoutVodCatalogMode.visibility = View.VISIBLE
        layoutSettingsMode.visibility = View.GONE

        val modeTitle = if (isSeries) "SÉRIES" else "FILMES"
        txtHeaderBreadcrumb.text = "WDTECH TV • $modeTitle"

        // 1. Apresenta categorias instantâneas de plataformas
        val instantCats = if (isSeries) {
            VodRepository.getFallbackSeriesCategories().map {
                ChannelCategory(id = "vod_series_cat_${it.id}", name = it.name, iconRes = R.drawable.ic_series_vod, channels = emptyList())
            }
        } else {
            VodRepository.getFallbackMovieCategories().map {
                ChannelCategory(id = "vod_cat_${it.id}", name = it.name, iconRes = R.drawable.ic_movie_vod, channels = emptyList())
            }
        }

        currentVodCategories = instantCats
        sidebarAdapter.updateCategories(instantCats)

        val firstCat = instantCats.firstOrNull()
        if (firstCat != null) {
            sidebarAdapter.setSelectedCategory(firstCat)
            onVodCategorySelected(firstCat)
        }

        // Foco inicial na categoria
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed && (currentMode == ScreenMode.VOD_FILMES || currentMode == ScreenMode.VOD_SERIES)) {
                focusFirstCategory()
            }
        }, 150)

        // 2. Consulta remota de categorias do servidor em segundo plano
        vodFetchJob?.cancel()
        vodFetchJob = lifecycleScope.launch {
            val remoteCats = if (isSeries) {
                VodRepository.getSeriesCategories().map {
                    ChannelCategory(id = "vod_series_cat_${it.id}", name = it.name, iconRes = R.drawable.ic_series_vod, channels = emptyList())
                }
            } else {
                VodRepository.getMovieCategories().map {
                    ChannelCategory(id = "vod_cat_${it.id}", name = it.name, iconRes = R.drawable.ic_movie_vod, channels = emptyList())
                }
            }
            if (remoteCats.isNotEmpty() && (currentMode == ScreenMode.VOD_FILMES || currentMode == ScreenMode.VOD_SERIES)) {
                currentVodCategories = remoteCats
                sidebarAdapter.updateCategories(remoteCats)
                val active = remoteCats.find { it.id == currentSelectedVodCategory?.id } ?: remoteCats.first()
                sidebarAdapter.setSelectedCategory(active)
            }
        }
    }

    private fun onVodCategorySelected(category: ChannelCategory) {
        currentSelectedVodCategory = category
        txtVodCategoryTitle.text = category.name
        txtHeaderBreadcrumb.text = "WDTECH TV • ${if (currentMode == ScreenMode.VOD_SERIES) "SÉRIES" else "FILMES"} • ${category.name.uppercase()}"

        val rawId = category.id.removePrefix("vod_cat_").removePrefix("vod_series_cat_")
        val isSeries = (currentMode == ScreenMode.VOD_SERIES)

        // Carrega itens instantâneos da categoria para nunca ficar tela preta/vazia
        val instantItems = if (isSeries) {
            VodRepository.getFallbackSeries(rawId)
        } else {
            VodRepository.getFallbackMovies(rawId)
        }
        ottVodPosterAdapter.updateItems(instantItems)
        txtVodItemsCount.text = "${instantItems.size} Títulos"

        // Busca acervo completo no servidor Xtream Codes em segundo plano
        vodFetchJob?.cancel()
        vodFetchJob = lifecycleScope.launch {
            val remoteItems = if (isSeries) {
                VodRepository.getSeriesByCategory(rawId, category.name)
            } else {
                VodRepository.getMoviesByCategory(rawId, category.name)
            }
            if (remoteItems.isNotEmpty() && currentSelectedVodCategory?.id == category.id) {
                ottVodPosterAdapter.updateItems(remoteItems)
                txtVodItemsCount.text = "${remoteItems.size} Títulos"
            }
        }
    }

    private fun setupVodModeListeners() {
        btnVodSearch.setOnClickListener {
            Toast.makeText(this, "Pesquisa de títulos em catálogo", Toast.LENGTH_SHORT).show()
        }
        btnVodActionMenu.setOnClickListener {
            showHomeHub()
        }
        btnVodActionSort.setOnClickListener {
            Toast.makeText(this, "Ordenado por: Mais Populares", Toast.LENGTH_SHORT).show()
        }
        btnVodActionFav.setOnClickListener {
            Toast.makeText(this, "Favoritos do Catálogo", Toast.LENGTH_SHORT).show()
        }

        btnVodSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                focusFirstCategory()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun openVodPlayer(item: VodItem) {
        if (item.isSeries) {
            lifecycleScope.launch {
                val seasons = VodRepository.getSeriesInfo(item.id)
                val firstEp = seasons.firstOrNull()?.episodes?.firstOrNull()
                if (firstEp != null) {
                    val epChannel = Channel(
                        id = "vod_ep_${firstEp.id}",
                        number = "🍿",
                        title = "${item.name} - ${firstEp.getDisplayName()}",
                        category = "Séries • ${item.categoryName}",
                        streamUrl = firstEp.streamUrl,
                        logoUrl = item.posterUrl,
                        description = firstEp.plot ?: item.plot ?: "",
                        quality = "FHD",
                        isHls = firstEp.streamUrl.contains(".m3u8")
                    )
                    openFullScreenPlayer(epChannel)
                } else {
                    showSeriesEpisodesDialog(item)
                }
            }
        } else {
            val channel = item.toChannel()
            openFullScreenPlayer(channel)
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
        val imgPoster = dialog.findViewById<ImageView>(R.id.img_dialog_series_cover)
        val txtSynopsis = dialog.findViewById<TextView>(R.id.txt_dialog_series_plot)
        val rvSeasons = dialog.findViewById<RecyclerView>(R.id.rv_dialog_seasons)
        val rvEpisodes = dialog.findViewById<RecyclerView>(R.id.rv_dialog_episodes)
        val btnClose = dialog.findViewById<Button>(R.id.btn_dialog_close)

        txtTitle?.text = series.name
        txtGenre?.text = "${series.getFormattedRating()} • ${series.year ?: "2025"} • ${series.genre ?: "Série"}"
        txtSynopsis?.text = series.plot ?: "Superprodução em alta definição sob demanda no WDTECH TV."

        if (imgPoster != null && !series.posterUrl.isNullOrEmpty()) {
            imgPoster.load(series.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.logo)
                error(R.drawable.logo)
            }
        }

        btnClose?.setOnClickListener {
            dialog.dismiss()
        }

        rvSeasons?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEpisodes?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val episodeAdapter = EpisodeAdapter(emptyList()) { ep ->
            dialog.dismiss()
            val epChannel = Channel(
                id = "vod_ep_${ep.id}",
                number = "🍿",
                title = "${series.name} - ${ep.getDisplayName()}",
                category = "Séries • ${series.categoryName}",
                streamUrl = ep.streamUrl,
                logoUrl = series.posterUrl,
                description = ep.plot ?: series.plot ?: "",
                quality = "FHD",
                isHls = ep.streamUrl.contains(".m3u8")
            )
            openFullScreenPlayer(epChannel)
        }
        rvEpisodes?.adapter = episodeAdapter

        val seasonAdapter = SeasonAdapter(emptyList()) { season ->
            episodeAdapter.updateEpisodes(season.episodes)
        }
        rvSeasons?.adapter = seasonAdapter

        lifecycleScope.launch {
            val seasons = VodRepository.getSeriesInfo(series.id)
            if (!isFinishing && !isDestroyed && seasons.isNotEmpty()) {
                seasonAdapter.updateSeasons(seasons)
                val firstSeason = seasons.firstOrNull()
                if (firstSeason != null) {
                    episodeAdapter.updateEpisodes(firstSeason.episodes)
                }
            }
        }

        dialog.show()
    }

    // =========================================================================
    // 4. AJUSTES (Settings Mode)
    // =========================================================================
    fun openSettingsMode() {
        currentMode = ScreenMode.SETTINGS
        pausePreviewPlayer()

        layoutHomeHub.visibility = View.GONE
        layoutMainContent.visibility = View.VISIBLE
        layoutTvMode.visibility = View.GONE
        layoutVodCatalogMode.visibility = View.GONE
        layoutSettingsMode.visibility = View.VISIBLE

        txtHeaderBreadcrumb.text = "WDTECH TV • AJUSTES DO SISTEMA"

        val items = getSettingItems()
        settingAdapter.updateItems(items)
        if (items.isNotEmpty()) {
            currentSettingItem = items.first()
            updateSettingPreview(items.first())
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed && currentMode == ScreenMode.SETTINGS) {
                focusFirstSetting()
            }
        }, 100)
    }

    private fun setupSettingsModeListeners() {
        btnSettingPreviewAction.setOnClickListener {
            currentSettingItem?.let { executeSettingAction(it) }
        }
        btnSettingPreviewAction.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                focusCurrentSetting()
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
                badgeText = "v2.9.9",
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
                description = "Mede a resposta e estabilidade da sua conexão com o servidor de transmissão WDTECH TV (cdn9.my:80). Pressione para medir a latência em tempo real.",
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
    }

    private fun notifySettingItemUpdated(item: SettingItem) {
        val idx = settingAdapter.getItems().indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            settingAdapter.notifyItemChanged(idx)
        }
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
                notifySettingItemUpdated(item)
                Toast.makeText(this, "Aceleração Cloudflare: $label", Toast.LENGTH_SHORT).show()
            }
            "setting_update" -> {
                Toast.makeText(this, "Verificando atualizações no servidor...", Toast.LENGTH_SHORT).show()
                AppUpdateManager.checkForUpdates(this, isManualCheck = true)
            }
            "setting_network" -> {
                txtSettingPreviewBadge.text = "MEDINDO..."
                btnSettingPreviewAction.text = "Medindo Latência..."
                lifecycleScope.launch {
                    val result = AppSettings.testConnectionLatency()
                    if (result.first) {
                        val ping = result.second
                        val quality = if (ping < 60) "Excelente" else if (ping < 120) "Boa" else "Instável"
                        txtSettingPreviewBadge.text = "${ping}ms • $quality"
                        txtSettingPreviewDesc.text = "Servidor CDN WDTECH TV (cdn9.my:80) online!\n\n• Latência: ${ping}ms ($quality)\n• Status: Online e Transmitindo\n• Conexão pronta para alta definição."
                        Toast.makeText(this@MainActivity, "Ping com Servidor: ${ping}ms ($quality)", Toast.LENGTH_LONG).show()
                    } else {
                        txtSettingPreviewBadge.text = "FALHA"
                        txtSettingPreviewDesc.text = "Não foi possível conectar ao servidor.\nVerifique a sua internet."
                        Toast.makeText(this@MainActivity, "Erro ao testar conexão.", Toast.LENGTH_SHORT).show()
                    }
                    btnSettingPreviewAction.text = item.actionButtonText
                    notifySettingItemUpdated(item)
                }
            }
            "setting_buffer" -> {
                val nextMode = AppSettings.cycleBufferMode(this)
                val label = AppSettings.getBufferLabel(nextMode)
                item.badgeText = label.split(" ")[0].uppercase()
                item.subtitle = "Modo ativo: $label"
                item.actionButtonText = "Modo Ativo: $label (OK)"
                updateSettingPreview(item)
                notifySettingItemUpdated(item)
                Toast.makeText(this, "Buffer configurado para: $label", Toast.LENGTH_SHORT).show()
            }
            "setting_aspect" -> {
                val nextAspect = AppSettings.cycleAspectRatio(this)
                val label = AppSettings.getAspectRatioLabel(nextAspect)
                playerView.resizeMode = nextAspect
                item.badgeText = label.split(" ")[0].uppercase()
                item.subtitle = "Modo ativo: $label"
                item.actionButtonText = "Formato: $label (OK)"
                updateSettingPreview(item)
                notifySettingItemUpdated(item)
                Toast.makeText(this, "Formato alterado: $label", Toast.LENGTH_SHORT).show()
            }
            "setting_cache" -> {
                txtSettingPreviewBadge.text = "LIMPANDO..."
                btnSettingPreviewAction.text = "Limpando Memória..."
                lifecycleScope.launch {
                    val bytesFreed = AppSettings.clearCache(this@MainActivity)
                    val mbFreed = String.format("%.2f", bytesFreed / (1024.0 * 1024.0))
                    txtSettingPreviewBadge.text = "LIMPO"
                    txtSettingPreviewDesc.text = "Memória limpa com sucesso!\n\n• Liberado: ${mbFreed} MB\n• Sistema otimizado para reprodução contínua."
                    btnSettingPreviewAction.text = item.actionButtonText
                    Toast.makeText(this@MainActivity, "Memória limpa! ${mbFreed} MB liberados.", Toast.LENGTH_LONG).show()
                    notifySettingItemUpdated(item)
                }
            }
            "setting_exit" -> {
                showExitConfirmation()
            }
        }
    }

    // =========================================================================
    // PLAYER & PREVIEW ENGINE (Multi-CDN Failover & Watchdog)
    // =========================================================================
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
                    Player.STATE_IDLE -> {}
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                cancelPreviewWatchdog()
                currentPlayingChannel?.let { triggerPreviewFailover(it, "Erro no player: ${error.errorCodeName}") }
            }
        })

        playerView.player = player
        exoPlayer = player
    }

    private fun cancelPreviewWatchdog() {
        previewWatchdogRunnable?.let { previewWatchdogHandler.removeCallbacks(it) }
        previewWatchdogRunnable = null
    }

    private fun startPreviewWatchdog(channel: Channel) {
        cancelPreviewWatchdog()
        val runnable = Runnable {
            if (!isFinishing && !isDestroyed && (exoPlayer?.playbackState == Player.STATE_BUFFERING || exoPlayer?.playbackState == Player.STATE_IDLE)) {
                android.util.Log.w("WDTECH_NETFLIX", "Preview stall detectado (3.5s) no canal ${channel.title}. Acionando auto-failover instantâneo...")
                triggerPreviewFailover(channel, "Stall de buffer no preview (3.5s)")
            }
        }
        previewWatchdogRunnable = runnable
        previewWatchdogHandler.postDelayed(runnable, 3500L)
    }

    private fun buildStreamCandidates(channel: Channel): List<String> {
        val rawList = mutableListOf<String>()
        val primary = channel.streamUrl
        if (primary.isNotEmpty()) rawList.add(primary)
        for (b in channel.backupUrls) {
            if (b.isNotEmpty() && !rawList.contains(b)) rawList.add(b)
        }

        // Prioriza streams .ts e cdn9.my de alta velocidade e resposta direta
        val sortedList = rawList.sortedBy { url ->
            if (url.contains("cdn9.my") && url.endsWith(".ts")) 0
            else if (url.contains("cdn9.my")) 1
            else if (url.contains("uniredes") || url.contains("186.219") || url.contains("170.83") || url.contains("jmvstream")) 2
            else 3
        }

        val result = mutableListOf<String>()
        for (u in sortedList) {
            if (!result.contains(u)) result.add(u)
            if (u.contains("cdn9.my")) {
                val alt = if (u.endsWith(".m3u8")) u.replace(".m3u8", ".ts") else if (u.endsWith(".ts")) u.replace(".ts", ".m3u8") else "$u.ts"
                if (alt != null && !result.contains(alt)) result.add(alt)
            }
        }
        return result
    }

    private fun triggerPreviewFailover(channel: Channel, reason: String) {
        if (isFinishing || isDestroyed) return
        cancelPreviewWatchdog()
        if (previewCandidates.isEmpty()) {
            previewCandidates = buildStreamCandidates(channel)
        }
        previewCandidateIndex = (previewCandidateIndex + 1) % previewCandidates.size
        val nextUrl = previewCandidates[previewCandidateIndex]
        android.util.Log.i("WDTECH_NETFLIX", "Preview failover [Candidato ${previewCandidateIndex + 1}/${previewCandidates.size}] para: $nextUrl ($reason)")
        previewLoadingLayout.visibility = View.VISIBLE
        playChannelPreviewInternal(channel, nextUrl)
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
        } catch (_: Throwable) {
            cancelPreviewWatchdog()
            previewLoadingLayout.visibility = View.GONE
        }
    }

    private fun openFullScreenPlayer(channel: Channel) {
        zapHandler.removeCallbacksAndMessages(null)
        cancelPreviewWatchdog()
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        previewLoadingLayout.visibility = View.GONE

        val channelsInScope = if (currentMode == ScreenMode.TV) {
            currentTvCategory?.channels?.filter { it.streamUrl.isNotEmpty() }
                ?: listOf(channel)
        } else {
            listOf(channel)
        }

        val currentIndex = channelsInScope.indexOfFirst { it.id == channel.id }.coerceAtLeast(0)

        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_CHANNEL, channel)
            putExtra(PlayerActivity.EXTRA_CHANNEL_LIST, ArrayList(channelsInScope))
            putExtra(PlayerActivity.EXTRA_CHANNEL_INDEX, currentIndex)
        }
        startActivity(intent)
    }

    private fun pausePreviewPlayer() {
        try {
            cancelPreviewWatchdog()
            zapHandler.removeCallbacksAndMessages(null)
            exoPlayer?.pause()
            previewLoadingLayout.visibility = View.GONE
        } catch (_: Exception) {}
    }

    // =========================================================================
    // D-PAD FOCUS HELPERS
    // =========================================================================
    private fun focusFirstCategory() {
        if (sidebarAdapter.itemCount == 0) return
        rvCategorySidebar.stopScroll()
        rvCategorySidebar.scrollToPosition(0)
        rvCategorySidebar.post {
            val vh = rvCategorySidebar.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus() ?: rvCategorySidebar.requestFocus()
        }
    }

    private fun focusFirstVodGrid() {
        if (ottVodPosterAdapter.itemCount == 0) return
        rvVodGrid.stopScroll()
        rvVodGrid.scrollToPosition(0)
        rvVodGrid.post {
            val vh = rvVodGrid.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus() ?: rvVodGrid.requestFocus()
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

    private fun focusCurrentSetting() {
        val idx = settingAdapter.getSelectedIndex().coerceAtLeast(0)
        rvSettingsList.stopScroll()
        rvSettingsList.scrollToPosition(idx)
        rvSettingsList.post {
            val vh = rvSettingsList.findViewHolderForAdapterPosition(idx)
            vh?.itemView?.requestFocus() ?: rvSettingsList.requestFocus()
        }
    }

    // =========================================================================
    // CONTROLE DE BOTÃO VOLTAR (D-PAD BACK) & DIÁLOGO DE SAÍDA
    // =========================================================================
    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (currentMode) {
                    ScreenMode.HOME_HUB -> {
                        showExitConfirmation()
                    }
                    ScreenMode.TV -> {
                        if (playerPreviewContainer.hasFocus() || playerView.hasFocus()) {
                            focusCurrentChannel()
                        } else {
                            showHomeHub()
                        }
                    }
                    ScreenMode.VOD_FILMES, ScreenMode.VOD_SERIES -> {
                        if (rvVodGrid.hasFocus()) {
                            focusFirstCategory()
                        } else {
                            showHomeHub()
                        }
                    }
                    ScreenMode.SETTINGS -> {
                        if (btnSettingPreviewAction.hasFocus()) {
                            focusCurrentSetting()
                        } else {
                            showHomeHub()
                        }
                    }
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

        btnSim?.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }

        btnNao?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            btnNao?.requestFocus()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (exoPlayer == null) {
            setupPlayer()
        }
        if (currentMode == ScreenMode.TV) {
            currentPlayingChannel?.let { playChannelPreview(it) }
        }
    }

    override fun onPause() {
        super.onPause()
        cancelPreviewWatchdog()
        zapHandler.removeCallbacksAndMessages(null)
        exoPlayer?.pause()
    }

    override fun onStop() {
        super.onStop()
        cancelPreviewWatchdog()
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
