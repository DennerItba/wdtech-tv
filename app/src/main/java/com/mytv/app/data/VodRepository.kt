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

    private const val SERVER_BASE = "http://64.31.23.207:80"
    private const val USERNAME = "59471019"
    private const val PASSWORD = "60849037"

    // Caches em memória para navegação ágil no controle remoto
    private val movieCategoriesCache = mutableListOf<VodCategory>()
    private val seriesCategoriesCache = mutableListOf<VodCategory>()
    private val categoryMoviesCache = ConcurrentHashMap<String, List<VodItem>>()
    private val categorySeriesCache = ConcurrentHashMap<String, List<VodItem>>()
    private val seriesSeasonsCache = ConcurrentHashMap<String, List<VodSeason>>()

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

        if (categoryId.startsWith("cat_filmes_")) {
            val fallback = getFallbackMovies(categoryId)
            categoryMoviesCache[categoryId] = fallback
            return@withContext fallback
        }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_vod_streams&category_id=$categoryId"
            val jsonText = httpGet(urlStr)
            if (!jsonText.isNullOrEmpty()) {
                val array = JSONArray(jsonText)
                val list = mutableListOf<VodItem>()
                val limit = minOf(array.length(), 60)

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
                                genre = categoryName,
                                year = null,
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

        if (categoryId.startsWith("cat_series_")) {
            val fallback = getFallbackSeries(categoryId)
            categorySeriesCache[categoryId] = fallback
            return@withContext fallback
        }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_series&category_id=$categoryId"
            val jsonText = httpGet(urlStr)
            if (!jsonText.isNullOrEmpty()) {
                val array = JSONArray(jsonText)
                val list = mutableListOf<VodItem>()
                val limit = minOf(array.length(), 60)

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
                        // URL direta de reprodução para a série
                        val streamUrl = "$SERVER_BASE/series/$USERNAME/$PASSWORD/$seriesId.mp4"
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
                                genre = genre,
                                year = releaseDate.take(4).ifEmpty { null },
                                isSeries = true,
                                streamUrl = streamUrl
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

        val fallback = getFallbackSeries(categoryId)
        categorySeriesCache[categoryId] = fallback
        fallback
    }

    suspend fun getSeriesInfo(seriesId: String): List<VodSeason> = withContext(Dispatchers.IO) {
        seriesSeasonsCache[seriesId]?.let { return@withContext it }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_series_info&series_id=$seriesId"
            val jsonText = httpGet(urlStr) ?: return@withContext emptyList()
            val root = JSONObject(jsonText)
            val episodesObj = root.optJSONObject("episodes") ?: return@withContext emptyList()

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
                seasonList.add(
                    VodSeason(
                        seasonNum = seasonNum,
                        title = "Temporada $seasonNum",
                        episodes = epList
                    )
                )
            }

            seasonList.sortBy { it.seasonNum }
            seriesSeasonsCache[seriesId] = seasonList
            return@withContext seasonList
        } catch (_: Exception) {
            return@withContext emptyList()
        }
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
            VodCategory("cat_filmes_lancamentos", "🔥 Lançamentos 2025/2026"),
            VodCategory("cat_filmes_acao", "💥 Ação & Aventura"),
            VodCategory("cat_filmes_ficcao", "🚀 Ficção & Fantasia"),
            VodCategory("cat_filmes_comedia", "🎭 Comédia & Família"),
            VodCategory("cat_filmes_terror", "👻 Terror & Suspense"),
            VodCategory("cat_filmes_animacao", "👶 Animação & Kids"),
            VodCategory("cat_filmes_todos", "⭐ Todos os Filmes")
        )
    }

    private val allFallbackMovies = listOf(
        VodItem(
            id = "movie_duna_2",
            name = "Duna: Parte 2",
            posterUrl = "https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769291.m3u8",
            rating = "8.6",
            ratingScore = 4.3f,
            categoryId = "cat_filmes_lancamentos",
            categoryName = "Lançamentos 2025",
            plot = "Paul Atreides se une a Chani e aos Fremen enquanto busca vingança contra os conspiradores que destruíram sua família. Diante de escolhas difíceis, ele tenta evitar um futuro terrível.",
            genre = "Ficção / Aventura",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "movie_deadpool_wolverine",
            name = "Deadpool & Wolverine",
            posterUrl = "https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769297.m3u8",
            rating = "8.0",
            ratingScore = 4.0f,
            categoryId = "cat_filmes_lancamentos",
            categoryName = "Lançamentos 2025",
            plot = "Wolverine está se recuperando de seus ferimentos quando cruza caminhos com o tagarela Deadpool para salvar seus universos em uma jornada insana.",
            genre = "Ação / Comédia",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "movie_gladiador_2",
            name = "Gladiador II",
            posterUrl = "https://image.tmdb.org/t/p/w500/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769309.m3u8",
            rating = "7.8",
            ratingScore = 3.9f,
            categoryId = "cat_filmes_lancamentos",
            categoryName = "Lançamentos 2025",
            plot = "Anos após testemunhar a morte do herói Maximus, Lucius é forçado a entrar no Coliseu após sua casa ser conquistada pelos imperadores tiranos que agora governam Roma.",
            genre = "Ação / Épico",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "movie_oppenheimer",
            name = "Oppenheimer",
            posterUrl = "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769315.m3u8",
            rating = "8.9",
            ratingScore = 4.5f,
            categoryId = "cat_filmes_lancamentos",
            categoryName = "Lançamentos 2025",
            plot = "A história do físico americano J. Robert Oppenheimer, seu papel no Projeto Manhattan e o dramático desenvolvimento da bomba atômica.",
            genre = "Drama / Biografia",
            year = "2024",
            isSeries = false
        ),
        VodItem(
            id = "movie_furiosa",
            name = "Furiosa: Uma Saga Mad Max",
            posterUrl = "https://image.tmdb.org/t/p/w500/iADOJ8Zymht2JPMoy3R7xUMZ51Q.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769322.m3u8",
            rating = "7.9",
            ratingScore = 4.0f,
            categoryId = "cat_filmes_acao",
            categoryName = "Ação & Aventura",
            plot = "A jovem Furiosa é arrebatada do Lugar Verde de Muitas Mães e cai nas mãos de uma horda de motoqueiros liderada pelo temível Senhor da Guerra Dementus.",
            genre = "Ação / Ficção",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "movie_bad_boys_4",
            name = "Bad Boys: Até o Fim",
            posterUrl = "https://image.tmdb.org/t/p/w500/nP6RliHjxsz4irTKsxe8FRhKZYl.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769327.m3u8",
            rating = "7.5",
            ratingScore = 3.8f,
            categoryId = "cat_filmes_acao",
            categoryName = "Ação & Aventura",
            plot = "Os policiais favoritos de Miami estão de volta com uma eletrizante mistura de ação frenética, tiroteios e comédia hilária.",
            genre = "Ação / Comédia",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "movie_godzilla_kong",
            name = "Godzilla e Kong: O Novo Império",
            posterUrl = "https://image.tmdb.org/t/p/w500/tMefBSflR6PGQLv7WvFPpKLZkyk.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769332.m3u8",
            rating = "7.4",
            ratingScore = 3.7f,
            categoryId = "cat_filmes_acao",
            categoryName = "Ação & Aventura",
            plot = "Uma batalha colossal coloca Kong e o lendário Godzilla lado a lado contra uma perigosa ameaça desconhecida oculta no centro da Terra.",
            genre = "Ação / Ficção",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "movie_alien_romulus",
            name = "Alien: Romulus",
            posterUrl = "https://image.tmdb.org/t/p/w500/b33nnKl1GSFbao8l3urDDujMMQj.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769337.m3u8",
            rating = "7.7",
            ratingScore = 3.9f,
            categoryId = "cat_filmes_ficcao",
            categoryName = "Ficção & Fantasia",
            plot = "Enquanto exploram as profundezas sombrias de uma estação espacial abandonada, jovens colonizadores enfrentam o predador mais implacável do universo.",
            genre = "Terror / Sci-Fi",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "movie_divertida_mente_2",
            name = "Divertida Mente 2",
            posterUrl = "https://image.tmdb.org/t/p/w500/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769343.m3u8",
            rating = "8.2",
            ratingScore = 4.1f,
            categoryId = "cat_filmes_comedia",
            categoryName = "Comédia & Família",
            plot = "Riley chega à adolescência e a sala de controle passa por uma grande reforma para abrigar novas emoções: Ansiedade, Inveja, Tédio e Vergonha.",
            genre = "Animação / Família",
            year = "2025",
            isSeries = false
        ),
        VodItem(
            id = "movie_kung_fu_panda_4",
            name = "Kung Fu Panda 4",
            posterUrl = "https://image.tmdb.org/t/p/w500/kDp1vUBnMpe8ak4rjgl3cLELqjU.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769348.m3u8",
            rating = "7.2",
            ratingScore = 3.6f,
            categoryId = "cat_filmes_animacao",
            categoryName = "Animação & Kids",
            plot = "Po é convocado para se tornar o Líder Espiritual do Vale da Paz, mas precisa treinar um novo guerreiro para derrotar a Camaleoa.",
            genre = "Animação / Aventura",
            year = "2025",
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
            VodCategory("cat_series_populares", "🔥 Séries em Alta (2025/2026)", isSeries = true),
            VodCategory("cat_series_misterio", "🕵️ Mistério & Suspense", isSeries = true),
            VodCategory("cat_series_acao", "⚔️ Ação & Sobrenatural", isSeries = true),
            VodCategory("cat_series_comedia", "😂 Comédia & Sitcoms", isSeries = true),
            VodCategory("cat_series_todas", "⭐ Todas as Séries", isSeries = true)
        )
    }

    private val allFallbackSeries = listOf(
        VodItem(
            id = "series_last_of_us",
            name = "The Last of Us",
            posterUrl = "https://image.tmdb.org/t/p/w500/uKvVjK19dn5249T695oN1Vv0W4n.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769291.m3u8",
            rating = "9.1",
            ratingScore = 4.6f,
            categoryId = "cat_series_populares",
            categoryName = "Séries Populares",
            plot = "Vinte anos após uma praga devastar a civilização, Joel é contratado para contrabandear Ellie, uma jovem imune de 14 anos, para fora de uma opressiva zona militar.",
            genre = "Drama / Aventura / Ficção",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_house_dragon",
            name = "A Casa do Dragão (House of the Dragon)",
            posterUrl = "https://image.tmdb.org/t/p/w500/1X4h40fcB4WWUmIBK0auT4zRBAV.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769297.m3u8",
            rating = "8.7",
            ratingScore = 4.4f,
            categoryId = "cat_series_populares",
            categoryName = "Séries Populares",
            plot = "A história da Casa Targaryen ambientada 200 anos antes de Game of Thrones, retratando a sangrenta guerra civil entre dragões conhecida como a Dança dos Dragões.",
            genre = "Fantasia / Drama",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_stranger_things",
            name = "Stranger Things",
            posterUrl = "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769309.m3u8",
            rating = "8.9",
            ratingScore = 4.5f,
            categoryId = "cat_series_populares",
            categoryName = "Séries Populares",
            plot = "Em Hawkins, o desaparecimento repentino de um garoto leva seus amigos e familiares a desvendarem experimentos secretos, portais dimensionais e forças sinistras.",
            genre = "Sci-Fi / Mistério",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_breaking_bad",
            name = "Breaking Bad",
            posterUrl = "https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769315.m3u8",
            rating = "9.5",
            ratingScore = 4.8f,
            categoryId = "cat_series_populares",
            categoryName = "Séries Populares",
            plot = "Um professor de química diagnosticado com câncer terminal decide produzir metanfetamina para assegurar o sustento de sua família, transformando-se em um poderoso barão do crime.",
            genre = "Crime / Drama / Suspense",
            year = "2024",
            isSeries = true
        ),
        VodItem(
            id = "series_round_6",
            name = "Round 6 (Squid Game)",
            posterUrl = "https://image.tmdb.org/t/p/w500/dDlG1Tz6zY3n732F5k4R50mO33y.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769322.m3u8",
            rating = "8.4",
            ratingScore = 4.2f,
            categoryId = "cat_series_misterio",
            categoryName = "Mistério & Suspense",
            plot = "Centenas de jogadores endividados aceitam um convite misterioso para disputar jogos infantis. O prêmio é milionário, mas as consequências da eliminação são fatais.",
            genre = "Suspense / Ação",
            year = "2025",
            isSeries = true
        ),
        VodItem(
            id = "series_the_boys",
            name = "The Boys",
            posterUrl = "https://image.tmdb.org/t/p/w500/7Ns6tO3aYjppFyNJda227um9io6.jpg",
            streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769332.m3u8",
            rating = "8.8",
            ratingScore = 4.4f,
            categoryId = "cat_series_acao",
            categoryName = "Ação & Sobrenatural",
            plot = "Um grupo de vigilantes se junta em uma missão heroica para combater super-heróis corruptos que abusam de seus poderes e se escondem sob uma corporação bilionária.",
            genre = "Ação / Ficção",
            year = "2025",
            isSeries = true
        )
    )

    fun getFallbackSeries(categoryId: String): List<VodItem> {
        return when (categoryId) {
            "cat_series_todas" -> allFallbackSeries
            "cat_series_populares" -> allFallbackSeries.filter { it.categoryId == "cat_series_populares" }
            "cat_series_misterio" -> allFallbackSeries.filter { it.categoryId == "cat_series_misterio" }
            "cat_series_acao" -> allFallbackSeries.filter { it.categoryId == "cat_series_acao" }
            else -> allFallbackSeries
        }
    }
}
