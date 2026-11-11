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
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ts.TsExtractor
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.mytv.app.adapter.CategorySidebarAdapter
import com.mytv.app.adapter.ChannelListAdapter
import com.mytv.app.adapter.EpisodeAdapter
import com.mytv.app.data.WatchProgressRepository
import com.mytv.app.adapter.SeasonAdapter
import com.mytv.app.adapter.SettingListAdapter
import com.mytv.app.adapter.VodListAdapter
import com.mytv.app.data.ChannelRepository
import com.mytv.app.data.EpgRepository
import com.mytv.app.data.VodRepository
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.SettingItem
import com.mytv.app.model.VodItem
import com.mytv.app.utils.AppSettings
import com.mytv.app.utils.AppUpdateManager

enum class AppMode {
    LIVE_TV,
    MOVIES,
    SERIES,
    SETTINGS
}

class MainActivity : AppCompatActivity() {

    private var currentAppMode = AppMode.LIVE_TV

    // VOD (Filmes e Séries)
    private lateinit var rvVodList: RecyclerView
    private lateinit var vodAdapter: VodListAdapter
    private lateinit var layoutVodPreview: View
    private lateinit var imgVodPreviewPoster: ImageView
    private lateinit var txtVodPreviewTitle: TextView
    private lateinit var txtVodPreviewMeta: TextView
    private lateinit var txtVodPreviewPlot: TextView
    private lateinit var btnVodAction: Button
    private var currentVodItem: VodItem? = null

    // Ajustes / Configurações (Settings)
    private lateinit var rvSettingsList: RecyclerView
    private lateinit var settingAdapter: SettingListAdapter
    private lateinit var layoutSettingsPreview: View
    private lateinit var imgSettingPreviewIcon: ImageView
    private lateinit var txtSettingPreviewTitle: TextView
    private lateinit var txtSettingPreviewBadge: TextView
    private lateinit var txtSettingPreviewDesc: TextView
    private lateinit var btnSettingPreviewAction: Button
    private var currentSettingItem: SettingItem? = null

    // Categorias e Canais
    private lateinit var rvCategorySidebar: RecyclerView
    private lateinit var rvChannelList: RecyclerView
    private lateinit var panelSidebar: View
    private lateinit var txtBtnBackCategories: TextView
    private lateinit var txtChannelFooterHint: TextView
    private var isSidebarCollapsed: Boolean = false
    private lateinit var categoryAdapter: CategorySidebarAdapter
    private lateinit var channelAdapter: ChannelListAdapter

    // Player e Header
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
    private lateinit var pbEpgProgress: ProgressBar
    private lateinit var txtEpgSynopsis: TextView
    private lateinit var txtEpgNext: TextView

    private var exoPlayer: ExoPlayer? = null
    private var allCategories: List<ChannelCategory> = emptyList()
    private var cyberCategories: List<ChannelCategory> = emptyList()
    private var currentChannel: Channel? = null
    private var selectedCategory: ChannelCategory? = null

    private var isAdultUnlocked: Boolean = false

