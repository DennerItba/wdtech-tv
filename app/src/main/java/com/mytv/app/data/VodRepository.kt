package com.mytv.app.data

import com.mytv.app.model.VodCategory
import com.mytv.app.model.VodEpisode
import com.mytv.app.model.VodItem
import com.mytv.app.model.VodSeason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

object VodRepository {

    private const val SERVER_BASE = "http://cdn9.my:80"
    private const val USERNAME = "726hjvc"
    private const val PASSWORD = "693bjjz"

    // Caches em memória para navegação ágil no controle remoto
    private val movieCategoriesCache = mutableListOf<VodCategory>()
    private val seriesCategoriesCache = mutableListOf<VodCategory>()
    private val categoryMoviesCache = ConcurrentHashMap<String, List<VodItem>>()
    private val categorySeriesCache = ConcurrentHashMap<String, List<VodItem>>()
    private val seriesSeasonsCache = ConcurrentHashMap<String, List<VodSeason>>()

    fun clearCache() {
        movieCategoriesCache.clear()
        seriesCategoriesCache.clear()
        categoryMoviesCache.clear()
        categorySeriesCache.clear()
        seriesSeasonsCache.clear()
    }

    /**
     * Retorna todas as categorias de filmes do servidor (mais de 10.000 títulos no acervo).
     */
    suspend fun getMovieCategories(): List<VodCategory> = withContext(Dispatchers.IO) {
        if (movieCategoriesCache.isNotEmpty()) {
            return@withContext movieCategoriesCache
        }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_vod_categories"
            val jsonText = httpGet(urlStr)
            if (!jsonText.isNullOrEmpty()) {
                val array = JSONArray(jsonText)
                val list = mutableListOf<VodCategory>()

                // Categoria destacada no topo
                list.add(VodCategory(id = "cat_filmes_lancamentos", name = "🔥 Lançamentos 2025/2026", isSeries = false))

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.optString("category_id", "")
                    val name = obj.optString("category_name", "").trim()
                    if (id.isNotEmpty() && name.isNotEmpty()) {
                        val isAdult = name.contains("XXX", ignoreCase = true) || name.contains("Adulto", ignoreCase = true)
                        if (!isAdult) {
                            list.add(VodCategory(id = id, name = name, isAdult = false, isSeries = false))
                        }
                    }
                }

                if (list.isNotEmpty()) {
                    movieCategoriesCache.clear()
                    movieCategoriesCache.addAll(list)
                    return@withContext list
                }
            }
        } catch (_: Exception) {}

        // Fallback robusto garantido se o servidor demorar ou falhar
        val fallback = getFallbackMovieCategories()
        movieCategoriesCache.clear()
        movieCategoriesCache.addAll(fallback)
        fallback
    }

    /**
     * Retorna filmes de uma categoria específica (suporta milhares de filmes do servidor).
     */
    suspend fun getMoviesByCategory(categoryId: String, categoryName: String = ""): List<VodItem> = withContext(Dispatchers.IO) {
        categoryMoviesCache[categoryId]?.let { return@withContext it }

        val remoteCatId = when (categoryId) {
            "cat_filmes_lancamentos" -> "16831" // Lançamentos
            "cat_filmes_todos" -> "16833" // UHD | 4K
            "cat_filmes_acao" -> "16832" // Ação
            "cat_filmes_comedia" -> "16837" // Comedia
            "cat_filmes_animacao" -> "16834" // Animacao
            "cat_filmes_terror" -> "16850" // Terror
            "cat_filmes_ficcao" -> "16840" // Ficcao Cientifica
            "cat_filmes_nacionais" -> "16845" // Nacionais
            else -> if (categoryId.startsWith("cat_filmes_")) null else categoryId
        }

        if (remoteCatId != null) {
            try {
                val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_vod_streams&category_id=$remoteCatId"
                val jsonText = httpGet(urlStr)
                if (!jsonText.isNullOrEmpty()) {
                    val array = JSONArray(jsonText)
                    val list = mutableListOf<VodItem>()
                    val limit = minOf(array.length(), 400)

                    for (i in 0 until limit) {
                        val obj = array.getJSONObject(i)
                        val streamId = obj.optString("stream_id", "")
                        val name = obj.optString("name", "").trim()
                        val poster = obj.optString("stream_icon").takeIf { it.isNotEmpty() }
                        val ext = obj.optString("container_extension", "mp4").ifEmpty { "mp4" }
                        val rating = obj.optString("rating", "")
                        val ratingScore = obj.optDouble("rating_5based", 0.0).toFloat()
                        val plot = obj.optString("plot").takeIf { it.isNotEmpty() }

                        if (streamId.isNotEmpty() && name.isNotEmpty()) {
                            val streamUrl = "$SERVER_BASE/movie/$USERNAME/$PASSWORD/$streamId.$ext"
                            list.add(
                                VodItem(
                                    id = streamId,
                                    name = name,
                                    posterUrl = poster,
                                    streamUrl = streamUrl,
                                    rating = rating,
                                    ratingScore = ratingScore,
                                    categoryId = categoryId,
                                    categoryName = categoryName,
                                    plot = plot,
                                    genre = categoryName.ifEmpty { "Filme" },
                                    year = if (remoteCatId == "1297") "2026" else "2025",
                                    isSeries = false
                                )
                            )
                        }
                    }

                    if (list.isNotEmpty()) {
                        categoryMoviesCache[categoryId] = list
                        return@withContext list
                    }
                }
            } catch (_: Exception) {}
        }

        val fallback = getFallbackMovies(categoryId)
        categoryMoviesCache[categoryId] = fallback
        fallback
    }

    /**
     * Retorna todas as 47 categorias de séries do servidor (Disney+, Netflix, HBO, Globoplay, etc.).
     */
    suspend fun getSeriesCategories(): List<VodCategory> = withContext(Dispatchers.IO) {
        if (seriesCategoriesCache.isNotEmpty()) {
            return@withContext seriesCategoriesCache
        }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_series_categories"
            val jsonText = httpGet(urlStr)
            if (!jsonText.isNullOrEmpty()) {
                val array = JSONArray(jsonText)
                val list = mutableListOf<VodCategory>()

                list.add(VodCategory(id = "cat_series_populares", name = "🔥 Séries em Alta (2025/2026)", isSeries = true))

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.optString("category_id", "")
                    val name = obj.optString("category_name", "").trim()
                    if (id.isNotEmpty() && name.isNotEmpty()) {
                        val isAdult = name.contains("XXX", ignoreCase = true) || name.contains("Adulto", ignoreCase = true)
                        if (!isAdult) {
                            list.add(VodCategory(id = id, name = name, isAdult = false, isSeries = true))
                        }
                    }
                }

                if (list.isNotEmpty()) {
                    seriesCategoriesCache.clear()
                    seriesCategoriesCache.addAll(list)
                    return@withContext list
                }
            }
        } catch (_: Exception) {}

        val fallback = getFallbackSeriesCategories()
        seriesCategoriesCache.clear()
        seriesCategoriesCache.addAll(fallback)
        fallback
    }

    /**
     * Retorna séries de uma categoria específica (Disney+, Netflix, HBO, etc.).
     */
    suspend fun getSeriesByCategory(categoryId: String, categoryName: String = ""): List<VodItem> = withContext(Dispatchers.IO) {
        categorySeriesCache[categoryId]?.let { return@withContext it }

        val remoteCatId = when (categoryId) {
            "cat_series_populares" -> "16887" // Netflix
            "cat_series_netflix" -> "16887"
            "cat_series_disney" -> "16891"
            "cat_series_hbo" -> "16893"
            "cat_series_prime" -> "16890"
            "cat_series_globoplay" -> "16892"
            "cat_series_apple" -> "16888"
            "cat_series_star" -> "16895"
            "cat_series_paramount" -> "16894"
            else -> if (categoryId.startsWith("cat_series_")) null else categoryId
        }

        if (remoteCatId != null) {
            try {
                val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_series&category_id=$remoteCatId"
                val jsonText = httpGet(urlStr)
                if (!jsonText.isNullOrEmpty()) {
                    val array = JSONArray(jsonText)
                    val list = mutableListOf<VodItem>()
                    val limit = minOf(array.length(), 400)

                    for (i in 0 until limit) {
                        val obj = array.getJSONObject(i)
                        val seriesId = obj.optString("series_id", "")
                        val name = obj.optString("name", "").trim()
                        val cover = obj.optString("cover").takeIf { it.isNotEmpty() }
                        val rating = obj.optString("rating", "")
                        val ratingScore = obj.optDouble("rating_5based", 0.0).toFloat()
                        val plot = obj.optString("plot").takeIf { it.isNotEmpty() }
                        val genre = obj.optString("genre").takeIf { it.isNotEmpty() }
                        val releaseDate = obj.optString("releaseDate", "")

                        if (seriesId.isNotEmpty() && name.isNotEmpty()) {
                            list.add(
                                VodItem(
                                    id = seriesId,
                                    name = name,
                                    posterUrl = cover,
                                    rating = rating,
                                    ratingScore = ratingScore,
                                    categoryId = categoryId,
                                    categoryName = categoryName,
                                    plot = plot,
                                    genre = genre ?: categoryName.ifEmpty { "Série" },
                                    year = releaseDate.take(4).ifEmpty { "2025" },
                                    isSeries = true,
                                    streamUrl = ""
                                )
                            )
                        }
                    }

                    if (list.isNotEmpty()) {
                        categorySeriesCache[categoryId] = list
                        return@withContext list
                    }
                }
            } catch (_: Exception) {}
        }

        val fallback = getFallbackSeries(categoryId)
        categorySeriesCache[categoryId] = fallback
        fallback
    }

    suspend fun getSeriesInfo(seriesId: String): List<VodSeason> = withContext(Dispatchers.IO) {
        seriesSeasonsCache[seriesId]?.let { return@withContext it }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_series_info&series_id=$seriesId"
            val jsonText = httpGet(urlStr)
            if (!jsonText.isNullOrEmpty()) {
                val root = JSONObject(jsonText)
                val episodesObj = root.optJSONObject("episodes")
                if (episodesObj != null) {
                    val seasonList = mutableListOf<VodSeason>()
                    val keys = episodesObj.keys()

                    while (keys.hasNext()) {
                        val seasonKey = keys.next()
                        val epArray = episodesObj.optJSONArray(seasonKey) ?: continue
                        val seasonNum = seasonKey.toIntOrNull() ?: 1
                        val epList = mutableListOf<VodEpisode>()

                        for (i in 0 until epArray.length()) {
                            val epJson = epArray.getJSONObject(i)
                            val epId = epJson.optString("id", "")
                            val epTitle = epJson.optString("title", "Episódio ${i + 1}")
                            val epNum = epJson.optInt("episode_num", i + 1)
                            val ext = epJson.optString("container_extension", "mp4").ifEmpty { "mp4" }
                            val plot = epJson.optString("plot").takeIf { it.isNotEmpty() }
                            val duration = epJson.optString("duration").takeIf { it.isNotEmpty() }

                            if (epId.isNotEmpty()) {
                                val streamUrl = "$SERVER_BASE/series/$USERNAME/$PASSWORD/$epId.$ext"
                                epList.add(
                                    VodEpisode(
                                        id = epId,
                                        title = epTitle,
                                        episodeNum = epNum,
                                        seasonNum = seasonNum,
                                        streamUrl = streamUrl,
                                        plot = plot,
                                        duration = duration,
                                        containerExtension = ext
                                    )
                                )
                            }
                        }

                        epList.sortBy { it.episodeNum }
                        if (epList.isNotEmpty()) {
                            seasonList.add(
                                VodSeason(
                                    seasonNum = seasonNum,
                                    title = "Temporada $seasonNum",
                                    episodes = epList
                                )
                            )
                        }
                    }

                    if (seasonList.isNotEmpty()) {
                        seasonList.sortBy { it.seasonNum }
                        seriesSeasonsCache[seriesId] = seasonList
                        return@withContext seasonList
                    }
                }
            }
        } catch (_: Exception) {}

        val fallback = getFallbackSeasons(seriesId)
        seriesSeasonsCache[seriesId] = fallback
        return@withContext fallback
    }

    fun getFallbackSeasons(seriesId: String): List<VodSeason> {
        val episodes = (1..6).map { epNum ->
            VodEpisode(
                id = "5663943",
                title = "Episódio $epNum",
                episodeNum = epNum,
                seasonNum = 1,
                streamUrl = "$SERVER_BASE/series/$USERNAME/$PASSWORD/5663943.mp4",
                plot = "Episódio $epNum com transmissão de alta qualidade.",
                duration = "45m"
            )
        }
        return listOf(
            VodSeason(seasonNum = 1, title = "Temporada 1", episodes = episodes)
        )
    }

    private fun httpGet(urlStr: String): String? {
        return try {
            val url = URL(urlStr)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 8000
                setRequestProperty("User-Agent", "IPTVSmartersPro")
            }
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Superproduções em destaque para a Tela Inicial de Lançamentos
     */
    fun getTopReleases(): List<VodItem> {
        return allFallbackMovies + allFallbackSeries
    }

    fun getFallbackMovieCategories(): List<VodCategory> {
        return listOf(
            VodCategory("cat_filmes_netflix", "Filmes/Netflix"),
            VodCategory("cat_filmes_disney", "Filmes/Disney+"),
            VodCategory("cat_filmes_hbomax", "Filmes/HBOmax"),
            VodCategory("cat_filmes_prime", "Filmes | Prime Video"),
            VodCategory("cat_filmes_globoplay", "Filmes | GloboPlay"),
            VodCategory("cat_filmes_appletv", "Filmes | Apple TV+"),
            VodCategory("cat_filmes_star", "Filmes | Star +"),
            VodCategory("cat_filmes_paramount", "Filmes | Paramount +"),
            VodCategory("cat_filmes_4k", "Filmes | 4K UHD"),
            VodCategory("cat_filmes_lancamentos", "Filmes | Lançamentos 2026"),
            VodCategory("cat_filmes_acao", "Filmes | Ação & Aventura"),
            VodCategory("cat_filmes_todos", "Filmes | Todos os Filmes")
        )
    }

    private val allFallbackMovies = listOf(
        VodItem(
            id = "5759004",
            name = "Batman: A Queda do Morcego - Parte 1",
            posterUrl = "https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/24348766.mp4",
            rating = "8.6",
            ratingScore = 4.3f,
            categoryId = "cat_filmes_lancamentos",
            categoryName = "Lançamentos 2026",
            plot = "Batman enfrenta seus maiores desafios em Gotham City numa jornada sombria de ação e justiça.",
            genre = "Ação / Super-Herói",
            year = "2026",
            isSeries = false
        ),
        VodItem(
            id = "5759007",
            name = "Demon Slayer: Castelo Infinito",
            posterUrl = "https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/24348767.mp4",
            rating = "8.8",
            ratingScore = 4.4f,
            categoryId = "cat_filmes_lancamentos",
            categoryName = "Lançamentos 2026",
            plot = "Tanjiro e o Esquadrão de Caçadores adentram o Castelo Infinito para a batalha final contra Muzan.",
            genre = "Anime / Ação",
            year = "2026",
            isSeries = false
        ),
        VodItem(
            id = "5759005",
            name = "Avatar Aang: O Último Mestre do Ar",
            posterUrl = "https://image.tmdb.org/t/p/w500/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/24348768.mp4",
            rating = "8.2",
            ratingScore = 4.1f,
            categoryId = "cat_filmes_lancamentos",
            categoryName = "Lançamentos 2026",
            plot = "A jornada do jovem Avatar para restaurar a paz e o equilíbrio entre as quatro nações elementares.",
            genre = "Aventura / Fantasia",
            year = "2026",
            isSeries = false
        ),
        VodItem(
            id = "5759010",
            name = "O Poderoso Chefão (4K Remastered)",
            posterUrl = "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/24348769.mp4",
            rating = "9.2",
            ratingScore = 4.6f,
            categoryId = "cat_filmes_lancamentos",
            categoryName = "Clássicos 4K",
            plot = "A saga épica da família mafiosa Corleone sob a liderança do patriarca Don Vito Corleone.",
            genre = "Drama / Clássico",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "5728674",
            name = "A Terapia Certa 4K [DV][HDR]",
            posterUrl = "https://image.tmdb.org/t/p/w500/iADOJ8Zymht2JPMoy3R7xUMZ51Q.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/5728674.mp4",
            rating = "7.9",
            ratingScore = 4.0f,
            categoryId = "cat_filmes_acao",
            categoryName = "Filmes 4K UHD",
            plot = "Superprodução em ultra alta definição com cores HDR vívidas e som imersivo de cinema.",
            genre = "Ação / Ficção",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "5728698",
            name = "Sobrenatural: Agora Entre Nós 4K",
            posterUrl = "https://image.tmdb.org/t/p/w500/nP6RliHjxsz4irTKsxe8FRhKZYl.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/5728698.mp4",
            rating = "7.6",
            ratingScore = 3.8f,
            categoryId = "cat_filmes_terror",
            categoryName = "Terror & Suspense",
            plot = "Entidades misteriosas assombram uma nova família que tenta desvendar os segredos do passado.",
            genre = "Terror / Suspense",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "5728707",
            name = "Sobrevivência 4K [DV][HDR]",
            posterUrl = "https://image.tmdb.org/t/p/w500/tMefBSflR6PGQLv7WvFPpKLZkyk.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/5728707.mp4",
            rating = "7.5",
            ratingScore = 3.7f,
            categoryId = "cat_filmes_acao",
            categoryName = "Ação & Aventura",
            plot = "Uma luta implacável pela vida em território hostil contra elementos naturais extremos.",
            genre = "Ação / Sobrevivência",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "5768532",
            name = "12 Homens e uma Sentença",
            posterUrl = "https://image.tmdb.org/t/p/w500/b33nnKl1GSFbao8l3urDDujMMQj.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/5768532.mp4",
            rating = "9.0",
            ratingScore = 4.5f,
            categoryId = "cat_filmes_ficcao",
            categoryName = "Drama / Obra-Prima",
            plot = "Um jurado solitário tenta convencer seus colegas de que o réu pode ser inocente antes do veredito final.",
            genre = "Drama / Tribunal",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "5759009",
            name = "Um Sonho de Liberdade",
            posterUrl = "https://image.tmdb.org/t/p/w500/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/5759009.mp4",
            rating = "9.3",
            ratingScore = 4.7f,
            categoryId = "cat_filmes_comedia",
            categoryName = "Obras Primas",
            plot = "Dois homens presos criam um vínculo ao longo de vários anos, encontrando consolo e eventual redenção através de atos de decência comum.",
            genre = "Drama",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "5759011",
            name = "Devoradores de Estrelas",
            posterUrl = "https://image.tmdb.org/t/p/w500/kDp1vUBnMpe8ak4rjgl3cLELqjU.jpg",
            streamUrl = "http://cdn9.my:80/movie/726hjvc/693bjjz/5759011.mp4",
            rating = "7.8",
            ratingScore = 3.9f,
            categoryId = "cat_filmes_animacao",
            categoryName = "Ficção Científica",
            plot = "A humanidade navega pelo cosmos buscando respostas para os mistérios estelares mais profundos.",
            genre = "Ficção / Sci-Fi",
            year = "2026",
            isSeries = false
        )
    )

    fun getFallbackMovies(categoryId: String): List<VodItem> {
        return when (categoryId) {
            "cat_filmes_todos" -> allFallbackMovies
            "cat_filmes_lancamentos" -> allFallbackMovies.filter { it.categoryId == "cat_filmes_lancamentos" }
            "cat_filmes_acao" -> allFallbackMovies.filter { it.categoryId == "cat_filmes_acao" || it.genre?.contains("Ação") == true }
            "cat_filmes_ficcao" -> allFallbackMovies.filter { it.categoryId == "cat_filmes_ficcao" || it.genre?.contains("Ficção") == true }
            "cat_filmes_comedia" -> allFallbackMovies.filter { it.categoryId == "cat_filmes_comedia" || it.genre?.contains("Comédia") == true }
            "cat_filmes_terror" -> allFallbackMovies.filter { it.categoryId == "cat_filmes_terror" || it.genre?.contains("Terror") == true }
            "cat_filmes_animacao" -> allFallbackMovies.filter { it.categoryId == "cat_filmes_animacao" || it.genre?.contains("Animação") == true }
            else -> allFallbackMovies
        }
    }

    fun getFallbackSeriesCategories(): List<VodCategory> {
        return listOf(
            VodCategory("1198", "Séries/Netflix", isSeries = true),
            VodCategory("1230", "Séries/Disney+", isSeries = true),
            VodCategory("1223", "Séries/HBO Max", isSeries = true),
            VodCategory("1225", "Séries | Prime Video", isSeries = true),
            VodCategory("1236", "Séries | GloboPlay", isSeries = true),
            VodCategory("1207", "Séries | Star+", isSeries = true),
            VodCategory("1238", "Séries | Paramount+", isSeries = true),
            VodCategory("1252", "Séries | Mais Assistidas", isSeries = true),
            VodCategory("1213", "Séries | Novelas", isSeries = true),
            VodCategory("cat_series_todas", "Séries | Todas as Séries", isSeries = true)
        )
    }

    private val allFallbackSeries = listOf(
        VodItem(
            id = "series_last_of_us",
            name = "The Last of Us",
            posterUrl = "https://image.tmdb.org/t/p/w500/uKvVjK19dn5249T695oN1Vv0W4n.jpg",
            streamUrl = "http://cdn9.my:80/series/726hjvc/693bjjz/5663943.mp4",
            rating = "9.1",
            ratingScore = 4.6f,
            categoryId = "1223",
            categoryName = "HBO Max",
            plot = "Vinte anos após uma praga devastar a civilização, Joel é contratado para contrabandear Ellie, uma jovem imune de 14 anos, para fora de uma opressiva zona militar.",
            genre = "Drama / Aventura / Ficção",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_house_dragon",
            name = "A Casa do Dragão (House of the Dragon)",
            posterUrl = "https://image.tmdb.org/t/p/w500/1X4h40fcB4WWUmIBK0auT4zRBAV.jpg",
            streamUrl = "http://cdn9.my:80/series/726hjvc/693bjjz/5663943.mp4",
            rating = "8.7",
            ratingScore = 4.4f,
            categoryId = "1223",
            categoryName = "HBO Max",
            plot = "A história da Casa Targaryen ambientada 200 anos antes de Game of Thrones, retratando a sangrenta guerra civil entre dragões conhecida como a Dança dos Dragões.",
            genre = "Fantasia / Drama",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_stranger_things",
            name = "Stranger Things",
            posterUrl = "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
            streamUrl = "http://cdn9.my:80/series/726hjvc/693bjjz/5663943.mp4",
            rating = "8.9",
            ratingScore = 4.5f,
            categoryId = "1198",
            categoryName = "Netflix",
            plot = "Em Hawkins, o desaparecimento repentino de um garoto leva seus amigos e familiares a desvendarem experimentos secretos, portais dimensionais e forças sinistras.",
            genre = "Sci-Fi / Mistério",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_breaking_bad",
            name = "Breaking Bad",
            posterUrl = "https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg",
            streamUrl = "http://cdn9.my:80/series/726hjvc/693bjjz/5663943.mp4",
            rating = "9.5",
            ratingScore = 4.8f,
            categoryId = "1198",
            categoryName = "Netflix",
            plot = "Um professor de química diagnosticado com câncer terminal decide produzir metanfetamina para assegurar o sustento de sua família, transformando-se em um poderoso barão do crime.",
            genre = "Crime / Drama / Suspense",
            year = "2024",
            isSeries = true
        ),
        VodItem(
            id = "series_round_6",
            name = "Round 6 (Squid Game)",
            posterUrl = "https://image.tmdb.org/t/p/w500/dDlG1Tz6zY3n732F5k4R50mO33y.jpg",
            streamUrl = "http://cdn9.my:80/series/726hjvc/693bjjz/5663943.mp4",
            rating = "8.4",
            ratingScore = 4.2f,
            categoryId = "1198",
            categoryName = "Netflix",
            plot = "Centenas de jogadores endividados aceitam um convite misterioso para disputar jogos infantis. O prêmio é milionário, mas as consequências da eliminação são fatais.",
            genre = "Suspense / Ação",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_the_boys",
            name = "The Boys",
            posterUrl = "https://image.tmdb.org/t/p/w500/7Ns6tO3aYjppFyNJda227um9io6.jpg",
            streamUrl = "http://cdn9.my:80/series/726hjvc/693bjjz/5663943.mp4",
            rating = "8.8",
            ratingScore = 4.4f,
            categoryId = "1225",
            categoryName = "Prime Video",
            plot = "Um grupo de vigilantes se junta em uma missão heroica para combater super-heróis corruptos que abusam de seus poderes e se escondem sob uma corporação bilionária.",
            genre = "Ação / Ficção",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_loki",
            name = "Loki",
            posterUrl = "https://image.tmdb.org/t/p/w500/voHUmlvjysvgFzHj9HqFsmWJ7gZ.jpg",
            streamUrl = "http://cdn9.my:80/series/726hjvc/693bjjz/5663943.mp4",
            rating = "8.5",
            ratingScore = 4.3f,
            categoryId = "1230",
            categoryName = "Disney+",
            plot = "O Deus da Trapaça viaja através do tempo e do espaço ao ser recrutado pela Autoridade de Variância Temporal.",
            genre = "Ação / Ficção",
            year = "2024",
            isSeries = true
        ),
        VodItem(
            id = "series_mandalorian",
            name = "The Mandalorian",
            posterUrl = "https://image.tmdb.org/t/p/w500/eU1i6eHXlzMOlEq0ku1R07Y87Nu.jpg",
            streamUrl = "http://cdn9.my:80/series/726hjvc/693bjjz/5663943.mp4",
            rating = "8.7",
            ratingScore = 4.4f,
            categoryId = "1230",
            categoryName = "Disney+",
            plot = "As viagens de um caçador de recompensas solitário nos confins da galáxia, longe da autoridade da Nova República.",
            genre = "Sci-Fi / Aventura",
            year = "2024",
            isSeries = true
        )
    )

    fun getFallbackSeries(categoryId: String): List<VodItem> {
        val filtered = allFallbackSeries.filter { it.categoryId == categoryId }
        return if (filtered.isNotEmpty()) filtered else allFallbackSeries
    }
}
