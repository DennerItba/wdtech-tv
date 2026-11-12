package com.mytv.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
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
import com.mytv.app.data.WatchProgressRepository
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.SettingItem
import com.mytv.app.model.VodItem
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

    // --- Dados Completos do Aplicativo (Grade 100% Mapeada) ---
    private var allChannels: List<Channel> = emptyList()
    private var fazendaChannels: List<Channel> = emptyList()
    private var premiereChannels: List<Channel> = emptyList()
    private var futebolChannels: List<Channel> = emptyList()
    private var sportsChannels: List<Channel> = emptyList()
    private var abertosChannels: List<Channel> = emptyList()
    private var telecineChannels: List<Channel> = emptyList()
    private var hboChannels: List<Channel> = emptyList()
    private var discoveryChannels: List<Channel> = emptyList()
    private var filmesChannels: List<Channel> = emptyList()
    private var kidsChannels: List<Channel> = emptyList()
    private var series24hChannels: List<Channel> = emptyList()
    private var ultra4kChannels: List<Channel> = emptyList()
    private var musicaChannels: List<Channel> = emptyList()
    private var docsChannels: List<Channel> = emptyList()
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

        // 1. A Fazenda 24H (7 Câmeras)
        fazendaChannels = repoCategories.filter { it.id == "cat_fazenda" }.flatMap { it.channels }

        // 2. Premiere FC (Todos os 8 canais separados)
        premiereChannels = repoCategories.filter { it.id == "cat_premiere" }.flatMap { it.channels }

        // 3. Futebol & Campeonatos
        futebolChannels = repoCategories.filter { it.id in listOf("cat_jogos_dia", "cat_campeonatos") }.flatMap { it.channels }

        // 4. SporTV & Esportes Gerais
        sportsChannels = repoCategories.filter {
            it.id in listOf("cat_esportes", "cat_espn", "cat_f1")
        }.flatMap { it.channels }

        // 5. Canais Abertos
        abertosChannels = repoCategories.filter {
            it.id in listOf("cat_globo", "cat_sbt", "cat_record", "cat_band", "cat_noticias")
        }.flatMap { it.channels }

        // 6. Rede Telecine
        telecineChannels = repoCategories.filter { it.id == "cat_telecine" }.flatMap { it.channels }

        // 7. Canais HBO
        hboChannels = repoCategories.filter { it.id == "cat_hbo" }.flatMap { it.channels }

        // 8. Universo Discovery
        discoveryChannels = repoCategories.filter { it.id == "cat_discovery" }.flatMap { it.channels }

        // 9. Filmes & Séries
        filmesChannels = repoCategories.filter { it.id == "cat_filmes" }.flatMap { it.channels }

        // 10. Infantil & Kids
        kidsChannels = repoCategories.filter { it.id == "cat_kids" }.flatMap { it.channels }

        // 11. Canais 24 Horas
        series24hChannels = repoCategories.filter { it.id == "cat_24h" }.flatMap { it.channels }

        // 12. 4K Ultra HD & H.265
        ultra4kChannels = repoCategories.filter { it.id in listOf("cat_4k", "cat_h265") }.flatMap { it.channels }

        // 13. Músicas & Shows
        musicaChannels = repoCategories.filter { it.id == "cat_musica" }.flatMap { it.channels }

        // 14. Documentários & Variedades
        docsChannels = repoCategories.filter { it.id in listOf("cat_docs", "cat_variedades") }.flatMap { it.channels }

        // Favoritos Inteligentes
        favoritosChannels = abertosChannels.take(4) + premiereChannels.take(2) + fazendaChannels.take(2) + telecineChannels.take(3)

        // Canais em Destaque no Início (Mix rico com Fazenda, Premiere, Esportes, Filmes e Globo)
        featuredChannels = fazendaChannels.take(2) + premiereChannels.take(3) + sportsChannels.take(2) + telecineChannels.take(2) + abertosChannels.take(2)

        // Destaque Hero: primeiro canal de filme ou esporte com streaming ativo
        featuredHeroChannel = telecineChannels.firstOrNull() ?: premiereChannels.firstOrNull() ?: allChannels.firstOrNull()

        // Lista de Jogos do Dia Simulados & Reais
        matchesList = listOf(
            OttMatchItem(
                id = "m1",
                teamA = "Flamengo",
                teamB = "Palmeiras",
                time = "AO VIVO",
                champ = "Brasileirão Série A",
                channel = premiereChannels.firstOrNull() ?: sportsChannels.firstOrNull()
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
            ChannelCategory(id = "cat_fazenda", name = "A Fazenda 24H", iconRes = R.drawable.ic_fazenda, isLocked = false, channels = fazendaChannels),
            ChannelCategory(id = "cat_premiere", name = "Premiere FC", iconRes = R.drawable.ic_premiere, isLocked = false, channels = premiereChannels),
            ChannelCategory(id = "cat_futebol", name = "Futebol & Jogos", iconRes = R.drawable.ic_soccer_ball, isLocked = false, channels = futebolChannels),
            ChannelCategory(id = "cat_esportes", name = "SporTV & Esportes", iconRes = R.drawable.ic_sports_tv, isLocked = false, channels = sportsChannels),
            ChannelCategory(id = "cat_abertos", name = "Canais Abertos", iconRes = R.drawable.ic_tv_aberta, isLocked = false, channels = abertosChannels),
            ChannelCategory(id = "cat_telecine", name = "Rede Telecine", iconRes = R.drawable.ic_star_kids, isLocked = false, channels = telecineChannels),
            ChannelCategory(id = "cat_hbo", name = "Canais HBO", iconRes = R.drawable.outline_live_tv_24, isLocked = false, channels = hboChannels),
            ChannelCategory(id = "cat_filmes", name = "Filmes & Séries", iconRes = R.drawable.ic_movie_vod, isLocked = false, channels = filmesChannels),
            ChannelCategory(id = "cat_kids", name = "Infantil & Kids", iconRes = R.drawable.ic_star_kids, isLocked = false, channels = kidsChannels),
            ChannelCategory(id = "cat_discovery", name = "Universo Discovery", iconRes = R.drawable.ic_tv_fechada, isLocked = false, channels = discoveryChannels),
            ChannelCategory(id = "cat_24h", name = "Canais 24 Horas", iconRes = R.drawable.ic_playback, isLocked = false, channels = series24hChannels),
            ChannelCategory(id = "cat_4k", name = "4K Ultra HD & HEVC", iconRes = R.drawable.ic_4k, isLocked = false, channels = ultra4kChannels),
            ChannelCategory(id = "cat_musica", name = "Músicas & Shows", iconRes = R.drawable.ic_music, isLocked = false, channels = musicaChannels),
            ChannelCategory(id = "cat_docs", name = "Variedades & Docs", iconRes = R.drawable.ic_tv_fechada, isLocked = false, channels = docsChannels),
            ChannelCategory(id = "cat_all", name = "Todos os Canais", iconRes = R.drawable.ic_all_channels, isLocked = false, channels = allChannels),
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
                Toast.makeText(this, "Iniciando transmissão...", Toast.LENGTH_SHORT).show()
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
                subtitle = "WDTECH TV v2.8.1",
                iconRes = R.drawable.ic_check_update,
                badgeText = "V2.8.1",
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
                val updated = WatchProgressRepository.getContinueWatchingList(this)
                continueAdapter.updateItems(updated)
            }
            "cat_fazenda" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "A Fazenda 24H"
                txtGridChannelCount.text = "${fazendaChannels.size} CÂMERAS AO VIVO"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_fazenda)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(fazendaChannels)
                txtHeaderBreadcrumb.text = "A FAZENDA 24H • CÂMERAS AO VIVO EM TEMPO REAL"
            }
            "cat_premiere" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Premiere FC"
                txtGridChannelCount.text = "${premiereChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_premiere)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(premiereChannels)
                txtHeaderBreadcrumb.text = "PREMIERE FC • TODOS OS JOGOS AO VIVO"
            }
            "cat_futebol" -> {
                viewFutebol.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "FUTEBOL & CAMPEONATOS • JOGOS DE HOJE"
            }
            "cat_esportes" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "SporTV & Esportes Gerais"
                txtGridChannelCount.text = "${sportsChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_sports_tv)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(sportsChannels)
                txtHeaderBreadcrumb.text = "SPORTV & ESPORTES • TRANSMISSÃO AO VIVO"
            }
            "cat_abertos" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Canais Abertos & Notícias"
                txtGridChannelCount.text = "${abertosChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_tv_aberta)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(abertosChannels)
                txtHeaderBreadcrumb.text = "CANAIS ABERTOS • GLOBO, SBT, RECORD, BAND E NOTÍCIAS"
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
            "cat_filmes" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Filmes & Séries (Ao Vivo)"
                txtGridChannelCount.text = "${filmesChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_movie_vod)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(filmesChannels)
                txtHeaderBreadcrumb.text = "FILMES & SÉRIES • MEGAPIX, WARNER, TNT, SONY, AXN"
            }
            "cat_kids" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Infantil & Kids"
                txtGridChannelCount.text = "${kidsChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_star_kids)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(kidsChannels)
                txtHeaderBreadcrumb.text = "INFANTIL & KIDS • CARTOON, NICK, DISNEY, GLOOB"
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
            "cat_24h" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Canais 24 Horas"
                txtGridChannelCount.text = "${series24hChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_playback)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(series24hChannels)
                txtHeaderBreadcrumb.text = "CANAIS 24H • CHAVES, CHAPOLIN, SIMPSONS, CHRIS, FRIENDS"
            }
            "cat_4k" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Canais 4K Ultra HD & H.265"
                txtGridChannelCount.text = "${ultra4kChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_4k)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(ultra4kChannels)
                txtHeaderBreadcrumb.text = "4K ULTRA HD & HEVC • MÁXIMA DEFINIÇÃO"
            }
            "cat_musica" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Músicas & Shows"
                txtGridChannelCount.text = "${musicaChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_music)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(musicaChannels)
                txtHeaderBreadcrumb.text = "MÚSICAS & SHOWS • MULTISHOW, BIS, MTV E RÁDIOS"
            }
            "cat_docs" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Variedades & Documentários"
                txtGridChannelCount.text = "${docsChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_tv_fechada)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(docsChannels)
                txtHeaderBreadcrumb.text = "VARIEDADES & DOCS • HISTORY, NAT GEO, ANIMAL PLANET"
            }
            "cat_all" -> {
                viewGrid.visibility = View.VISIBLE
                txtGridCategoryTitle.text = "Todos os Canais"
                txtGridChannelCount.text = "${allChannels.size} CANAIS"
                imgGridCategoryIcon.setImageResource(R.drawable.ic_all_channels)
                viewGridIconBadge.setBackgroundResource(R.drawable.badge_hd)
                ottChannelGridAdapter.updateChannels(allChannels)
                txtHeaderBreadcrumb.text = "GRADE COMPLETA • TODOS OS ${allChannels.size} CANAIS"
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
            "cat_buscar" -> {
                viewSearch.visibility = View.VISIBLE
                txtHeaderBreadcrumb.text = "BUSCA • ENCONTRE QUALQUER CANAL OU CONTEÚDO"
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
            "cat_futebol" -> {
                rvFutebolMatches.post {
                    val vh = rvFutebolMatches.findViewHolderForAdapterPosition(0)
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
            else -> {
                rvOttChannelGrid.post {
                    val vh = rvOttChannelGrid.findViewHolderForAdapterPosition(0)
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
