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
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mytv.app.adapter.CategorySidebarAdapter
import com.mytv.app.adapter.OttChannelCardAdapter
import com.mytv.app.adapter.OttContinueAdapter
import com.mytv.app.adapter.OttMatchAdapter
import com.mytv.app.adapter.OttMatchItem
import com.mytv.app.adapter.OttPosterAdapter
import com.mytv.app.adapter.SettingListAdapter
import com.mytv.app.data.ChannelRepository
import com.mytv.app.data.EpgRepository
import com.mytv.app.data.VodRepository
import com.mytv.app.data.WatchProgress
import com.mytv.app.data.WatchProgressRepository
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.SettingItem
import com.mytv.app.model.VodItem
import com.mytv.app.utils.AppSettings
import com.mytv.app.utils.AppUpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    // --- Referências de Layout e Navegação ---
    private lateinit var rvSidebar: RecyclerView
    private lateinit var sidebarAdapter: CategorySidebarAdapter
    private lateinit var txtHeaderBreadcrumb: TextView
    private lateinit var btnSidebarSettings: View

    // --- Containers de Viewport Dinâmicos ---
    private lateinit var viewHome: NestedScrollView
    private lateinit var viewGrid: LinearLayout
    private lateinit var viewFutebol: NestedScrollView
    private lateinit var viewPosters: LinearLayout
    private lateinit var viewSearch: LinearLayout
    private lateinit var viewSettings: LinearLayout

    // --- Componentes da View: Início (Home & Hero) ---
    private lateinit var imgHeroBackdrop: ImageView
    private lateinit var txtHeroTitle: TextView
    private lateinit var txtHeroMatch: TextView
    private lateinit var txtHeroYear: TextView
    private lateinit var txtHeroAge: TextView
    private lateinit var txtHeroDescription: TextView
    private lateinit var btnHeroWatch: Button
    private lateinit var btnHeroDetails: Button
    private lateinit var rvHomeContinue: RecyclerView
    private lateinit var continueAdapter: OttContinueAdapter
    private lateinit var rvHomeFeatured: RecyclerView
    private lateinit var featuredChannelsAdapter: OttChannelCardAdapter

    // --- Componentes da View: Grade de Canais ---
    private lateinit var viewGridIconBadge: FrameLayout
    private lateinit var imgGridCategoryIcon: ImageView
    private lateinit var txtGridCategoryTitle: TextView
    private lateinit var txtGridChannelCount: TextView
    private lateinit var rvOttChannelGrid: RecyclerView
    private lateinit var ottChannelGridAdapter: OttChannelCardAdapter

    // --- Componentes da View: Futebol & Esportes ---
    private lateinit var rvFutebolMatches: RecyclerView
    private lateinit var matchAdapter: OttMatchAdapter
    private lateinit var rvFutebolChannels: RecyclerView
    private lateinit var sportsChannelsAdapter: OttChannelCardAdapter

    // --- Componentes da View: Pôsteres (Filmes & Séries) ---
    private lateinit var imgPosterCategoryIcon: ImageView
    private lateinit var txtPosterCategoryTitle: TextView
    private lateinit var rvOttPosterGrid: RecyclerView
    private lateinit var posterAdapter: OttPosterAdapter

    // --- Componentes da View: Busca ---
    private lateinit var edtSearchQuery: EditText
    private lateinit var btnSearchSubmit: Button
    private lateinit var txtSearchResultsCount: TextView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var searchResultsAdapter: OttChannelCardAdapter

    // --- Componentes da View: Ajustes ---
    private lateinit var rvSettingsGrid: RecyclerView
    private lateinit var settingsAdapter: SettingListAdapter

    // --- Dados do Aplicativo ---
    private var allChannels: List<Channel> = emptyList()
    private var abertosChannels: List<Channel> = emptyList()
    private var telecineChannels: List<Channel> = emptyList()
    private var hboChannels: List<Channel> = emptyList()
    private var discoveryChannels: List<Channel> = emptyList()
    private var sportsChannels: List<Channel> = emptyList()
    private var favoritosChannels: List<Channel> = emptyList()
    private var featuredChannels: List<Channel> = emptyList()
    private var matchesList: List<OttMatchItem> = emptyList()
    private var moviesList: List<VodItem> = emptyList()
    private var seriesList: List<VodItem> = emptyList()
    private var currentActiveTabId: String = "cat_home"

    // Conteúdo em Destaque no Hero Section
    private var featuredHeroChannel: Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Proteção contra crashes inesperados em TV Boxes
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("WDTECH_TV", "Exceção em [${thread.name}]: ${throwable.message}", throwable)
        }

        setContentView(R.layout.activity_main)

        bindViews()
        setupBackPressed()
        initData()
        setupSidebar()
        setupHomeHero()
        setupGrids()
        setupSearch()
        setupSettings()

        // Abre na aba Início por padrão
        switchToTab("cat_home")

        // Foco inicial garantido na Sidebar
        rvSidebar.post {
            val vh = rvSidebar.findViewHolderForAdapterPosition(1) // Posição 1 = Início
            vh?.itemView?.requestFocus()
        }

        // Verificação de atualização automática
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                AppUpdateManager.checkForUpdates(this, isManualCheck = false)
            }
        }, 3000)
    }

    private fun bindViews() {
        rvSidebar = findViewById(R.id.rv_category_sidebar)
        txtHeaderBreadcrumb = findViewById(R.id.txt_header_breadcrumb)
        btnSidebarSettings = findViewById(R.id.btn_sidebar_settings)

        // Viewports
        viewHome = findViewById(R.id.layout_view_home)
        viewGrid = findViewById(R.id.layout_view_grid)
        viewFutebol = findViewById(R.id.layout_view_futebol)
        viewPosters = findViewById(R.id.layout_view_posters)
        viewSearch = findViewById(R.id.layout_view_search)
        viewSettings = findViewById(R.id.layout_view_settings)

        // Início / Hero
        imgHeroBackdrop = findViewById(R.id.img_hero_backdrop)
        txtHeroTitle = findViewById(R.id.txt_hero_title)
        txtHeroMatch = findViewById(R.id.txt_hero_match)
        txtHeroYear = findViewById(R.id.txt_hero_year)
        txtHeroAge = findViewById(R.id.txt_hero_age)
        txtHeroDescription = findViewById(R.id.txt_hero_description)
        btnHeroWatch = findViewById(R.id.btn_hero_watch)
        btnHeroDetails = findViewById(R.id.btn_hero_details)
        rvHomeContinue = findViewById(R.id.rv_home_continue)
        rvHomeFeatured = findViewById(R.id.rv_home_featured)

        // Grade de Canais
        viewGridIconBadge = findViewById(R.id.view_grid_icon_badge)
        imgGridCategoryIcon = findViewById(R.id.img_grid_category_icon)
        txtGridCategoryTitle = findViewById(R.id.txt_grid_category_title)
        txtGridChannelCount = findViewById(R.id.txt_grid_channel_count)
        rvOttChannelGrid = findViewById(R.id.rv_ott_channel_grid)

        // Futebol
        rvFutebolMatches = findViewById(R.id.rv_futebol_matches)
        rvFutebolChannels = findViewById(R.id.rv_futebol_channels)

        // Pôsteres
        imgPosterCategoryIcon = findViewById(R.id.img_poster_category_icon)
        txtPosterCategoryTitle = findViewById(R.id.txt_poster_category_title)
        rvOttPosterGrid = findViewById(R.id.rv_ott_poster_grid)

        // Busca
        edtSearchQuery = findViewById(R.id.edt_search_query)
        btnSearchSubmit = findViewById(R.id.btn_search_submit)
        txtSearchResultsCount = findViewById(R.id.txt_search_results_count)
        rvSearchResults = findViewById(R.id.rv_search_results)

        // Ajustes
        rvSettingsGrid = findViewById(R.id.rv_settings_grid)
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentActiveTabId != "cat_home") {
                    switchToTab("cat_home")
                    rvSidebar.post {
                        val vh = rvSidebar.findViewHolderForAdapterPosition(1)
                        vh?.itemView?.requestFocus()
                    }
                } else {
                    showExitConfirmation()
                }
            }
        })
    }

    private fun showExitConfirmation() {
        AlertDialog.Builder(this, R.style.Theme_MyTvApp_Dialog)
            .setTitle("Sair do WDTECH TV")
            .setMessage("Deseja realmente fechar o aplicativo?")
            .setPositiveButton("Sim, Sair") { _, _ -> finish() }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun initData() {
        val repoCategories = EpgRepository.enrichCategories(ChannelRepository.getCategories())
        allChannels = repoCategories.flatMap { it.channels }

        abertosChannels = repoCategories.filter {
            it.id in listOf("cat_globo", "cat_sbt", "cat_record", "cat_band", "cat_noticias")
        }.flatMap { it.channels }

        telecineChannels = repoCategories.filter { it.id == "cat_telecine" }.flatMap { it.channels }
        hboChannels = repoCategories.filter { it.id == "cat_hbo" }.flatMap { it.channels }
        discoveryChannels = repoCategories.filter { it.id in listOf("cat_discovery", "cat_docs") }.flatMap { it.channels }

        sportsChannels = repoCategories.filter {
            it.id in listOf("cat_premiere", "cat_esportes", "cat_espn", "cat_f1")
        }.flatMap { it.channels }

        favoritosChannels = abertosChannels.take(6) + sportsChannels.take(4) + telecineChannels.take(4)
        featuredChannels = (sportsChannels.take(3) + telecineChannels.take(3) + abertosChannels.take(3))

        // Destaque Hero: primeiro canal de filme ou esporte com streaming ativo
        featuredHeroChannel = telecineChannels.firstOrNull() ?: allChannels.firstOrNull()

        // Lista de Jogos do Dia Simulados & Reais
        matchesList = listOf(
            OttMatchItem(
                id = "m1",
                teamA = "Flamengo",
                teamB = "Palmeiras",
                time = "AO VIVO",
                champ = "Brasileirão Série A",
                channel = sportsChannels.firstOrNull { it.title.contains("Premiere", true) }
            ),
            OttMatchItem(
                id = "m2",
                teamA = "Real Madrid",
                teamB = "Manchester City",
                time = "16:00",
                champ = "Champions League",
                channel = sportsChannels.firstOrNull { it.title.contains("ESPN", true) }
            ),
            OttMatchItem(
                id = "m3",
                teamA = "São Paulo",
                teamB = "Corinthians",
                time = "18:30",
                champ = "Paulistão",
                channel = sportsChannels.firstOrNull { it.title.contains("SporTV", true) }
            ),
            OttMatchItem(
                id = "m4",
                teamA = "Arsenal",
                teamB = "Liverpool",
                time = "13:30",
                champ = "Premier League",
                channel = sportsChannels.firstOrNull { it.title.contains("ESPN", true) }
            )
        )

        // Catálogo Curado de Filmes e Séries
        val sampleStream = allChannels.firstOrNull()?.streamUrl ?: ""
        moviesList = listOf(
            VodItem(
                id = "m_oppenheimer",
                name = "Oppenheimer",
                posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?q=80&w=600&auto=format&fit=crop",
                rating = "8.9",
                categoryId = "filmes",
                year = "2023",
                genre = "Drama • Biografia",
                streamUrl = sampleStream
            ),
            VodItem(
                id = "m_john_wick_4",
                name = "John Wick 4: Baba Yaga",
                posterUrl = "https://images.unsplash.com/photo-1574169208507-84376144848b?q=80&w=600&auto=format&fit=crop",
                rating = "8.2",
                categoryId = "filmes",
                year = "2023",
                genre = "Ação • Adrenalina",
                streamUrl = sampleStream
            ),
            VodItem(
                id = "m_interstellar",
                name = "Interestelar",
                posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=600&auto=format&fit=crop",
                rating = "8.7",
                categoryId = "filmes",
                year = "2024",
                genre = "Ficção Científica",
                streamUrl = sampleStream
            ),
            VodItem(
                id = "m_batman",
                name = "The Batman",
                posterUrl = "https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?q=80&w=600&auto=format&fit=crop",
                rating = "8.0",
                categoryId = "filmes",
                year = "2022",
                genre = "Ação • Investigação",
                streamUrl = sampleStream
            )
        )

        seriesList = listOf(
            VodItem(
                id = "s_tlou",
                name = "The Last of Us",
                posterUrl = "https://images.unsplash.com/photo-1605806616949-1e87b487cb2a?q=80&w=600&auto=format&fit=crop",
                rating = "9.1",
                categoryId = "series",
                year = "2023",
                genre = "Em Alta • Sobrevivência",
                isSeries = true,
                streamUrl = sampleStream
            ),
            VodItem(
                id = "s_succession",
                name = "Succession",
                posterUrl = "https://images.unsplash.com/photo-1573164713988-8665fc963095?q=80&w=600&auto=format&fit=crop",
                rating = "8.9",
                categoryId = "series",
                year = "2023",
                genre = "Drama • Corporativo",
                isSeries = true,
                streamUrl = sampleStream
            ),
            VodItem(
                id = "s_stranger_things",
                name = "Stranger Things",
                posterUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=600&auto=format&fit=crop",
                rating = "8.7",
                categoryId = "series",
                year = "2024",
                genre = "Ficção • Mistério",
                isSeries = true,
                streamUrl = sampleStream
            ),
            VodItem(
                id = "s_severance",
                name = "Ruptura (Severance)",
                posterUrl = "https://images.unsplash.com/photo-1573165231977-3f0e27806045?q=80&w=600&auto=format&fit=crop",
                rating = "8.8",
                categoryId = "series",
                year = "2024",
                genre = "Suspense Psicológico",
                isSeries = true,
                streamUrl = sampleStream
            )
        )

        // Sincroniza Jogos do Dia remotos
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val freshJogos = ChannelRepository.fetchJogosDoDia()
                if (freshJogos.isNotEmpty()) {
                    val dynamicMatches = freshJogos.mapIndexed { index, ch ->
                        val parts = ch.title.split(Regex(" x | X | vs | VS "))
                        val tA = if (parts.size >= 2) parts[0].trim() else ch.title
                        val tB = if (parts.size >= 2) parts[1].trim() else "Ao Vivo"
                        OttMatchItem(
                            id = "remote_m_$index",
                            teamA = tA,
                            teamB = tB,
                            time = if (ch.quality.contains("VIVO", true)) "AO VIVO" else "HOJE",
                            champ = ch.category.ifEmpty { "Futebol Ao Vivo" },
                            channel = ch
                        )
                    }
                    withContext(Dispatchers.Main) {
                        matchesList = dynamicMatches
                        matchAdapter.updateMatches(matchesList)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun setupSidebar() {
        val sidebarItems = listOf(
            ChannelCategory(id = "cat_buscar", name = "Buscar", iconRes = R.drawable.ic_search_tv, isLocked = false, channels = emptyList()),
            ChannelCategory(id = "cat_home", name = "Início", iconRes = R.drawable.ic_home_tv, isLocked = false, channels = emptyList()),
            ChannelCategory(id = "cat_abertos", name = "Canais Abertos", iconRes = R.drawable.ic_tv_aberta, isLocked = false, channels = abertosChannels),
            ChannelCategory(id = "cat_filmes", name = "Filmes", iconRes = R.drawable.ic_movie_vod, isLocked = false, channels = emptyList()),
            ChannelCategory(id = "cat_series", name = "Séries", iconRes = R.drawable.ic_series_vod, isLocked = false, channels = emptyList()),
            ChannelCategory(id = "cat_futebol", name = "Futebol", iconRes = R.drawable.ic_soccer_ball, isLocked = false, channels = sportsChannels),
            ChannelCategory(id = "cat_telecine", name = "Rede Telecine", iconRes = R.drawable.ic_star_kids, isLocked = false, channels = telecineChannels),
            ChannelCategory(id = "cat_hbo", name = "Canais HBO", iconRes = R.drawable.outline_live_tv_24, isLocked = false, channels = hboChannels),
            ChannelCategory(id = "cat_discovery", name = "Universo Discovery", iconRes = R.drawable.ic_tv_fechada, isLocked = false, channels = discoveryChannels),
            ChannelCategory(id = "cat_favoritos", name = "Favoritos", iconRes = R.drawable.ic_heart_fav, isLocked = false, channels = favoritosChannels)
        )

        rvSidebar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        sidebarAdapter = CategorySidebarAdapter(
            categories = sidebarItems,
            onCategoryFocused = { cat ->
                txtHeaderBreadcrumb.text = "WDTECH TV • ${cat.name.uppercase()}"
            },
            onCategoryClicked = { cat ->
                switchToTab(cat.id)
            },
            onNavigateRightToContent = {
                focusCurrentViewport()
            }
        )
        rvSidebar.adapter = sidebarAdapter

        btnSidebarSettings.setOnClickListener {
            switchToTab("cat_settings")
        }

        btnSidebarSettings.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                switchToTab("cat_settings")
                focusCurrentViewport()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun setupHomeHero() {
        // Inicializa Hero Content
        imgHeroBackdrop.load("https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=2094&auto=format&fit=crop") {
            crossfade(true)
            placeholder(R.drawable.tv_bg)
            error(R.drawable.tv_bg)
        }

        btnHeroWatch.setOnClickListener {
            featuredHeroChannel?.let { ch ->
                openFullScreenPlayer(ch, allChannels)
            } ?: run {
                Toast.makeText(this, "Iniciando superprodução...", Toast.LENGTH_SHORT).show()
            }
        }

        btnHeroDetails.setOnClickListener {
            AlertDialog.Builder(this, R.style.Theme_MyTvApp_Dialog)
                .setTitle("Duna: Parte Dois (2024)")
                .setMessage("A jornada mítica de Paul Atreides continua enquanto ele se une a Chani e aos Fremen em uma guerra de vingança contra os conspiradores que destruíram sua família.\n\nFormato: 4K HDR • 60 FPS • Áudio 5.1 Dolby")
                .setPositiveButton("Assistir Agora") { _, _ ->
                    featuredHeroChannel?.let { ch -> openFullScreenPlayer(ch, allChannels) }
                }
                .setNegativeButton("Fechar", null)
                .show()
        }

        // Trilho: Continuar Assistindo
        rvHomeContinue.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val savedProgress = WatchProgressRepository.getContinueWatchingList(this)
        continueAdapter = OttContinueAdapter(savedProgress) { progress ->
            val ch = progress.toChannel()
            openFullScreenPlayer(ch, listOf(ch))
        }
        rvHomeContinue.adapter = continueAdapter

        // Trilho: Canais em Destaque
        rvHomeFeatured.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        featuredChannelsAdapter = OttChannelCardAdapter(
            channels = featuredChannels,
            defaultGradientRes = R.drawable.bg_channel_gradient_blue,
            onChannelClick = { ch ->
                openFullScreenPlayer(ch, featuredChannels)
            }
        )
        rvHomeFeatured.adapter = featuredChannelsAdapter
    }

    private fun setupGrids() {
        // Grade de Canais (3 Colunas)
        rvOttChannelGrid.layoutManager = GridLayoutManager(this, 3)
        ottChannelGridAdapter = OttChannelCardAdapter(
            channels = emptyList(),
            defaultGradientRes = R.drawable.bg_channel_gradient_blue,
            onChannelClick = { ch ->
                openFullScreenPlayer(ch, ottChannelGridAdapter.getChannels())
            }
        )
        rvOttChannelGrid.adapter = ottChannelGridAdapter

        // Seção Futebol: Jogos de Hoje (2 Colunas)
        rvFutebolMatches.layoutManager = GridLayoutManager(this, 2)
        matchAdapter = OttMatchAdapter(matchesList) { match ->
            match.channel?.let { ch ->
                openFullScreenPlayer(ch, sportsChannels)
            } ?: run {
                val sportCh = sportsChannels.firstOrNull()
                if (sportCh != null) {
                    openFullScreenPlayer(sportCh, sportsChannels)
                } else {
                    Toast.makeText(this, "Sinal do jogo sintonizado!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        rvFutebolMatches.adapter = matchAdapter

        // Seção Futebol: Canais Esportivos (3 Colunas)
        rvFutebolChannels.layoutManager = GridLayoutManager(this, 3)
        sportsChannelsAdapter = OttChannelCardAdapter(
            channels = sportsChannels,
            defaultGradientRes = R.drawable.bg_channel_gradient_green,
            onChannelClick = { ch ->
                openFullScreenPlayer(ch, sportsChannels)
            }
        )
        rvFutebolChannels.adapter = sportsChannelsAdapter

        // Grade de Pôsteres (4 Colunas)
        rvOttPosterGrid.layoutManager = GridLayoutManager(this, 4)
        posterAdapter = OttPosterAdapter(emptyList()) { item ->
            val ch = item.toChannel()
            openFullScreenPlayer(ch, listOf(ch))
        }
        rvOttPosterGrid.adapter = posterAdapter
    }

    private fun setupSearch() {
        rvSearchResults.layoutManager = GridLayoutManager(this, 3)
        searchResultsAdapter = OttChannelCardAdapter(
            channels = emptyList(),
            defaultGradientRes = R.drawable.bg_channel_gradient_dark,
            onChannelClick = { ch ->
                openFullScreenPlayer(ch, searchResultsAdapter.getChannels())
            }
        )
        rvSearchResults.adapter = searchResultsAdapter

        btnSearchSubmit.setOnClickListener {
            performSearch(edtSearchQuery.text.toString().trim())
        }

        edtSearchQuery.setOnEditorActionListener { _, _, _ ->
            performSearch(edtSearchQuery.text.toString().trim())
            true
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            searchResultsAdapter.updateChannels(emptyList())
            txtSearchResultsCount.text = "Digite algo para buscar..."
            return
        }

        val results = allChannels.filter {
            it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
        searchResultsAdapter.updateChannels(results)
        txtSearchResultsCount.text = "${results.size} resultados encontrados para \"$query\""

        if (results.isNotEmpty()) {
            rvSearchResults.post {
                val vh = rvSearchResults.findViewHolderForAdapterPosition(0)
                vh?.itemView?.requestFocus()
            }
        }
    }

    private fun setupSettings() {
        rvSettingsGrid.layoutManager = GridLayoutManager(this, 2)
        val settingsList = listOf(
            SettingItem(
                id = "set_aspect",
                title = "Proporção de Tela",
                subtitle = "Ajustar Zoom / 16:9 / Original",
                iconRes = R.drawable.ic_aspect_ratio,
                badgeText = "AJUSTAR",
                description = "Alterna entre Ajustar à Tela, Zoom e Esticar.",
                actionButtonText = "Alterar"
            ),
            SettingItem(
                id = "set_engine",
                title = "Motor de Reprodução",
                subtitle = "Media3 ExoPlayer Ultra HD",
                iconRes = R.drawable.outline_live_tv_24,
                badgeText = "ATIVO",
                description = "Buffer adaptativo inteligente com anti-travamento.",
                actionButtonText = "Ver Detalhes"
            ),
            SettingItem(
                id = "set_update",
                title = "Verificar Atualização",
                subtitle = "WDTECH TV v2.7.2",
                iconRes = R.drawable.ic_check_update,
                badgeText = "V2.7.2",
                description = "Consulta a versão mais recente diretamente no GitHub.",
                actionButtonText = "Verificar"
            ),
            SettingItem(
                id = "set_speed",
                title = "Teste de Conexão",
                subtitle = "Latência e Banda de Rede",
                iconRes = R.drawable.ic_wifi_tv,
                badgeText = "100%",
                description = "Mede a estabilidade da rota para os fluxos HLS.",
                actionButtonText = "Testar"
            )
        )

        settingsAdapter = SettingListAdapter(
            items = settingsList,
            onSettingFocused = {},
            onSettingClicked = { item ->
                when (item.id) {
                    "set_update" -> AppUpdateManager.checkForUpdates(this, isManualCheck = true)
                    "set_aspect" -> Toast.makeText(this, "Proporção padrão definida para 16:9 Full HD", Toast.LENGTH_SHORT).show()
                    "set_engine" -> Toast.makeText(this, "Motor ExoPlayer Media3 operando em 60 FPS", Toast.LENGTH_SHORT).show()
                    "set_speed" -> Toast.makeText(this, "Conexão excelente: 0ms jitter", Toast.LENGTH_SHORT).show()
                }
            },
            onNavigateToCategories = {
                rvSidebar.post {
                    val vh = rvSidebar.findViewHolderForAdapterPosition(1)
                    vh?.itemView?.requestFocus()
                }
            },
            onNavigateToPreview = {}
        )
        rvSettingsGrid.adapter = settingsAdapter
    }

    private fun switchToTab(tabId: String) {
        currentActiveTabId = tabId

        // Oculta todas as views de conteúdo
        viewHome.visibility = View.GONE
        viewGrid.visibility = View.GONE
        viewFutebol.visibility = View.GONE
        viewPosters.visibility = View.GONE
        viewSearch.visibility = View.GONE
        viewSettings.visibility = View.GONE

        when (tabId) {
            "cat_home" -> {
                viewHome.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "INÍCIO • DESTAQUES & CONTINUAR ASSISTINDO"
                // Atualiza histórico do Continuar Assistindo dinamicamente
                val updated = WatchProgressRepository.getContinueWatchingList(this)
                continueAdapter.updateItems(updated)
            }
            "cat_abertos" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Canais Abertos"
                txtGridChannelCount.text = "${abertosChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_tv_aberta)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(abertosChannels)
                txtHeaderBreadcrumb.text = "CANAIS ABERTOS • TRANSMISSÃO AO VIVO"
            }
            "cat_telecine" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Rede Telecine"
                txtGridChannelCount.text = "${telecineChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_star_kids)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(telecineChannels)
                txtHeaderBreadcrumb.text = "REDE TELECINE • O MELHOR DO CINEMA"
            }
            "cat_hbo" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Canais HBO"
                txtGridChannelCount.text = "${hboChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.outline_live_tv_24)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(hboChannels)
                txtHeaderBreadcrumb.text = "CANAIS HBO • SÉRIES E PRODUÇÕES EXCLUSIVAS"
            }
            "cat_discovery" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Universo Discovery"
                txtGridChannelCount.text = "${discoveryChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_tv_fechada)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(discoveryChannels)
                txtHeaderBreadcrumb.text = "UNIVERSO DISCOVERY • DOCUMENTÁRIOS & CIÊNCIA"
            }
            "cat_favoritos" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Meus Favoritos"
                txtGridChannelCount.text = "${favoritosChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_heart_fav)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(favoritosChannels)
                txtHeaderBreadcrumb.text = "FAVORITOS • ACESSO RÁPIDO"
            }
            "cat_futebol" -> {
                viewFutebol.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "FUTEBOL & CAMPEONATOS • JOGOS DE HOJE"
            }
            "cat_filmes" -> {
                viewPosters.visibility = View.VISIBLE
                txtPosterCategoryTitle.text = "Filmes • Lançamentos & Ação"
                imgPosterCategoryIcon.setImageResource(R.drawable.ic_movie_vod)
                posterAdapter.updateItems(moviesList)
                txtHeaderBreadcrumb.text = "FILMES • LANÇAMENTOS DO CINEMA"
            }
            "cat_series" -> {
                viewPosters.visibility = View.VISIBLE
                txtPosterCategoryTitle.text = "Séries • Produções em Alta"
                imgPosterCategoryIcon.setImageResource(R.drawable.ic_series_vod)
                posterAdapter.updateItems(seriesList)
                txtHeaderBreadcrumb.text = "SÉRIES • TEMPORADAS COMPLETAS"
            }
            "cat_buscar" -> {
                viewSearch.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "BUSCA • ENCONTRE CANAIS E FILMES"
            }
            "cat_settings" -> {
                viewSettings.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "CONFIGURAÇÕES • AJUSTES DO WDTECH TV"
            }
        }
    }

    private fun focusCurrentViewport() {
        when (currentActiveTabId) {
            "cat_home" -> btnHeroWatch.requestFocus()
            "cat_abertos", "cat_telecine", "cat_hbo", "cat_discovery", "cat_favoritos" -> {
                rvOttChannelGrid.post {
                    val vh = rvOttChannelGrid.findViewHolderForAdapterPosition(0)
                    vh?.itemView?.requestFocus()
                }
            }
            "cat_futebol" -> {
                rvFutebolMatches.post {
                    val vh = rvFutebolMatches.findViewHolderForAdapterPosition(0)
                    vh?.itemView?.requestFocus()
                }
            }
            "cat_filmes", "cat_series" -> {
                rvOttPosterGrid.post {
                    val vh = rvOttPosterGrid.findViewHolderForAdapterPosition(0)
                    vh?.itemView?.requestFocus()
                }
            }
            "cat_buscar" -> edtSearchQuery.requestFocus()
            "cat_settings" -> {
                rvSettingsGrid.post {
                    val vh = rvSettingsGrid.findViewHolderForAdapterPosition(0)
                    vh?.itemView?.requestFocus()
                }
            }
        }
    }

    private fun openFullScreenPlayer(channel: Channel, listContext: List<Channel>) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_CHANNEL, channel)
            putExtra(PlayerActivity.EXTRA_CHANNEL_LIST, ArrayList(listContext))
        }
        startActivity(intent)
    }
}
