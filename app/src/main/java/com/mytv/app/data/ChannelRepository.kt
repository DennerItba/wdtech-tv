package com.mytv.app.data

import android.util.Log
import com.mytv.app.App
import com.mytv.app.R
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.MainSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ChannelRepository {

    private const val TAG = "ChannelRepository"
    private const val SERVER_BASE = "http://cdn9.my:80"
    private const val USERNAME = "726hjvc"
    private const val PASSWORD = "693bjjz"

    @Volatile
    private var cachedCategories: List<ChannelCategory>? = null
    private val lock = Any()

    fun getCategories(): List<ChannelCategory> {
        cachedCategories?.let { return it }
        synchronized(lock) {
            cachedCategories?.let { return it }
            val loaded = loadFromAssets()
            cachedCategories = loaded
            return loaded
        }
    }

    private fun loadFromAssets(): List<ChannelCategory> {
        val list = mutableListOf<ChannelCategory>()
        try {
            val app = App.instance
            val inputStream = app.assets.open("channels.json")
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            val jsonText = reader.use { it.readText() }
            val jsonArray = JSONArray(jsonText)

            for (i in 0 until jsonArray.length()) {
                val catObj = jsonArray.getJSONObject(i)
                val catId = catObj.optString("id")
                val catName = catObj.optString("name")
                val iconName = catObj.optString("icon", "ic_all_channels")
                val isLocked = catObj.optBoolean("isLocked", false)
                val iconRes = resolveIconRes(iconName)

                val channelsArray = catObj.optJSONArray("channels") ?: JSONArray()
                val channelsList = mutableListOf<Channel>()

                for (j in 0 until channelsArray.length()) {
                    val chObj = channelsArray.getJSONObject(j)
                    val id = chObj.optString("id")
                    val number = chObj.optString("number")
                    val title = chObj.optString("title")
                    val category = chObj.optString("category")
                    val streamUrl = chObj.optString("streamUrl")
                    val logoUrl = chObj.optString("logoUrl").takeIf { it.isNotEmpty() }
                    val quality = chObj.optString("quality", "HD")

                    val backupsArray = chObj.optJSONArray("backupUrls")
                    val backups = mutableListOf<String>()
                    if (backupsArray != null) {
                        for (k in 0 until backupsArray.length()) {
                            backups.add(backupsArray.getString(k))
                        }
                    }

                    channelsList.add(
                        Channel(
                            id = id,
                            number = number,
                            title = title,
                            category = category,
                            streamUrl = streamUrl,
                            logoUrl = logoUrl,
                            quality = quality,
                            backupUrls = backups
                        )
                    )
                }

                list.add(
                    ChannelCategory(
                        id = catId,
                        name = catName,
                        iconRes = iconRes,
                        isLocked = isLocked,
                        channels = channelsList
                    )
                )
            }
            Log.i(TAG, "Carregadas ${list.size} categorias com sucesso dos assets.")
        } catch (t: Throwable) {
            Log.e(TAG, "Erro ao carregar canais dos assets: ${t.message}", t)
            return getFallbackCategories()
        }
        return if (list.isNotEmpty()) list else getFallbackCategories()
    }

    private fun resolveIconRes(iconName: String): Int {
        return when (iconName) {
            "ic_sports_tv" -> R.drawable.ic_sports_tv
            "ic_premiere" -> R.drawable.ic_premiere
            "ic_movie_vod" -> R.drawable.ic_movie_vod
            "ic_series_vod" -> R.drawable.ic_series_vod
            "ic_star_kids" -> R.drawable.ic_star_kids
            else -> R.drawable.ic_all_channels
        }
    }

    fun getCategoriesForSection(section: MainSection): List<ChannelCategory> {
        val allCats = getCategories()

        return when (section) {
            MainSection.TV -> {
                val tvCats = mutableListOf<ChannelCategory>()
                tvCats.addAll(allCats)

                // Categoria Todos os Canais adicionada no final da lista
                val allChannels = allCats.filter { !it.isLocked }.flatMap { it.channels }.distinctBy { it.id }
                if (allChannels.isNotEmpty()) {
                    tvCats.add(
                        ChannelCategory(
                            id = "cat_tv_todos",
                            name = "⭐ Todos os Canais (${allChannels.size})",
                            iconRes = R.drawable.ic_all_channels,
                            channels = allChannels
                        )
                    )
                }
                tvCats
            }

            MainSection.FILMES -> {
                val movieCats = mutableListOf<ChannelCategory>()
                val telecine = allCats.find { it.id == "cat_telecine" }
                val hbo = allCats.find { it.id == "cat_hbo" }
                val filmes = allCats.find { it.id == "cat_filmes" }
                val cinesky = allCats.find { it.id == "cat_cinesky" }
                val prime = allCats.find { it.id == "cat_prime" }
                val hbomax = allCats.find { it.id == "cat_hbomax" }
                val paramount = allCats.find { it.id == "cat_paramount" }

                telecine?.let { movieCats.add(it) }
                hbo?.let { movieCats.add(it) }
                filmes?.let { movieCats.add(it) }
                cinesky?.let { movieCats.add(it) }
                prime?.let { movieCats.add(it) }
                hbomax?.let { movieCats.add(it) }
                paramount?.let { movieCats.add(it) }

                val allFilmes = movieCats.flatMap { it.channels }.distinctBy { it.id }
                movieCats.add(
                    0,
                    ChannelCategory(
                        id = "cat_filmes_todos",
                        name = "⭐ Todos os Filmes (${allFilmes.size})",
                        iconRes = R.drawable.ic_all_channels,
                        channels = allFilmes
                    )
                )
                movieCats
            }

            MainSection.SERIES -> {
                val seriesCats = mutableListOf<ChannelCategory>()
                val series24h = allCats.find { it.id == "cat_24h_series" }
                val filmesSeries = allCats.find { it.id == "cat_filmes" }
                val h265 = allCats.find { it.id == "cat_h265" }

                val seriesFromCanais = filmesSeries?.channels?.filter { ch ->
                    val t = ch.title.lowercase()
                    t.contains("warner") || t.contains("sony") || t.contains("universal") ||
                            t.contains("axn") || t.contains("novela")
                } ?: emptyList()

                if (seriesFromCanais.isNotEmpty()) {
                    seriesCats.add(
                        ChannelCategory(
                            id = "cat_series_canais",
                            name = "📺 Canais de Séries",
                            iconRes = R.drawable.ic_series_vod,
                            channels = seriesFromCanais
                        )
                    )
                }

                series24h?.let { seriesCats.add(it) }

                val allSeries = (seriesFromCanais + (series24h?.channels ?: emptyList())).distinctBy { it.id }
                seriesCats.add(
                    0,
                    ChannelCategory(
                        id = "cat_series_todos",
                        name = "⭐ Todas as Séries (${allSeries.size})",
                        iconRes = R.drawable.ic_all_channels,
                        channels = allSeries
                    )
                )
                seriesCats
            }

            MainSection.INFANTIL -> {
                val kidsCats = mutableListOf<ChannelCategory>()
                val kids = allCats.find { it.id == "cat_kids" }
                val desenhos24h = allCats.find { it.id == "cat_24h_desenhos" }
                val disney = allCats.find { it.id == "cat_disney" }

                kids?.let { kidsCats.add(it) }
                desenhos24h?.let { kidsCats.add(it) }
                disney?.let { kidsCats.add(it) }

                val allKids = (kidsCats.flatMap { it.channels }).distinctBy { it.id }
                kidsCats.add(
                    0,
                    ChannelCategory(
                        id = "cat_kids_todos",
                        name = "⭐ Todos os Infantis (${allKids.size})",
                        iconRes = R.drawable.ic_all_channels,
                        channels = allKids
                    )
                )
                kidsCats
            }

            MainSection.FUTEBOL -> {
                val futCats = mutableListOf<ChannelCategory>()
                val jogosDia = allCats.find { it.id == "cat_jogos_dia" }
                val premiere = allCats.find { it.id == "cat_premiere" }
                val sportv = allCats.find { it.id == "cat_sportv" }
                val espn = allCats.find { it.id == "cat_espn" }
                val lutas = allCats.find { it.id == "cat_lutas" }
                val nba = allCats.find { it.id == "cat_nba" }
                val nfl = allCats.find { it.id == "cat_nfl" }

                jogosDia?.let { futCats.add(it) }
                premiere?.let { futCats.add(it) }
                sportv?.let { futCats.add(it) }
                espn?.let { futCats.add(it) }
                lutas?.let { futCats.add(it) }
                nba?.let { futCats.add(it) }
                nfl?.let { futCats.add(it) }

                val allFut = (futCats.flatMap { it.channels }).distinctBy { it.id }
                futCats.add(
                    ChannelCategory(
                        id = "cat_futebol_todos",
                        name = "⭐ Todos do Futebol (${allFut.size})",
                        iconRes = R.drawable.ic_all_channels,
                        channels = allFut
                    )
                )
                futCats
            }
        }
    }

    suspend fun fetchJogosDoDia(): List<Channel> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Channel>()
        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_live_streams&category_id=16630"
            val json = httpGet(urlStr) ?: return@withContext emptyList()
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val sid = obj.optInt("stream_id")
                val name = obj.optString("name")
                val icon = obj.optString("stream_icon")
                list.add(
                    Channel(
                        id = "ch_jogos_$sid",
                        number = "${i + 1}",
                        title = name,
                        category = "🏆 Jogos do Dia (Ao Vivo)",
                        streamUrl = "$SERVER_BASE/live/$USERNAME/$PASSWORD/$sid.ts",
                        logoUrl = icon.takeIf { it.isNotEmpty() },
                        quality = "FHD",
                        backupUrls = listOf(
                            "$SERVER_BASE/$USERNAME/$PASSWORD/$sid",
                            "$SERVER_BASE/live/$USERNAME/$PASSWORD/$sid.m3u8",
                            "http://e.cdn9.my/live/$USERNAME/$PASSWORD/$sid.ts"
                        )
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar Jogos do Dia: ${e.message}")
        }
        list
    }

    private fun httpGet(urlStr: String): String? {
        return try {
            val conn = URL(urlStr).openConnection() as HttpURLConnection
            conn.connectTimeout = 4000
            conn.readTimeout = 4000
            conn.setRequestProperty("User-Agent", "IPTVSmartersPlayer")
            if (conn.responseCode == 200) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun getFallbackCategories(): List<ChannelCategory> {
        return listOf(
            ChannelCategory(
                id = "cat_abertos",
                name = "📺 Canais Abertos",
                iconRes = R.drawable.ic_all_channels,
                channels = listOf(
                    Channel(
                        id = "ch_24351656",
                        number = "01",
                        title = "AGRO BRASIL HD",
                        category = "📺 Canais Abertos",
                        streamUrl = "http://cdn9.my:80/live/726hjvc/693bjjz/24351656.ts",
                        quality = "HD",
                        backupUrls = listOf("http://cdn9.my/726hjvc/693bjjz/24351656", "http://cdn9.my:80/live/726hjvc/693bjjz/24351656.m3u8")
                    )
                )
            )
        )
    }
}
