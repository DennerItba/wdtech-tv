package com.mytv.app.data

import android.content.Context
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object UniTvCatalogManager {

    private const val PRIMARY_BASE_URL = "https://unitv.cdntv.com.br/rest-api/v130/"
    private const val BACKUP_BASE_URL = "https://unifibra.me/rest-api/v130/"
    private const val PRIMARY_API_KEY = "9japcl6p8hk0mf3jb3jcjfz3"
    private const val BACKUP_API_KEY = "rjmgz6l3fsh9k04k2vuo5ok1"
    private const val ENDPOINT_CATEGORIES = "all_tv_channel_by_category"
    private const val CACHE_FILE_NAME = "unitv_catalog_cache.json"

    suspend fun syncCatalog(context: Context): List<ChannelCategory>? = withContext(Dispatchers.IO) {
        // 1. Tenta baixar do servidor primário (CDN Oficial)
        var jsonResponse = fetchFromApi(PRIMARY_BASE_URL, PRIMARY_API_KEY)

        // 2. Se falhar, tenta o servidor de contingência (Backup)
        if (jsonResponse.isNullOrEmpty()) {
            jsonResponse = fetchFromApi(BACKUP_BASE_URL, BACKUP_API_KEY)
        }

        if (!jsonResponse.isNullOrEmpty()) {
            // Salva no cache local do dispositivo
            saveToCache(context, jsonResponse)
            return@withContext parseJsonCatalog(jsonResponse)
        }

        // 3. Se ambos falharem (offline), tenta ler do cache salvo anteriormente
        val cached = readFromCache(context)
        if (!cached.isNullOrEmpty()) {
            return@withContext parseJsonCatalog(cached)
        }

        return@withContext null
    }

    private fun fetchFromApi(baseUrl: String, apiKey: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(baseUrl + ENDPOINT_CATEGORIES)
            conn = (url.openConnection() as HttpsURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("API-KEY", apiKey)
                setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 10; TV) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                )
                setRequestProperty("Accept", "application/json")
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun parseJsonCatalog(rawJson: String): List<ChannelCategory>? {
        return try {
            val jsonArray = JSONArray(rawJson)
            val baseCategories = ChannelRepository.getCategories().toMutableList()

            // Dicionário de canais atualizados vindos do servidor
            val remoteChannelsByCat = mutableMapOf<String, MutableList<Channel>>()

            val categoryMapping = mapOf(
                "Globo" to "cat_globo",
                "SBT" to "cat_sbt",
                "Record" to "cat_record",
                "Band" to "cat_band",
                "Noticias e Jornalismo" to "cat_noticias",
                "ESPN" to "cat_esportes",
                "Acao e Entretenimento" to "cat_esportes",
                "Diversos e Internacionais" to "cat_esportes",
                "Filmes e Series" to "cat_filmes",
                "Documentarios e Filmes" to "cat_filmes",
                "Discoverys" to "cat_docs",
                "Filmes e Infantil" to "cat_kids",
                "Musicas" to "cat_musica",
                "Agro e Rural" to "cat_agro",
                "Educacao e Cultura" to "cat_cultura",
                "Religiosos" to "cat_religiosos",
                "Religiosos 2" to "cat_religiosos",
                "Estilo de Vida" to "cat_variedades",
                "Entretenimento Geral" to "cat_variedades",
                "Variedades e Outros" to "cat_variedades",
                "Canais Diversos" to "cat_variedades"
            )

            for (i in 0 until jsonArray.length()) {
                val catObj = jsonArray.getJSONObject(i)
                val catTitle = catObj.optString("title", "")
                val targetCatId = categoryMapping[catTitle] ?: "cat_variedades"
                val chList = remoteChannelsByCat.getOrPut(targetCatId) { mutableListOf() }

                val channelsArr = catObj.optJSONArray("channels") ?: JSONArray()
                for (j in 0 until channelsArr.length()) {
                    val chObj = channelsArr.getJSONObject(j)
                    val id = chObj.optString("live_tv_id", "")
                    val name = chObj.optString("tv_name", "").trim()
                    val streamUrl = chObj.optString("stream_url", "").trim()
                    val logoUrl = chObj.optString("thumbnail_url", "").trim()
                    val quality = chObj.optString("stream_label", "HD").trim()
                    val desc = chObj.optString("description", "").trim()

                    // Pula Cazé TV aqui pois ela já tem posição de honra como nº 1 no topo de esportes
                    if (name.contains("caze", ignoreCase = true) || id == "320") {
                        continue
                    }

                    // Blacklist Definitiva: Expurgar canais instáveis ou descontinuados
                    val nameLower = name.lowercase()
                    if (nameLower.contains("cinesky") || nameLower.contains("cine sky")) {
                        continue
                    }

                    if (streamUrl.isNotEmpty()) {
                        chList.add(
                            Channel(
                                id = "ch_$id",
                                number = "00",
                                title = name,
                                category = catTitle,
                                streamUrl = streamUrl,
                                logoUrl = if (logoUrl.isNotEmpty()) logoUrl else null,
                                description = desc,
                                quality = quality
                            )
                        )
                    }
                }
            }

            // Atualiza as categorias existentes com os dados mais recentes do servidor
            val updatedCategories = baseCategories.map { cat ->
                val remoteChs = remoteChannelsByCat[cat.id]
                if (remoteChs != null && remoteChs.isNotEmpty()) {
                    // Mantém canais especiais locais (como CazéTV, F1, SporTV, ESPN, PPV, 4K, H265, Sony, etc.)
                    val preservedSpecial = cat.channels.filter { localCh ->
                        localCh.streamUrl.contains("64.31.23.207") ||
                        localCh.id.contains("caze", ignoreCase = true) ||
                        localCh.id.startsWith("ch_f1") ||
                        localCh.id.startsWith("prem_") ||
                        localCh.id.startsWith("tc_") ||
                        localCh.id.startsWith("hbo_") ||
                        localCh.id.startsWith("warner_") ||
                        localCh.id.startsWith("megapix_") ||
                        localCh.id.startsWith("ch_fazenda") ||
                        localCh.id.startsWith("ch_bbb") ||
                        localCh.id.startsWith("ch_history") ||
                        localCh.id.startsWith("sportv_") ||
                        localCh.id.startsWith("espn_") ||
                        localCh.id.startsWith("ch_4k_") ||
                        localCh.id.startsWith("ch_h265_") ||
                        localCh.id.startsWith("ch_sony") ||
                        localCh.id.startsWith("ch_universal") ||
                        localCh.id.startsWith("ch_studio") ||
                        localCh.id.startsWith("ch_paramount") ||
                        localCh.id.startsWith("ch_cinemax") ||
                        localCh.id.startsWith("disc_") ||
                        localCh.id.startsWith("doc_") ||
                        localCh.id.startsWith("var_") ||
                        localCh.id.startsWith("ppv_") ||
                        cat.name.contains("ESPN") ||
                        cat.name.contains("SporTV") ||
                        cat.name.contains("Premiere") ||
                        cat.name.contains("Discovery") ||
                        cat.name.contains("Fazenda") ||
                        cat.isLocked // Canais adultos protegidos
                    }

                    val merged = (preservedSpecial + remoteChs).distinctBy { it.id }
                    cat.copy(channels = merged)
                } else {
                    cat
                }
            }

            // Renumera todos sequencialmente e enriquece com EPG
            var count = 1
            val renumberedCats = updatedCategories.map { c ->
                val renumbered = c.channels.map { ch ->
                    ch.copy(number = String.format("%02d", count++))
                }
                c.copy(channels = renumbered)
            }
            EpgRepository.enrichCategories(renumberedCats)
        } catch (_: Exception) {
            null
        }
    }

    private fun saveToCache(context: Context, data: String) {
        try {
            val file = File(context.cacheDir, CACHE_FILE_NAME)
            file.writeText(data)
        } catch (_: Exception) {}
    }

    private fun readFromCache(context: Context): String? {
        return try {
            val file = File(context.cacheDir, CACHE_FILE_NAME)
            if (file.exists()) file.readText() else null
        } catch (_: Exception) {
            null
        }
    }
}
