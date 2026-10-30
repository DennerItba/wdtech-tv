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

    suspend fun getMovieCategories(): List<VodCategory> = withContext(Dispatchers.IO) {
        if (movieCategoriesCache.isNotEmpty()) {
            return@withContext movieCategoriesCache
        }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_vod_categories"
            val jsonText = httpGet(urlStr) ?: return@withContext emptyList()
            val array = JSONArray(jsonText)
            val list = mutableListOf<VodCategory>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("category_id", "")
                var name = obj.optString("category_name", "").trim()
                if (id.isNotEmpty() && name.isNotEmpty()) {
                    val isAdult = name.contains("XXX", ignoreCase = true) || name.contains("Adulto", ignoreCase = true)
                    list.add(VodCategory(id = id, name = name, isAdult = isAdult, isSeries = false))
                }
            }

            movieCategoriesCache.clear()
            movieCategoriesCache.addAll(list)
            return@withContext list
        } catch (_: Exception) {
            return@withContext emptyList()
        }
    }

    suspend fun getMoviesByCategory(categoryId: String, categoryName: String = ""): List<VodItem> = withContext(Dispatchers.IO) {
        categoryMoviesCache[categoryId]?.let { return@withContext it }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_vod_streams&category_id=$categoryId"
            val jsonText = httpGet(urlStr) ?: return@withContext emptyList()
            val array = JSONArray(jsonText)
            val list = mutableListOf<VodItem>()

            for (i in 0 until array.length()) {
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

            categoryMoviesCache[categoryId] = list
            return@withContext list
        } catch (_: Exception) {
            return@withContext emptyList()
        }
    }

    suspend fun getSeriesCategories(): List<VodCategory> = withContext(Dispatchers.IO) {
        if (seriesCategoriesCache.isNotEmpty()) {
            return@withContext seriesCategoriesCache
        }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_series_categories"
            val jsonText = httpGet(urlStr) ?: return@withContext emptyList()
            val array = JSONArray(jsonText)
            val list = mutableListOf<VodCategory>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("category_id", "")
                val name = obj.optString("category_name", "").trim()
                if (id.isNotEmpty() && name.isNotEmpty()) {
                    val isAdult = name.contains("XXX", ignoreCase = true) || name.contains("Adulto", ignoreCase = true)
                    list.add(VodCategory(id = id, name = name, isAdult = isAdult, isSeries = true))
                }
            }

            seriesCategoriesCache.clear()
            seriesCategoriesCache.addAll(list)
            return@withContext list
        } catch (_: Exception) {
            return@withContext emptyList()
        }
    }

    suspend fun getSeriesByCategory(categoryId: String, categoryName: String = ""): List<VodItem> = withContext(Dispatchers.IO) {
        categorySeriesCache[categoryId]?.let { return@withContext it }

        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_series&category_id=$categoryId"
            val jsonText = httpGet(urlStr) ?: return@withContext emptyList()
            val array = JSONArray(jsonText)
            val list = mutableListOf<VodItem>()

            for (i in 0 until array.length()) {
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
                            genre = genre,
                            year = releaseDate.take(4).ifEmpty { null },
                            isSeries = true,
                            streamUrl = ""
                        )
                    )
                }
            }

            categorySeriesCache[categoryId] = list
            return@withContext list
        } catch (_: Exception) {
            return@withContext emptyList()
        }
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
                connectTimeout = 8000
                readTimeout = 12000
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
}