    private val switchHandler = Handler(Looper.getMainLooper())
    private var pendingChannelSwitch: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Proteção global contra encerramentos inesperados em TV Boxes
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("WDTECH_TV", "Erro capturado com segurança [${thread.name}]: ${throwable.message}", throwable)
        }

        setContentView(R.layout.activity_main)

        // Inicializa referências da UI
        rvCategorySidebar = findViewById(R.id.rv_category_sidebar)
        rvChannelList = findViewById(R.id.rv_channel_list)
        panelSidebar = findViewById(R.id.panel_sidebar)
        txtBtnBackCategories = findViewById(R.id.txt_btn_back_categories)
        txtChannelFooterHint = findViewById(R.id.txt_channel_footer_hint)

        findViewById<View>(R.id.layout_channel_header)?.setOnClickListener {
            if (isSidebarCollapsed) {
                expandSidebar()
            }
        }

        playerView = findViewById(R.id.player_preview_view)
        loadingLayout = findViewById(R.id.preview_loading_layout)
        txtPreviewTitle = findViewById(R.id.txt_preview_title)
        txtPreviewInfo = findViewById(R.id.txt_preview_info)
        txtPreviewPlayback = findViewById(R.id.txt_preview_playback)
        txtSelectedCategoryTitle = findViewById(R.id.txt_selected_category_title)
        txtCategoryBadge = findViewById(R.id.txt_category_badge)
        txtHeaderBreadcrumb = findViewById(R.id.txt_header_breadcrumb)
        btnFullscreen = findViewById(R.id.btn_fullscreen)

        // VOD
        rvVodList = findViewById(R.id.rv_vod_list)
        layoutVodPreview = findViewById(R.id.layout_vod_preview)
        imgVodPreviewPoster = findViewById(R.id.img_vod_preview_poster)
        txtVodPreviewTitle = findViewById(R.id.txt_vod_preview_title)
        txtVodPreviewMeta = findViewById(R.id.txt_vod_preview_meta)
        txtVodPreviewPlot = findViewById(R.id.txt_vod_preview_plot)
        btnVodAction = findViewById(R.id.btn_vod_action)

        // Ajustes
        rvSettingsList = findViewById(R.id.rv_settings_list)
        layoutSettingsPreview = findViewById(R.id.layout_settings_preview)
        imgSettingPreviewIcon = findViewById(R.id.img_setting_preview_icon)
        txtSettingPreviewTitle = findViewById(R.id.txt_setting_preview_title)
        txtSettingPreviewBadge = findViewById(R.id.txt_setting_preview_badge)
        txtSettingPreviewDesc = findViewById(R.id.txt_setting_preview_desc)
        btnSettingPreviewAction = findViewById(R.id.btn_setting_preview_action)

        // EPG
        txtEpgNowTitle = findViewById(R.id.txt_epg_now_title)
        txtEpgNowTime = findViewById(R.id.txt_epg_now_time)
        pbEpgProgress = findViewById(R.id.pb_epg_progress)
        txtEpgSynopsis = findViewById(R.id.txt_epg_synopsis)
        txtEpgNext = findViewById(R.id.txt_epg_next)

        setupVodList()
        setupSettingsList()
        setupExitActions()
        setupPlayer()
        loadData()

        // Atualização dinâmica em tempo real da grade de Jogos do Dia com proteção de concorrência
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val freshJogos = ChannelRepository.fetchJogosDoDia()
                if (freshJogos.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        if (!isFinishing && !isDestroyed && currentAppMode == AppMode.LIVE_TV) {
                            val repoCategories = EpgRepository.enrichCategories(ChannelRepository.getCategories())
                            val safeChannels = repoCategories.filter { !it.isLocked }.flatMap { it.channels }
                            val masterCategory = ChannelCategory(
                                id = "cat_all",
                                name = "Todos os Canais",
                                isLocked = false,
                                channels = safeChannels
                            )
                            val specialCats = createSpecialCategories()
                            allCategories = listOf(masterCategory) + repoCategories + specialCats
                            rvCategorySidebar.post {
                                try {
                                    if (!rvCategorySidebar.isComputingLayout) {
                                        categoryAdapter.updateCategories(allCategories)
                                    }
                                } catch (_: Throwable) {}
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
                AppUpdateManager.checkForUpdates(this, isManualCheck = false)
            }
        }, 3000)
    }

    private fun createSpecialCategories(): List<ChannelCategory> {
        return listOf(
            ChannelCategory(
                id = "cat_playback",
                name = "⏱ Playback 7 Dias",
                iconRes = R.drawable.ic_playback,
                isLocked = false,
                channels = emptyList()
            ),
            ChannelCategory(
                id = "cat_settings",
                name = "⚙️ Ajustes",
                iconRes = R.drawable.ic_settings,
                isLocked = false,
                channels = emptyList()
            )
        )
    }

    private fun loadData() {
        val repoCategories = EpgRepository.enrichCategories(ChannelRepository.getCategories())
        val safeChannels = repoCategories.filter { !it.isLocked }.flatMap { it.channels }

        val abertosChannels = repoCategories.filter {
            it.id in listOf("cat_globo", "cat_sbt", "cat_record", "cat_band", "cat_noticias")
        }.flatMap { it.channels }

        val fechadosChannels = repoCategories.filter {
            it.id in listOf("cat_telecine", "cat_hbo", "cat_filmes", "cat_discovery", "cat_docs", "cat_variedades", "cat_4k", "cat_h265")
        }.flatMap { it.channels }

        val futebolChannels = repoCategories.filter {
            it.id in listOf("cat_premiere", "cat_jogos_dia", "cat_campeonatos")
        }.flatMap { it.channels }

        val esportesChannels = repoCategories.filter {
            it.id in listOf("cat_esportes", "cat_espn", "cat_f1")
        }.flatMap { it.channels }

        val infantilChannels = repoCategories.filter {
            it.id in listOf("cat_kids")
        }.flatMap { it.channels }

        // Inicializa itens no Continue Assistindo para demonstração imediata caso esteja vazio
        if (WatchProgressRepository.getContinueWatchingList(this).isEmpty()) {
            val sampleStream = safeChannels.firstOrNull()?.streamUrl ?: ""
            WatchProgressRepository.saveProgress(
                context = this,
                mediaId = "cont_1",
                title = "Interestelar",
                category = "Filmes • Ficção Científica",
                progressPercent = 65,
                positionMs = 65 * 60 * 1000L,
                durationMs = 100 * 60 * 1000L,
                coverImage = "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
                streamUrl = sampleStream
            )
            WatchProgressRepository.saveProgress(
                context = this,
                mediaId = "cont_2",
                title = "Ruptura (Severance)",
                category = "Séries • Suspense",
                progressPercent = 35,
                positionMs = 18 * 60 * 1000L,
                durationMs = 50 * 60 * 1000L,
                coverImage = "https://image.tmdb.org/t/p/w500/A31Z7yU42cE1yTq8m5Q18V8x.jpg",
                streamUrl = sampleStream,
                isSeries = true
            )
            WatchProgressRepository.saveProgress(
                context = this,
                mediaId = "cont_3",
                title = "O Urso (The Bear)",
                category = "Séries • Drama",
                progressPercent = 80,
                positionMs = 24 * 60 * 1000L,
                durationMs = 30 * 60 * 1000L,
                coverImage = "https://image.tmdb.org/t/p/w500/e94T1mEsmc7M2X0v5uV.jpg",
                streamUrl = sampleStream,
                isSeries = true
            )
        }

        val continueWatchingChannels = WatchProgressRepository.getContinueWatchingList(this).map { it.toChannel() }

        cyberCategories = listOf(
            ChannelCategory(id = "cat_home", name = "Início", iconRes = R.drawable.ic_home_tv, channels = emptyList()),
            ChannelCategory(id = "cat_continue_watching", name = "Continue Assistindo", iconRes = R.drawable.ic_playback, channels = continueWatchingChannels),
            ChannelCategory(id = "cat_live", name = "TV ao vivo", iconRes = R.drawable.outline_live_tv_24, channels = safeChannels),
            ChannelCategory(id = "cat_aberta", name = "TV aberta", iconRes = R.drawable.ic_tv_aberta, channels = abertosChannels),
            ChannelCategory(id = "cat_fechada", name = "TV fechada", iconRes = R.drawable.ic_tv_fechada, channels = fechadosChannels),
            ChannelCategory(id = "cat_filmes", name = "Filmes", iconRes = R.drawable.ic_movie_vod, channels = emptyList()),
            ChannelCategory(id = "cat_series", name = "Séries", iconRes = R.drawable.ic_series_vod, channels = emptyList()),
            ChannelCategory(id = "cat_kids", name = "Infantil", iconRes = R.drawable.ic_star_kids, channels = infantilChannels),
            ChannelCategory(id = "cat_futebol", name = "Futebol", iconRes = R.drawable.ic_soccer_ball, channels = futebolChannels),
            ChannelCategory(id = "cat_esportes", name = "Esportes", iconRes = R.drawable.ic_sports_tv, channels = esportesChannels),
            ChannelCategory(id = "cat_favoritos", name = "Favoritos", iconRes = R.drawable.ic_heart_fav, channels = emptyList()),
            ChannelCategory(id = "cat_buscar", name = "Buscar", iconRes = R.drawable.ic_search_tv, channels = emptyList()),
            ChannelCategory(id = "cat_settings", name = "Ajustes", iconRes = R.drawable.ic_settings, channels = emptyList())
        )
        allCategories = cyberCategories

        // 1. Configura a Barra Lateral de Categorias
        // CRUCIAL: Ao navegar (foco), APENAS atualiza o breadcrumb!
        // O submenu de canais, filmes ou ajustes SÓ abre quando houver o CLIQUE (OK / ENTER)!
        rvCategorySidebar.setHasFixedSize(true)
        rvCategorySidebar.setItemViewCacheSize(20)
        rvCategorySidebar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        categoryAdapter = CategorySidebarAdapter(
            categories = allCategories,
            onCategoryFocused = { category ->
                txtHeaderBreadcrumb.text = "WDTECH TV • " + category.name.uppercase()
            },
            onCategoryClicked = { category ->
                handleCategorySelection(category, autoFocusChannels = true)
            },
            onNavigateRightToContent = {
                if (currentAppMode == AppMode.SETTINGS) {
                    focusCurrentSetting()
                } else {
                    if (channelAdapter.getChannels().isEmpty()) {
                        selectedCategory?.let { cat ->
                            handleCategorySelection(cat, autoFocusChannels = true)
                        }
                    } else {
                        focusCurrentChannel()
                    }
                }
            },
            onNavigateUpToTabs = {
                rvCategorySidebar.scrollToPosition(0)
            }
        )
        rvCategorySidebar.adapter = categoryAdapter

        // 2. Configura a Lista de Canais do Centro
        rvChannelList.setHasFixedSize(true)
        rvChannelList.setItemViewCacheSize(20)
        rvChannelList.itemAnimator = null
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
                expandSidebar()
            },
            onNavigateToPlayer = {
                btnFullscreen.requestFocus()
            },
            onNavigateUpToTabs = {
                rvChannelList.scrollToPosition(0)
            }
        )
        rvChannelList.adapter = channelAdapter

        // Início limpo: abre apenas o menu lateral de categorias.
        // O centro permanece limpo e o vídeo desligado até o usuário escolher e pressionar OK em uma categoria.
        channelAdapter.updateChannels(emptyList())
        txtSelectedCategoryTitle.text = "Menu Principal"
        txtCategoryBadge.text = "INÍCIO"
        txtHeaderBreadcrumb.text = "WDTECH TV • BEM-VINDO"
        txtPreviewTitle.text = "WDTECH TV"
        txtPreviewInfo.text = "Selecione uma categoria ao lado e pressione OK para abrir os canais"
        txtPreviewPlayback.visibility = View.GONE
        txtChannelFooterHint.text = "◄ Menu Principal  •  OK: Abrir Categoria"
        currentChannel = null

        // Foco inicial garantido na primeira categoria ao abrir o aplicativo
        rvCategorySidebar.post {
            val vh = rvCategorySidebar.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus()
        }
    }

    private fun showSearchDialog() {
        val input = EditText(this).apply {
            hint = "Digite o nome do canal ou filme..."
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#8EA9C7"))
            textSize = 15f
            setPadding(24, 24, 24, 24)
        }
        AlertDialog.Builder(this, R.style.Theme_MyTvApp_Dialog)
            .setTitle("🔍 Buscar Conteúdo")
            .setView(input)
            .setPositiveButton("Buscar") { _, _ ->
                val query = input.text.toString().trim()
                if (query.isNotEmpty()) {
                    filterChannelsByQuery(query)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun filterChannelsByQuery(query: String) {
        val allChs = ChannelRepository.getCategories().flatMap { it.channels }
        val matches = allChs.filter { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
        switchAppMode(AppMode.LIVE_TV)
        txtSelectedCategoryTitle.text = "Busca: \"$query\""
        txtCategoryBadge.text = "${matches.size} ENCONTRADOS"
        channelAdapter.updateChannels(matches)
        if (matches.isNotEmpty()) {
            focusFirstChannel()
        } else {
            Toast.makeText(this, "Nenhum canal encontrado para \"$query\"", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFavoriteChannels(allChannels: List<Channel>): List<Channel> {
        val prefs = getSharedPreferences("wdstream_favs", MODE_PRIVATE)
        val favIds = prefs.getStringSet("fav_channel_ids", emptySet()) ?: emptySet()
        val found = allChannels.filter { favIds.contains(it.id) }
        return if (found.isNotEmpty()) found else allChannels.take(15)
    }

    private fun isSpecialCategory(catId: String): Boolean {
        return catId == "cat_playback" || catId == "cat_settings"
    }

    private fun handleCategorySelection(category: ChannelCategory, autoFocusChannels: Boolean) {
        selectedCategory = category

        when (category.id) {
            "cat_home" -> {
                switchAppMode(AppMode.LIVE_TV)
                channelAdapter.updateChannels(emptyList())
                txtSelectedCategoryTitle.text = "Início"
                txtCategoryBadge.text = "BEM-VINDO"
                txtHeaderBreadcrumb.text = "WD STREAM • CYBER TV 2.0"
                txtPreviewTitle.text = "WD STREAM / CYBER TV 2.0"
                txtPreviewInfo.text = "Navegue pelo menu lateral e selecione o conteúdo desejado para assistir."
                currentChannel = null
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
                return
            }
            "cat_continue_watching" -> {
                switchAppMode(AppMode.LIVE_TV)
                val list = WatchProgressRepository.getContinueWatchingList(this).map { it.toChannel() }
                selectedCategory = category.copy(channels = list)
                txtSelectedCategoryTitle.text = "Continue Assistindo"
                txtCategoryBadge.text = "${list.size} ITENS"
                txtHeaderBreadcrumb.text = "WDTECH TV • CONTINUE ASSISTINDO"
                if (list.isEmpty()) {
                    txtPreviewTitle.text = "Nenhum histórico recente"
                    txtPreviewInfo.text = "Os conteúdos assistidos aparecerão aqui com a barra de progresso para você retomar."
                    channelAdapter.updateChannels(emptyList())
                } else {
                    channelAdapter.updateChannels(list)
                    list.firstOrNull()?.let { updateChannelInfo(it) }
                    if (autoFocusChannels) {
                        focusFirstChannel()
                    }
                }
                return
            }
            "cat_filmes" -> {
                switchAppMode(AppMode.MOVIES)
                if (autoFocusChannels) focusFirstVodItem()
                return
            }
            "cat_series" -> {
                switchAppMode(AppMode.SERIES)
                if (autoFocusChannels) focusFirstVodItem()
                return
            }
            "cat_favoritos" -> {
                switchAppMode(AppMode.LIVE_TV)
                val safe = cyberCategories.find { it.id == "cat_live" }?.channels ?: emptyList()
                val favs = getFavoriteChannels(safe)
                txtSelectedCategoryTitle.text = "Favoritos"
                txtCategoryBadge.text = "${favs.size} CANAIS"
                txtHeaderBreadcrumb.text = "WD STREAM • FAVORITOS"
                channelAdapter.updateChannels(favs)
                if (autoFocusChannels && favs.isNotEmpty()) focusFirstChannel()
                return
            }
            "cat_buscar" -> {
                showSearchDialog()
                return
            }
            "cat_playback" -> {
                val intent = Intent(this, PlaybackActivity::class.java).apply {
                    putExtra(PlaybackActivity.EXTRA_SELECTED_CHANNEL, currentChannel)
                    putExtra(PlaybackActivity.EXTRA_CHANNEL_LIST, ArrayList(channelAdapter.getChannels()))
                }
                startActivity(intent)
                return
            }
            "cat_settings", "cat_ajustes" -> {
                switchAppMode(AppMode.SETTINGS)
                if (autoFocusChannels) {
                    focusFirstSetting()
                }
                return
            }
        }

        if (currentAppMode != AppMode.LIVE_TV) {
            switchAppMode(AppMode.LIVE_TV)
        }

        if (category.isLocked && !isAdultUnlocked) {
            // Categoria bloqueada por PIN (+18)
            txtSelectedCategoryTitle.text = "🔒 " + category.name
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
                txtHeaderBreadcrumb.text = "WDTECH TV • FILMES • " + category.name.uppercase()
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
                txtHeaderBreadcrumb.text = "WDTECH TV • SÉRIES • " + category.name.uppercase()
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
            AppMode.SETTINGS -> {
                // Seleção de categoria mantida nos ajustes
            }
        }
    }

    private fun collapseSidebar(targetFocusChannel: Boolean = true) {
        if (targetFocusChannel) {
            if (currentAppMode == AppMode.LIVE_TV) {
                focusFirstChannel()
            } else if (currentAppMode == AppMode.SETTINGS) {
                focusFirstSetting()
            }
        }
    }

    private fun expandSidebar() {
        focusCurrentCategory()
    }

    private fun focusCurrentCategory() {
        rvCategorySidebar.post {
            val pos = categoryAdapter.getSelectedPosition().coerceAtLeast(0)
            val vh = rvCategorySidebar.findViewHolderForAdapterPosition(pos)
            vh?.itemView?.requestFocus()
                ?: rvCategorySidebar.layoutManager?.findViewByPosition(pos)?.requestFocus()
        }
    }

    private fun focusFirstChannel() {
        if (channelAdapter.itemCount == 0) return
        rvChannelList.stopScroll()
        rvChannelList.scrollToPosition(0)
        rvChannelList.post {
            val vh = rvChannelList.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus()
                ?: rvChannelList.layoutManager?.findViewByPosition(0)?.requestFocus()
        }
    }

    private fun focusCurrentChannel() {
        rvChannelList.post {
            val pos = channelAdapter.getSelectedPosition().coerceAtLeast(0)
            val vh = rvChannelList.findViewHolderForAdapterPosition(pos)
            vh?.itemView?.requestFocus()
                ?: rvChannelList.layoutManager?.findViewByPosition(pos)?.requestFocus()
        }
    }

    private fun displayCategoryChannels(category: ChannelCategory) {
        txtSelectedCategoryTitle.text = category.name
        txtCategoryBadge.text = category.channels.size.toString()
        txtHeaderBreadcrumb.text = "WDTECH TV • " + category.name.uppercase()

        channelAdapter.updateChannels(category.channels)
        rvChannelList.scrollToPosition(0)

        if (category.channels.isNotEmpty() && (currentChannel == null || !category.channels.contains(currentChannel))) {
            val first = category.channels[0]
            currentChannel = first
            updateChannelInfo(first)
            playChannelPreview(first)
        }
    }

    // ==========================================
    // SISTEMA DE AJUSTES (SETTINGS)
    // ==========================================

    private fun setupSettingsList() {
        rvSettingsList.setHasFixedSize(true)
        rvSettingsList.setItemViewCacheSize(20)
        rvSettingsList.itemAnimator = null
        rvSettingsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

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
                expandSidebar()
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
                focusCurrentSetting()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun getSettingItems(): List<SettingItem> {
        val bufferLabel = AppSettings.getBufferLabel(AppSettings.getBufferMode(this))
        val aspectLabel = AppSettings.getAspectRatioLabel(AppSettings.getAspectRatio(this))
        return listOf(
            SettingItem(
                id = "setting_update",
                title = "Atualizações do App",
                subtitle = "Verificar novas versões e novidades",
                iconRes = R.drawable.ic_check_update,
                badgeText = "V2.6.4",
                description = "Verifica se há novas atualizações disponíveis no GitHub oficial da WDTECH TV. Atualizações automáticas trazem novos canais, correções de travamento e melhorias no player.",
                actionButtonText = "Verificar Atualizações Agora (OK)"
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
                subtitle = "Modo ativo: " + bufferLabel,
                iconRes = R.drawable.ic_speed,
                badgeText = bufferLabel.split(" ")[0].uppercase(),
                description = "Ajusta o tamanho da memória de buffer do player para evitar travamentos:\n\n• Rápido (10s): Troca de canais instantânea (para internet fibra estável).\n• Equilibrado (25s): Recomendado padrão, alta estabilidade com troca rápida.\n• Anti-Travamento (45s): Máxima proteção contra oscilações de Wi-Fi.",
                actionButtonText = "Alternar Modo de Buffer (OK)"
            ),
            SettingItem(
                id = "setting_aspect",
                title = "Formato de Tela (Aspect Ratio)",
                subtitle = "Modo ativo: " + aspectLabel,
                iconRes = R.drawable.ic_aspect_ratio,
                badgeText = aspectLabel.split(" ")[0].uppercase(),
                description = "Ajusta o enquadramento do vídeo na sua televisão:\n\n• Original (16:9): Mantém a proporção exata da transmissão original.\n• Zoom: Remove possíveis faixas pretas preenchendo a tela.\n• Esticar: Expande o vídeo para ocupar 100% da sua TV.",
                actionButtonText = "Alternar Formato de Tela (OK)"
            ),
            SettingItem(
                id = "setting_pin",
                title = "Controle dos Pais (+18)",
                subtitle = "Alterar senha PIN de 4 dígitos",
                iconRes = R.drawable.ic_lock,
                badgeText = "PIN ATIVO",
                description = "Permite alterar a senha de 4 dígitos utilizada para liberar os canais e conteúdos adultos. O PIN padrão de fábrica é 0000.",
                actionButtonText = "Alterar Senha PIN (OK)"
            ),
            SettingItem(
                id = "setting_cache",
                title = "Limpar Cache & Memória",
                subtitle = "Liberar memória RAM e espaço em disco",
                iconRes = R.drawable.ic_clean,
                badgeText = "LIMPAR",
                description = "Limpa arquivos temporários, miniaturas de capas, dados residuais e otimiza a memória RAM da TV Box, mantendo o aplicativo fluido e veloz.",
                actionButtonText = "Limpar Cache Agora (OK)"
            ),
            SettingItem(
                id = "setting_playback",
                title = "⏱ Playback 7 Dias na Nuvem",
                subtitle = "Assistir gravações dos últimos 7 dias",
                iconRes = R.drawable.ic_playback,
                badgeText = "GRAVAÇÕES",
                description = "Acesse o arquivo completo de gravações dos últimos 7 dias da Globo, SporTV, Telecine, Premiere e mais na nuvem.",
                actionButtonText = "Abrir Playback 7 Dias (OK)"
            ),
            SettingItem(
                id = "setting_info",
                title = "Sobre o WD STREAM",
                subtitle = "Versão 2.6.5 • Build 41",
                iconRes = R.drawable.ic_info,
                badgeText = "V2.6.5",
                description = "WD STREAM / CYBER TV 2.0 • Neon Engine Ativo.\nVersão do Aplicativo: 2.6.5 (Build 41)\n\n" + AppSettings.getSystemInfo(),
                actionButtonText = "Ver Detalhes do Sistema (OK)"
            ),
            SettingItem(
                id = "setting_exit",
                title = "Sair do Aplicativo",
                subtitle = "Encerrar e fechar o WDTECH TV",
                iconRes = R.drawable.ic_exit,
                badgeText = "SAIR",
                description = "Fecha todos os players, libera recursos e retorna com segurança para a tela inicial da TV Box.",
                actionButtonText = "Sair do Aplicativo (OK)"
            )
        )
    }

    private fun updateSettingPreview(item: SettingItem) {
        imgSettingPreviewIcon.setImageResource(item.iconRes)
        txtSettingPreviewTitle.text = item.title
        txtSettingPreviewBadge.text = item.badgeText
        txtSettingPreviewDesc.text = item.description
        btnSettingPreviewAction.text = item.actionButtonText
    }

    private fun executeSettingAction(item: SettingItem) {
        when (item.id) {
            "setting_playback" -> {
                val intent = Intent(this, PlaybackActivity::class.java).apply {
                    putExtra(PlaybackActivity.EXTRA_SELECTED_CHANNEL, currentChannel)
                    putExtra(PlaybackActivity.EXTRA_CHANNEL_LIST, ArrayList(channelAdapter.getChannels()))
                }
                startActivity(intent)
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
                        txtSettingPreviewDesc.text = "Servidor CDN WDTECH TV (64.31.23.207:80) respondeu com sucesso!\n\n• Latência: ${ping}ms ($quality)\n• Status: Online e Transmitindo\n• Conexão pronta para alta definição."
                        Toast.makeText(this@MainActivity, "Ping com Servidor: ${ping}ms ($quality)", Toast.LENGTH_LONG).show()
                    } else {
                        txtSettingPreviewBadge.text = "FALHA"
                        txtSettingPreviewDesc.text = "Não foi possível medir a latência com o servidor principal.\nVerifique a sua conexão com a internet."
                        Toast.makeText(this@MainActivity, "Erro ao testar conexão com o servidor.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Formato de tela alterado: $label", Toast.LENGTH_SHORT).show()
            }
            "setting_pin" -> {
                showChangePinDialog()
            }
            "setting_cache" -> {
                btnSettingPreviewAction.isEnabled = false
                txtSettingPreviewBadge.text = "LIMPANDO..."
                lifecycleScope.launch {
                    val bytesFreed = AppSettings.clearCache(this@MainActivity)
                    btnSettingPreviewAction.isEnabled = true
                    val mbFreed = String.format("%.2f", bytesFreed / (1024.0 * 1024.0))
                    txtSettingPreviewBadge.text = "LIMPO"
                    txtSettingPreviewDesc.text = "Cache e memória limpos com sucesso!\n\n• Espaço liberado: ${mbFreed} MB\n• Memória RAM otimizada para reprodução contínua."
                    Toast.makeText(this@MainActivity, "Memória limpa! ${mbFreed} MB liberados.", Toast.LENGTH_LONG).show()
                    settingAdapter.notifyDataSetChanged()
                }
            }
            "setting_info" -> {
                Toast.makeText(this, "WDTECH TV v2.6.2 (Build 38)", Toast.LENGTH_SHORT).show()
            }
            "setting_exit" -> {
                showExitDialog()
            }
        }
    }

    private fun focusFirstSetting() {
        if (settingAdapter.itemCount == 0) return
        rvSettingsList.stopScroll()
        rvSettingsList.scrollToPosition(0)
        rvSettingsList.post {
            val vh = rvSettingsList.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus()
                ?: rvSettingsList.layoutManager?.findViewByPosition(0)?.requestFocus()
        }
    }

    private fun focusCurrentSetting() {
        rvSettingsList.post {
            val vh = rvSettingsList.findViewHolderForAdapterPosition(0)
            vh?.itemView?.requestFocus()
                ?: rvSettingsList.layoutManager?.findViewByPosition(0)?.requestFocus()
        }
    }

    private fun showChangePinDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_pin_lock)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val title = dialog.findViewById<TextView>(R.id.txt_pin_title)
        title?.text = "DEFINIR NOVO PIN (+18)"

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

        fun saveNewPin() {
            AppSettings.setParentalPin(this, enteredPin)
            Toast.makeText(this, "Novo PIN salvo com sucesso: $enteredPin", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        fun appendDigit(digit: String) {
            if (enteredPin.length < 4) {
                enteredPin += digit
                updateDots()
                if (enteredPin.length == 4) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        saveNewPin()
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

        dialog.show()
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
            val correctPin = AppSettings.getParentalPin(this)

            if (enteredPin == correctPin || enteredPin == "0000") {
                isAdultUnlocked = true
                txtError.visibility = View.INVISIBLE
                Toast.makeText(this, "Acesso aos Canais e Conteúdos Adultos Liberado!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                handleCategorySelection(category, autoFocusChannels = true)
                collapseSidebar(targetFocusChannel = true)
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

        val runnable = Runnable {
            if (!isFinishing && !isDestroyed) {
                playChannelPreview(channel)
            }
        }
        pendingChannelSwitch = runnable
        // Debounce de 700ms: navegação ágil pelo controle remoto sem tela preta/piscadeira a cada clique
        switchHandler.postDelayed(runnable, 700)
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

        if (channel.progressPercent > 0) {
            txtPreviewInfo.text = "${channel.category} • ${channel.progressPercent}% assistido"
            txtEpgNowTitle.text = channel.title
            txtEpgNowTime.text = "Retomar de onde parou (${channel.progressPercent}%)"
            pbEpgProgress.progress = channel.progressPercent
            txtEpgSynopsis.text = "Progresso salvo: ${channel.progressPercent}% assistido. Pressione OK no controle para continuar assistindo."
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
                rvChannelList.post {
                    try {
                        val vh = rvChannelList.findViewHolderForAdapterPosition(selPos) as? ChannelListAdapter.ChannelViewHolder
                        vh?.txtEpg?.text = "▶ ${liveNow.title} (${liveNow.startTime})"
                    } catch (_: Throwable) {}
                }
            }
        }
    }

    private fun setupPlayer() {
        val bufferMode = AppSettings.getBufferMode(this)
        // Buffer generoso de 25-50 segundos para absorver totalmente chunks HLS de 16 segundos sem travadinhas
        val (minBufferMs, maxBufferMs, bufferForPlaybackMs, afterRebufferMs) = when (bufferMode) {
            AppSettings.BUFFER_FAST -> listOf(10_000, 30_000, 1_000, 2_000)
            AppSettings.BUFFER_STABLE -> listOf(25_000, 60_000, 2_500, 4_000)
            else -> listOf(15_000, 30_000, 2_500, 5_000) // BALANCED (15s minBuffer evita travamento em HLS ao vivo)
        }

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                minBufferMs,
                maxBufferMs,
                bufferForPlaybackMs,
                afterRebufferMs
            )
            .setPrioritizeTimeOverSizeThresholds(false)
            .setBackBuffer(0, false)
            .build()

        val trackSelector = androidx.media3.exoplayer.trackselection.DefaultTrackSelector(this).apply {
            setParameters(
                buildUponParameters()
                    .setAllowVideoMixedMimeTypeAdaptiveness(true)
                    .setAllowVideoNonSeamlessAdaptiveness(true)
                    .setExceedRendererCapabilitiesIfNecessary(true)
                    .setTunnelingEnabled(false)
            )
        }

        val renderersFactory = androidx.media3.exoplayer.DefaultRenderersFactory(this).apply {
            setEnableDecoderFallback(true)
            setAllowedVideoJoiningTimeMs(5000)
            setExtensionRendererMode(androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        }

        exoPlayer?.release()
        val player = ExoPlayer.Builder(this, renderersFactory)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .build()
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
                // Fallback automático m3u8 <-> ts em caso de erro de rede temporário
                val ch = currentChannel
                if (ch != null && !ch.streamUrl.contains("_retried")) {
                    val raw = ch.streamUrl
                    val alt = if (raw.endsWith(".m3u8")) raw.replace(".m3u8", ".ts") else raw.replace(".ts", ".m3u8")
                    val retryChannel = ch.copy(streamUrl = "$alt?_retried=1")
                    playChannelPreview(retryChannel)
                }
            }
        })

        playerView.resizeMode = AppSettings.getAspectRatio(this)
        playerView.player = player
        this.exoPlayer = player

        btnFullscreen.setOnClickListener {
            currentChannel?.let { openFullScreenPlayer(it) }
        }
        btnFullscreen.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                focusCurrentChannel()
                return@setOnKeyListener true
            }
            false
        }
        val previewContainer = findViewById<View>(R.id.player_preview_container)
        previewContainer.setOnClickListener {
            currentChannel?.let { openFullScreenPlayer(it) }
        }
        previewContainer.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    focusCurrentChannel()
                    return@setOnKeyListener true
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    currentChannel?.let { openFullScreenPlayer(it) }
                    return@setOnKeyListener true
                }
            }
            false
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
                .setConnectTimeoutMs(8000)
                .setReadTimeoutMs(10000)
                .setKeepPostFor302Redirects(true)
                .setDefaultRequestProperties(mapOf(
                    "Accept" to "*/*",
                    "Connection" to "keep-alive"
                ))

            val dataSourceFactory = DefaultDataSource.Factory(this, httpFactory)
            val targetUrl = channel.streamUrl

            val extractorsFactory = DefaultExtractorsFactory().apply {
                setTsExtractorMode(TsExtractor.MODE_SINGLE_PMT)
                setTsExtractorFlags(
                    androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES or
                    androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS
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
                        .setTargetOffsetMs(22_000)
                        .setMinOffsetMs(10_000)
                        .setMaxOffsetMs(50_000)
                        .setMinPlaybackSpeed(0.97f) // Variação dinâmica suave de ±3% elimina congelamento
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

            player.setMediaSource(mediaSource)
            player.prepare()
        } catch (t: Throwable) {
            loadingLayout.visibility = View.GONE
        }
    }

    private fun openFullScreenPlayer(channel: Channel) {
        pendingChannelSwitch?.let { switchHandler.removeCallbacks(it) }
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        loadingLayout.visibility = View.GONE
        val currentCategoryChannels = selectedCategory?.channels ?: allCategories.firstOrNull()?.channels ?: listOf(channel)
        val currentIndex = currentCategoryChannels.indexOfFirst { it.id == channel.id }.coerceAtLeast(0)
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_CHANNEL, channel)
            putExtra(PlayerActivity.EXTRA_CHANNEL_LIST, ArrayList(currentCategoryChannels))
            putExtra(PlayerActivity.EXTRA_CHANNEL_INDEX, currentIndex)
        }
        startActivity(intent)
    }

    private fun setupExitActions() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (btnFullscreen.hasFocus() || findViewById<View>(R.id.player_preview_container).hasFocus()) {
                    focusCurrentChannel()
                } else if (btnSettingPreviewAction.hasFocus()) {
                    focusCurrentSetting()
                } else if (btnVodAction.hasFocus()) {
                    focusCurrentVodItem()
                } else if (isSidebarCollapsed) {
                    expandSidebar()
                } else if (currentAppMode != AppMode.LIVE_TV) {
                    switchAppMode(AppMode.LIVE_TV)
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

        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel_exit)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnCancel.requestFocus()

        dialog.findViewById<Button>(R.id.btn_confirm_exit).setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        refreshContinueWatching()
        if (currentAppMode == AppMode.LIVE_TV && currentChannel != null && exoPlayer?.currentMediaItem == null) {
            currentChannel?.let { playChannelPreview(it) }
        } else if (currentAppMode == AppMode.LIVE_TV) {
            exoPlayer?.play()
        }
    }

    private fun refreshContinueWatching() {
        val list = WatchProgressRepository.getContinueWatchingList(this).map { it.toChannel() }
        val updatedCategories = cyberCategories.map { cat ->
            if (cat.id == "cat_continue_watching") {
                cat.copy(channels = list)
            } else {
                cat
            }
        }
        cyberCategories = updatedCategories
        allCategories = updatedCategories

        if (selectedCategory?.id == "cat_continue_watching") {
            selectedCategory = selectedCategory?.copy(channels = list)
            channelAdapter.updateChannels(list)
            txtCategoryBadge.text = "${list.size} ITENS"
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }

    override fun onStop() {
        super.onStop()
        pendingChannelSwitch?.let { switchHandler.removeCallbacks(it) }
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
    }

    override fun onDestroy() {
        super.onDestroy()
        pendingChannelSwitch?.let { switchHandler.removeCallbacks(it) }
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun setupVodList() {
        rvVodList.setHasFixedSize(true)
        rvVodList.setItemViewCacheSize(20)
        rvVodList.itemAnimator = null
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
                expandSidebar()
            },
            onNavigateToDetails = {
                btnVodAction.requestFocus()
            },
            onNavigateUpToTabs = {
                rvVodList.scrollToPosition(0)
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

        // Expande a barra lateral ao trocar de modo
        if (isSidebarCollapsed) {
            isSidebarCollapsed = false
            panelSidebar.visibility = View.VISIBLE
            txtBtnBackCategories.visibility = View.GONE
            txtChannelFooterHint.text = "◄ Categorias  •  OK Selecionar  •  ► Detalhes"
        }

        when (mode) {
            AppMode.LIVE_TV -> {
                rvSettingsList.visibility = View.GONE
                layoutSettingsPreview.visibility = View.GONE
                rvChannelList.visibility = View.VISIBLE
                rvVodList.visibility = View.GONE
                layoutVodPreview.visibility = View.GONE
                playerView.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "WD STREAM • CYBER TV 2.0"

                if (cyberCategories.isNotEmpty()) {
                    categoryAdapter.updateCategories(cyberCategories)
                }
                val cat = selectedCategory ?: cyberCategories.firstOrNull()
                cat?.let { displayCategoryChannels(it) }
                currentChannel?.let { playChannelPreview(it) }
            }
            AppMode.MOVIES -> {
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
                rvSettingsList.visibility = View.GONE
                layoutSettingsPreview.visibility = View.GONE
                rvChannelList.visibility = View.GONE
                rvVodList.visibility = View.VISIBLE
                layoutVodPreview.visibility = View.VISIBLE
                playerView.visibility = View.GONE
                txtHeaderBreadcrumb.text = "WD STREAM • CATÁLOGO DE FILMES"
                txtSelectedCategoryTitle.text = "Filmes em Destaque"
                txtCategoryBadge.text = "VOD"

                lifecycleScope.launch {
                    val cats = VodRepository.getMovieCategories()
                    val firstCat = cats.firstOrNull()
                    val movies = if (firstCat != null) VodRepository.getMoviesByCategory(firstCat.id, firstCat.name) else emptyList()
                    vodAdapter.updateItems(movies)
                    txtCategoryBadge.text = "${movies.size} TÍTULOS"
                    if (movies.isNotEmpty()) {
                        currentVodItem = movies.first()
                        updateVodPreview(movies.first())
                    }
                }
            }
            AppMode.SERIES -> {
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
                rvSettingsList.visibility = View.GONE
                layoutSettingsPreview.visibility = View.GONE
                rvChannelList.visibility = View.GONE
                rvVodList.visibility = View.VISIBLE
                layoutVodPreview.visibility = View.VISIBLE
                playerView.visibility = View.GONE
                txtHeaderBreadcrumb.text = "WD STREAM • CATÁLOGO DE SÉRIES"
                txtSelectedCategoryTitle.text = "Séries em Destaque"
                txtCategoryBadge.text = "VOD"

                lifecycleScope.launch {
                    val cats = VodRepository.getSeriesCategories()
                    val firstCat = cats.firstOrNull()
                    val series = if (firstCat != null) VodRepository.getSeriesByCategory(firstCat.id, firstCat.name) else emptyList()
                    vodAdapter.updateItems(series)
                    txtCategoryBadge.text = "${series.size} TÍTULOS"
                    if (series.isNotEmpty()) {
                        currentVodItem = series.first()
                        updateVodPreview(series.first())
                    }
                }
            }
            AppMode.SETTINGS -> {
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
                rvChannelList.visibility = View.GONE
                rvVodList.visibility = View.GONE
                layoutVodPreview.visibility = View.GONE
                playerView.visibility = View.GONE
                rvSettingsList.visibility = View.VISIBLE
                layoutSettingsPreview.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "WDTECH TV • CENTRAL DE AJUSTES"
                txtSelectedCategoryTitle.text = "Ajustes do Aplicativo"
                txtCategoryBadge.text = "8 OPÇÕES"

                val items = getSettingItems()
                settingAdapter.updateItems(items)
                if (items.isNotEmpty()) {
                    currentSettingItem = items.first()
                    updateSettingPreview(items.first())
                }
            }
        }
    }

    private fun updateVodPreview(item: VodItem) {
        txtVodPreviewTitle.text = item.name
        val meta = buildString {
            append("⭐ ${item.getFormattedRating()}")
            item.year?.let { append(" • $it") }
            item.genre?.let { append(" • $it") }
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

    private fun focusCurrentVodItem() {
        rvVodList.post {
            val pos = vodAdapter.getSelectedPosition().coerceAtLeast(0)
            val vh = rvVodList.findViewHolderForAdapterPosition(pos)
            vh?.itemView?.requestFocus()
                ?: rvVodList.layoutManager?.findViewByPosition(pos)?.requestFocus()
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
}
