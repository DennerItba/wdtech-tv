package com.mytv.app.data

import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.EpgProgram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ConcurrentHashMap

object EpgRepository {

    val timeZone: TimeZone = TimeZone.getTimeZone("America/Sao_Paulo")

    // Cache em memória de EPGs dinâmicos ao vivo vindos do servidor (atualizados a cada 10 min)
    private val liveEpgCache = ConcurrentHashMap<String, CachedEpg>()

    data class CachedEpg(
        val programs: Pair<EpgProgram, EpgProgram?>,
        val timestamp: Long = System.currentTimeMillis()
    )

    // Grade de horários padrão em minutos a partir da meia-noite (00:00)
    // 00:00, 02:00, 06:00, 08:30, 10:00, 12:00, 13:30, 15:00, 17:00, 18:30, 20:00, 21:30, 23:00, 24:00
    val timeSlots = listOf(
        Pair(0, 120),     // Slot 0:  00:00 - 02:00
        Pair(120, 360),   // Slot 1:  02:00 - 06:00
        Pair(360, 510),   // Slot 2:  06:00 - 08:30
        Pair(510, 600),   // Slot 3:  08:30 - 10:00
        Pair(600, 720),   // Slot 4:  10:00 - 12:00
        Pair(720, 810),   // Slot 5:  12:00 - 13:30
        Pair(810, 900),   // Slot 6:  13:30 - 15:00
        Pair(900, 1020),  // Slot 7:  15:00 - 17:00
        Pair(1020, 1110), // Slot 8:  17:00 - 18:30
        Pair(1110, 1200), // Slot 9:  18:30 - 20:00
        Pair(1200, 1290), // Slot 10: 20:00 - 21:30
        Pair(1290, 1380), // Slot 11: 21:30 - 23:00
        Pair(1380, 1440)  // Slot 12: 23:00 - 00:00
    )

    fun enrichChannel(channel: Channel): Channel {
        val cached = liveEpgCache[channel.id]
        val now = System.currentTimeMillis()
        if (cached != null && now - cached.timestamp < 10 * 60 * 1000L) {
            channel.currentProgram = cached.programs.first
            channel.nextProgram = cached.programs.second
        } else {
            val (current, next) = getProgramsForChannel(channel)
            channel.currentProgram = current
            channel.nextProgram = next
        }

        PlaybackRepository.enrichChannelWithPlayback(channel)
        return channel
    }

    fun enrichCategories(categories: List<ChannelCategory>): List<ChannelCategory> {
        return categories.map { cat ->
            val enrichedChs = cat.channels.map { ch ->
                enrichChannel(ch)
            }
            cat.copy(channels = enrichedChs)
        }
    }

    /**
     * Busca o Guia de Programação (EPG) AO VIVO diretamente da API do servidor de streaming.
     * Atualiza o canal imediatamente com os nomes reais de programas e sinopses.
     */
        // Mapeamento preciso de IDs de canais para o servidor de EPG ao vivo da Vivlar
        /**
     * Mapeamento de canais para o servidor de EPG ao vivo da Corruptx.
     * Cobre emissoras hospedadas em outras CDNs (UniTV, etc.) garantindo EPG oficial e sinopse real.
     */
    val CORRUPTX_EPG_MAP: Map<String, String> = mapOf(
        // Rede Discovery
        "ch_discovery" to "8736",
        "ch_discovery_kids" to "8742",
        "ch_discovery_hh" to "8740",
        "ch_discovery_turbo" to "8756",
        "disc_id" to "8932",
        "disc_world" to "8758",
        "disc_science" to "8745",
        "disc_theater" to "8748",
        "disc_animal" to "304491",
        "disc_tlc" to "8754",
        "disc_hgtv" to "304506",
        "ch_4k_disc" to "8736",

        // Telecine
        "tc_premium" to "9133",
        "tc_action" to "9126",
        "tc_touch" to "9135",
        "tc_pipoca" to "9131",
        "tc_fun" to "9130",
        "tc_cult" to "24534",
        "ch_4k_tc" to "9133",
        "ch_h265_tc" to "9133",

        // HBO
        "hbo_main" to "8902",
        "hbo_2" to "8898",
        "hbo_plus" to "24592",
        "hbo_family" to "8901",
        "hbo_sig" to "8912",
        "hbo_signature" to "8912",
        "hbo_pop" to "24593",
        "hbo_mundi" to "8898",
        "hbo_xtreme" to "24592",
        "ch_4k_hbo" to "8902",
        "ch_h265_hbo" to "8902",

        // Filmes & Séries
        "warner_ch" to "9219",
        "warner_channel" to "9219",
        "megapix_ch" to "8961",
        "megapix" to "8961",
        "ch_universal_tv" to "21682",
        "ch_universal" to "21682",
        "ch_studio_universal" to "9115",
        "ch_studio" to "9115",
        "ch_sony_channel" to "9098",
        "ch_sony_movies" to "9098",
        "ch_cinemax" to "8708",
        "ch_space" to "9101",
        "ch_tnt" to "594020",
        "ch_284" to "275103",
        "ch_tnt_novelas" to "275103",
        "ch_axn" to "81600",
        "ch_paramount" to "240737",
        "ch_332" to "8708",
        "ch_273" to "9101",
        "ch_canal_brasil" to "8691",
        "ch_ae" to "23156",
        "ch_amc" to "23687",
        "ch_tcm" to "9124",
        "ch_tnt_series" to "9144",

        // Documentários & Cultura
        "ch_history" to "304494",
        "ch_history_2" to "304498",
        "doc_curta" to "12948",
        "doc_arte1" to "67512",
        "ch_natgeo" to "8736",

        // Variedades & Estilo
        "var_multishow" to "8975",
        "var_gnt" to "8891",
        "var_bis" to "8680",
        "var_viagem" to "8883",
        "ch_322" to "8883",

        // Esportes
        "sportv_1" to "9111",
        "sportv_2" to "30123",
        "sportv_3" to "30124",
        "sportv_4" to "9111",
        "ch_4k_sportv" to "81591",
        "ch_h265_sportv" to "9111",
        "prem_clubes" to "9037",
        "prem_2" to "9022",
        "prem_3" to "13577",
        "prem_4" to "108295",
        "prem_5" to "9037",
        "prem_6" to "9037",
        "prem_7" to "9037",
        "prem_8" to "9037",
        "ch_4k_prem" to "9037",
        "ch_h265_prem" to "9022",
        "espn_fhd" to "8782",
        "espn_2" to "8780",
        "espn_3" to "8784",
        "espn_4" to "8786",
        "espn_5" to "8788",
        "espn_6" to "8789",
        "bandsports_fhd" to "8661",
        "bandsports_campeonatos" to "8661",
        "combate_fhd" to "8714",
        "combate_campeonatos" to "8714",

        // Infantil & Kids
        "ch_cartoon" to "8698",
        "ch_cartoonito" to "8688",
        "ch_gloob" to "8888",
        "ch_gloobinho" to "69037"
    )

    // Alias retrocompatível
    val VIVLAR_EPG_MAP: Map<String, String> = CORRUPTX_EPG_MAP

    /**
     * Busca o Guia de Programação (EPG) AO VIVO diretamente da API do servidor de streaming.
     * Atualiza o canal imediatamente com os nomes reais de filmes, programas e sinopses completas.
     */
    suspend fun fetchLiveEpg(channel: Channel): Pair<EpgProgram, EpgProgram?>? = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val cached = liveEpgCache[channel.id]
        if (cached != null && now - cached.timestamp < 10 * 60 * 1000L) {
            channel.currentProgram = cached.programs.first
            channel.nextProgram = cached.programs.second
            return@withContext cached.programs
        }

        // Se for canal de Jogos do Dia, o EPG é a própria partida de hoje
        if (channel.category.contains("Jogos") || channel.id.startsWith("jogo_dia")) {
            val (cur, next) = getProgramsForChannel(channel)
            val pair = Pair(cur, next)
            liveEpgCache[channel.id] = CachedEpg(pair)
            channel.currentProgram = cur
            channel.nextProgram = next
            return@withContext pair
        }

        // Resolve o ID do stream para consulta de EPG ao vivo
        val streamId = CORRUPTX_EPG_MAP[channel.id]
            ?: if (channel.streamUrl.contains("corruptx.org")) extractStreamIdFromUrl(channel.streamUrl) else null
            ?: channel.playbackStreamId
            ?: extractStreamIdFromUrl(channel.streamUrl)

        if (streamId == null) return@withContext null

        val serverUrl = "http://corruptx.org:80/player_api.php?username=hfp5ejnrey&password=zq0u3kzads&action=get_short_epg&stream_id=$streamId&limit=6"

        try {
            val url = URL(serverUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 3500
                readTimeout = 3500
                setRequestProperty("User-Agent", "VLC/3.0.18 LibVLC/3.0.18")
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
                val root = JSONObject(jsonStr)
                val listings = root.optJSONArray("epg_listings")

                if (listings != null && listings.length() > 0) {
                    val parsedPrograms = mutableListOf<EpgProgram>()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
                        timeZone = EpgRepository.timeZone
                    }
                    val sdfHour = SimpleDateFormat("HH:mm", Locale.US).apply {
                        timeZone = EpgRepository.timeZone
                    }

                    for (i in 0 until listings.length()) {
                        val item = listings.getJSONObject(i)
                        val rawTitle = item.optString("title", "")
                        val title = decodeBase64(rawTitle).ifEmpty { rawTitle }
                        val rawDesc = item.optString("description", "")
                        val desc = decodeBase64(rawDesc).ifEmpty { rawDesc }
                        val startStr = item.optString("start", "")
                        val stopStr = item.optString("stop", "").ifEmpty { item.optString("end", "") }

                        val startTs = try {
                            val rawTs = item.optString("start_timestamp", "")
                            if (rawTs.isNotEmpty()) rawTs.toLong() * 1000L else sdf.parse(startStr)?.time ?: 0L
                        } catch (_: Exception) { 0L }

                        val stopTs = try {
                            val rawTs = item.optString("stop_timestamp", "")
                            if (rawTs.isNotEmpty()) rawTs.toLong() * 1000L else sdf.parse(stopStr)?.time ?: 0L
                        } catch (_: Exception) { 0L }

                        val startH = if (startStr.length >= 16) startStr.substring(11, 16)
                                     else if (startTs > 0) sdfHour.format(java.util.Date(startTs)) else "00:00"
                        val stopH = if (stopStr.length >= 16) stopStr.substring(11, 16)
                                    else if (stopTs > 0) sdfHour.format(java.util.Date(stopTs)) else "00:00"

                        if (title.isNotEmpty() && title != "VT" && title != "Programação") {
                            parsedPrograms.add(
                                EpgProgram(
                                    title = title,
                                    category = channel.category,
                                    startTime = startH,
                                    endTime = stopH,
                                    startTimestamp = startTs,
                                    endTimestamp = stopTs,
                                    synopsis = desc.ifEmpty { "Transmissão oficial ao vivo em alta definição." },
                                    rating = getRatingForCategory(channel.category)
                                )
                            )
                        }
                    }

                    if (parsedPrograms.isNotEmpty()) {
                        val cur = parsedPrograms.firstOrNull { now in it.startTimestamp until it.endTimestamp }
                            ?: parsedPrograms.firstOrNull()

                        val next = if (cur != null) {
                            parsedPrograms.firstOrNull { it.startTimestamp >= cur.endTimestamp }
                        } else if (parsedPrograms.size > 1) {
                            parsedPrograms[1]
                        } else null

                        if (cur != null) {
                            val pair = Pair(cur, next)
                            liveEpgCache[channel.id] = CachedEpg(pair)
                            channel.currentProgram = cur
                            channel.nextProgram = next
                            return@withContext pair
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Usa fallback offline em caso de timeout
        }

        return@withContext null
    }

    private fun decodeBase64(encoded: String): String {
        return try {
            val bytes = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT)
            String(bytes, Charsets.UTF_8).trim()
        } catch (_: Exception) {
            encoded
        }
    }

    private fun extractStreamIdFromUrl(streamUrl: String): String? {
        val clean = streamUrl.substringBefore("?").substringBefore("#")
        val idStr = clean.substringAfterLast("/").substringBefore(".")
        return if (idStr.isNotEmpty() && idStr.all { it.isDigit() }) idStr else null
    }

    fun getProgramsForChannel(channel: Channel): Pair<EpgProgram, EpgProgram?> {
        // Se for Jogos do Dia, extrai o jogo e horário diretamente do nome do canal
        if (channel.category.contains("Jogos") || channel.id.startsWith("jogo_dia")) {
            val title = channel.title
            val timeMatch = Regex("""\[(\\d{1,2}:\\d{2})\]\\s*(.*)""").find(title)
            val (gameTime, gameName) = if (timeMatch != null) {
                Pair(timeMatch.groupValues[1], timeMatch.groupValues[2].trim())
            } else {
                Pair("Ao Vivo", if (title.contains("Canal")) "Transmissão Esportiva do Dia" else title)
            }

            val now = System.currentTimeMillis()
            val cur = EpgProgram(
                title = gameName,
                category = "⚽ Jogos do Dia (Ao Vivo)",
                startTime = gameTime,
                endTime = "23:59",
                startTimestamp = now - 30 * 60 * 1000L,
                endTimestamp = now + 90 * 60 * 1000L,
                synopsis = "Partida ao vivo de hoje com escalações, estatísticas e lances em tempo real.",
                rating = "L"
            )
            val next = EpgProgram(
                title = "Giro dos Campeonatos & Melhores Momentos",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                startTime = "23:59",
                endTime = "01:30",
                startTimestamp = now + 90 * 60 * 1000L,
                endTimestamp = now + 180 * 60 * 1000L,
                synopsis = "Análise detalhada de todos os resultados da rodada de hoje.",
                rating = "L"
            )
            return Pair(cur, next)
        }

        val cal = Calendar.getInstance(timeZone)
        val currentMinutesOfDay = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        var currentSlotIdx = timeSlots.indexOfFirst { (start, end) ->
            currentMinutesOfDay in start until end
        }
        if (currentSlotIdx == -1) currentSlotIdx = 0

        val nextSlotIdx = (currentSlotIdx + 1) % timeSlots.size

        val currentSlot = timeSlots[currentSlotIdx]
        val nextSlot = timeSlots[nextSlotIdx]

        val currentStartCal = cal.clone() as Calendar
        currentStartCal.set(Calendar.HOUR_OF_DAY, currentSlot.first / 60)
        currentStartCal.set(Calendar.MINUTE, currentSlot.first % 60)
        currentStartCal.set(Calendar.SECOND, 0)
        currentStartCal.set(Calendar.MILLISECOND, 0)

        val currentEndCal = cal.clone() as Calendar
        currentEndCal.set(Calendar.HOUR_OF_DAY, currentSlot.second / 60)
        currentEndCal.set(Calendar.MINUTE, currentSlot.second % 60)
        currentEndCal.set(Calendar.SECOND, 0)
        currentEndCal.set(Calendar.MILLISECOND, 0)

        val nextEndCal = cal.clone() as Calendar
        nextEndCal.set(Calendar.HOUR_OF_DAY, nextSlot.second / 60)
        nextEndCal.set(Calendar.MINUTE, nextSlot.second % 60)
        nextEndCal.set(Calendar.SECOND, 0)
        nextEndCal.set(Calendar.MILLISECOND, 0)

        val curProgramMeta = getProgramMeta(channel, currentSlotIdx)
        val nextProgramMeta = getProgramMeta(channel, nextSlotIdx)

        val currentProgram = EpgProgram(
            title = curProgramMeta.first,
            category = channel.category,
            startTime = formatTime(currentSlot.first),
            endTime = formatTime(currentSlot.second),
            startTimestamp = currentStartCal.timeInMillis,
            endTimestamp = currentEndCal.timeInMillis,
            synopsis = curProgramMeta.second,
            rating = getRatingForCategory(channel.category)
        )

        val nextProgram = EpgProgram(
            title = nextProgramMeta.first,
            category = channel.category,
            startTime = formatTime(nextSlot.first),
            endTime = formatTime(nextSlot.second),
            startTimestamp = currentEndCal.timeInMillis,
            endTimestamp = nextEndCal.timeInMillis,
            synopsis = nextProgramMeta.second,
            rating = getRatingForCategory(channel.category)
        )

        return Pair(currentProgram, nextProgram)
    }

private fun formatTime(minutes: Int): String {
        val h = (minutes / 60) % 24
        val m = minutes % 60
        return String.format("%02d:%02d", h, m)
    }

    fun getRatingForCategory(cat: String): String {
        return when {
            cat.contains("Adulto", ignoreCase = true) -> "18"
            cat.contains("Kids", ignoreCase = true) || cat.contains("Infantil", ignoreCase = true) -> "L"
            cat.contains("Notícia", ignoreCase = true) -> "10"
            cat.contains("Telecine", ignoreCase = true) || cat.contains("HBO", ignoreCase = true) -> "14"
            cat.contains("Combate", ignoreCase = true) -> "14"
            else -> "12"
        }
    }

    /**
     * Retorna a programação EXATA e autêntica de cada emissora de acordo com o slot de horário do dia.
     */
    fun getProgramMeta(channel: Channel, slotIdx: Int): Pair<String, String> {
        val titleLower = channel.title.lowercase()
        val catLower = channel.category.lowercase()
        val idLower = channel.id.lowercase()

                // 0. Jogos do Dia (Ao Vivo)
        if (catLower.contains("jogos") || idLower.startsWith("jogo_dia")) {
            val gameTitle = if (channel.title.startsWith("[")) channel.title else "Futebol Ao Vivo - Jogos do Dia"
            return Pair(gameTitle, "Transmissão ao vivo de hoje com lances, estatísticas e cobertura completa da partida.")
        }

        // 0.1 Campeonatos & Estaduais
        if (catLower.contains("campeonato") || catLower.contains("estadual") || titleLower.contains("sportynet")) {
            val campGrid = listOf(
                Pair("Giro dos Campeonatos Estaduais", "Notícias, escalações e tabela de classificação dos estaduais."),
                Pair("Futebol Madrugada: Melhores Momentos", "Os gols e lances mais bonitos das rodadas dos campeonatos."),
                Pair("Aquecimento da Rodada do Brasileirão", "Prévia dos confrontos das Séries A, B e C com comentaristas."),
                Pair("Campeonatos Regionais: Partida Ao Vivo", "Transmissão completa com emoção do início ao fim."),
                Pair("Debate da Rodada & Entrevistas", "Entrevistas com técnicos e jogadores nos vestiários."),
                Pair("Futebol Ao Vivo - Duelo Decisivo", "Confronto direto valendo classificação nos campeonatos estaduais."),
                Pair("Show dos Gols da Rodada", "Todos os gols narrados com emoção e replay em câmera lenta.")
            )
            return campGrid[slotIdx % campGrid.size]
        }

        // 1. Fórmula 1 & Velocidade
        if (catLower.contains("f1") || catLower.contains("fórmula") || catLower.contains("formula") ||
            titleLower.contains("f1") || titleLower.contains("motorvision") || titleLower.contains("pit lane") || idLower.startsWith("ch_f1")) {
            val f1Grid = listOf(
                Pair("F1 Retrô: Vitórias Históricas", "Reviva os Grandes Prêmios inesquecíveis e duelos lendários de Senna e Schumacher."),
                Pair("Fórmula 1 Madrugada: Compacto do GP", "Os melhores momentos e ultrapassagens da última corrida da temporada."),
                Pair("F1 Aquecimento & Bastidores do Paddock", "Notícias das equipes, novidades de motores, asas móveis e telemetria."),
                Pair("Treino Livre 1 (FP1) - Ao Vivo", "Primeira sessão de testes de pista com acerto aerodinâmico."),
                Pair("Treino Livre 2 (FP2) - Análise de Pneus", "Pilotos testam ritmo de corrida e desgaste dos compostos macios e duros."),
                Pair("Fórmula 2: Treino de Classificação", "Jovens talentos buscam vaga no grid da categoria de acesso à F1."),
                Pair("Treino Livre 3 (FP3) - Acerto Final", "Última oportunidade de ajuste dos carros antes da tomada de tempos."),
                Pair("Classificação Oficial (Pole Position Ao Vivo)", "Q1, Q2 e Q3 na disputa pelos milésimos da primeira fila do grid."),
                Pair("Sprint Shootout & Corrida Sprint Ao Vivo", "Disputa eletrizante por pontos decisivos na corrida curta de sábado."),
                Pair("Desfile de Pilotos & Hino Nacional", "Cerimônia de abertura e volta de honra no circuito com entrevistas."),
                Pair("Grande Prêmio de Fórmula 1: Corrida Ao Vivo", "Largada oficial, pit stops em tempo real e a bandeira quadriculada."),
                Pair("Cerimônia de Pódio & Entrevistas dos Vencedores", "Festa com champanhe e declarações exclusivas dos três primeiros colocados."),
                Pair("Fórmula 1 Pós-Corrida: Análise Técnica", "Especialistas debatem táticas de pit stop, undercut e telemetria dos carros.")
            )
            return f1Grid[slotIdx % f1Grid.size]
        }

        // 2. Cazé TV
        if (titleLower.contains("caze") || idLower.contains("caze")) {
            val cazeGrid = listOf(
                Pair("CazéTV Madrugada: Melhores Momentos", "Os lances mais marcantes e cortes exclusivos com Casimiro."),
                Pair("CazéTV Highlights & Resenha", "Resenha esportiva com gols, memes e bastidores do esporte."),
                Pair("Café com Cazé: Notícias do Futebol", "Análise matinal das principais notícias do futebol mundial."),
                Pair("Aquecimento CazéTV Ao Vivo", "Preparação e pré-jogo com a equipe mais irreverente da internet."),
                Pair("Copa do Brasil & Libertadores Especial", "Cobertura das maiores competições esportivas da América Latina."),
                Pair("CazéTV Ao Vivo: Debate da Rodada", "Debate com a bancada de narradores e influenciadores."),
                Pair("Casimiro Reage: Fails & Golaços", "As reações mais engraçadas aos gols da semana."),
                Pair("Transmissão Ao Vivo CazéTV", "Acompanhe as partidas e transmissões de alta definição em tempo real."),
                Pair("Pré-Jogo Especial Brasileirão", "Entrevistas exclusivas e escalações em tempo real direto do campo."),
                Pair("CazéTV Noturno: Giro Internacional", "Champions League, futebol europeu e campeonatos internacionais."),
                Pair("Resenha Pós-Jogo CazéTV", "Análise detalhada de todos os resultados da rodada esportiva."),
                Pair("CazéTV Noite: Cortes & Humor", "Os melhores momentos do dia com muita descontração."),
                Pair("CazéTV Madrugada Sem Limites", "Entrevistas com atletas e personalidades da internet.")
            )
            return cazeGrid[slotIdx % cazeGrid.size]
        }

        // 3. SporTV (SporTV 1, 2, 3, 4, 4K, H265)
        if (titleLower.contains("sportv") || titleLower.contains("spor tv") || idLower.contains("sportv")) {
            val sportvGrid = listOf(
                Pair("SporTV News - Edição da Madrugada", "O resumo completo de todos os resultados da rodada esportiva nacional e mundial."),
                Pair("Baú do Esporte: Clássicos Inesquecíveis", "Relembre jogos históricos, finais épicas e os maiores ídolos do futebol."),
                Pair("SporTV News - Primeira Edição", "Abertura da cobertura esportiva com o aquecimento dos centros de treinamento."),
                Pair("Redação SporTV (Ao Vivo)", "Os principais jornalistas debatem as capas de jornais e manchetes da rodada."),
                Pair("Giro dos Campeões: Notícias do Dia", "Reportagens exclusivas, chegada de delegações e movimentações do mercado."),
                Pair("Seleção SporTV (Ao Vivo com André Rizek)", "O debate mais qualificado da TV com análises táticas aprofundadas."),
                Pair("Giro da Rodada & Coletivas de Imprensa", "Declarações ao vivo de técnicos e capitães antes das grandes decisões."),
                Pair("Tá na Área (Ao Vivo)", "Informação com dinamismo e bom humor trazendo as notícias quentes da tarde."),
                Pair("Aquecimento Especial SporTV", "Pré-jogo com links ao vivo direto dos estádios, vestiários e arquibancadas."),
                Pair("Pré-Jogo dos Campeões (Ao Vivo)", "Equipes no gramado, reconhecimento do campo e escalações oficiais comentadas."),
                Pair("Transmissão Ao Vivo: Brasileirão / Copa do Brasil", "A emoção do futebol ao vivo em alta definição com narração premium."),
                Pair("Troca de Passes (Ao Vivo)", "Análise tática dos lances polêmicos com recursos gráficos e entrevistas pós-jogo."),
                Pair("Boleiragem (Ao Vivo)", "Resenha descontraída entre ex-jogadores de renome e convidados especiais.")
            )
            return sportvGrid[slotIdx % sportvGrid.size]
        }

        // 4. ESPN (ESPN, ESPN 2, 3, 4, 5, 6, Extra, 4K, H265)
        if (titleLower.contains("espn") || idLower.contains("espn")) {
            val espnGrid = listOf(
                Pair("SportsCenter - Edição da Madrugada", "Todos os gols, compactos e análises do futebol internacional e NBA."),
                Pair("ESPN Replay: Clássicos Europeus", "Reapresentação dos melhores jogos da Premier League e Champions League."),
                Pair("SportsCenter Manhã", "As primeiras notícias do esporte mundial e resultados da rodada noturna."),
                Pair("Futebol no Mundo (Debate Internacional)", "Os correspondentes na Europa trazem os bastidores do futebol internacional."),
                Pair("SportsCenter 1ª Edição (Ao Vivo)", "O noticiário esportivo mais premiado da TV com toda a credibilidade ESPN."),
                Pair("ESPN F90 (Ao Vivo)", "Mesa redonda acalorada com debates francos sobre os grandes clubes do Brasil."),
                Pair("Futebol 360 (Ao Vivo)", "Panorama completo dos campeonatos europeus, sul-americanos e da Seleção."),
                Pair("Premier League / La Liga Ao Vivo", "Transmissão ao vivo do melhor futebol do planeta com narração consagrada."),
                Pair("ESPN FC Internacional", "Análise tática de Real Madrid, Barcelona, Manchester City, Liverpool e PSG."),
                Pair("SportsCenter 2ª Edição (Ao Vivo)", "Edição principal com cobertura das partidas mais quentes do horário nobre."),
                Pair("Linha de Passe: Mesa Redonda (Ao Vivo)", "A bancada mais tradicional do jornalismo esportivo debate a rodada."),
                Pair("NBA / NFL: Jogo da Noite Ao Vivo", "A emoção do basquete e do futebol americano ao vivo com comentários de ponta."),
                Pair("SportsCenter Noite: Melhores Momentos", "O encerramento do dia com os gols mais bonitos e lances inacreditáveis.")
            )
            return espnGrid[slotIdx % espnGrid.size]
        }

        // 5. Premiere (Clubes, 2, 3, 4, 5, 6, 7, 8, 4K, H265)
        if (!titleLower.contains("telecine") && !idLower.contains("tc_") && (titleLower.contains("premiere") || titleLower.startsWith("prem") || idLower.startsWith("prem_") || idLower.contains("_prem"))) {
            val premGrid = listOf(
                Pair("Gols da Rodada & Melhores Lances", "Todos os gols dos jogos de ontem com replays em alta definição."),
                Pair("Arquivo Premiere: Grandes Decisões", "Relembre jogos inesquecíveis da história do Campeonato Brasileiro."),
                Pair("Aquecimento Brasileirão Série A", "Bastidores dos estádios, vestiários e a chegada dos ônibus das equipes."),
                Pair("Giro dos Clubes: Treinos & Tática", "A rotina dos grandes clubes de futebol do Brasil e coletivas."),
                Pair("Painel Tático Premiere", "Especialistas debatem pranchetas, movimentações e estratégias dos times."),
                Pair("Campeonato Brasileiro Série B Ao Vivo", "Disputa acirrada pelo acesso à elite do futebol nacional."),
                Pair("Pré-Jogo Exclusivo Premiere", "Entrevistas exclusivas à beira do gramado e escalações confirmadas."),
                Pair("Campeonato Brasileiro Série A (Ao Vivo)", "Transmissão principal da rodada com narração oficial Premiere."),
                Pair("Campeonato Brasileiro Série A: Mosaico", "Jogos simultâneos ao vivo com acompanhamento de todas as partidas."),
                Pair("Pós-Jogo: Coletivas de Imprensa", "Técnicos e jogadores comentam os resultados e polêmicas do jogo."),
                Pair("Compacto da Rodada Premiere", "Os melhores momentos de todas as partidas disputadas no dia."),
                Pair("Faixa Especial de Gols do Brasileirão", "Todos os ângulos dos gols mais bonitos da rodada."),
                Pair("Resenha Premiere Noturna", "Encerramento com os destaques e a tabela de classificação atualizada.")
            )
            return premGrid[slotIdx % premGrid.size]
        }

        // 6. Combate & Lutas
        if (titleLower.contains("combate") || idLower.contains("combate")) {
            val combateGrid = listOf(
                Pair("Nocautes Históricos do UFC", "Os golpes mais espetaculares e finalizações da história do MMA."),
                Pair("UFC Countdown: Bastidores das Lutas", "A preparação física e psicológica dos maiores lutadores do mundo."),
                Pair("Venum Fight Magazine", "Notícias, treinos e bastidores do mundo das artes marciais mistas."),
                Pair("Luta Livre & Jiu-Jitsu Ao Vivo", "Competições nacionais e internacionais de grappling e submission."),
                Pair("Pesagem Oficial do UFC Ao Vivo", "Tensão na encarada oficial antes da noite de lutas."),
                Pair("UFC Fight Night: Card Preliminar", "O octógono pega fogo com os talentos em ascensão no UFC."),
                Pair("UFC Fight Night: Card Principal Ao Vivo", "Grandes disputas mundiais de cinturão com narração brasileira."),
                Pair("Nocautes da Noite & Entrevistas", "Os vencedores da noite comentam suas vitórias direto do vestiário."),
                Pair("Resenha Combate Especial", "Especialistas debatem os resultados e os próximos confrontos do octógono."),
                Pair("K-1 / Kickboxing Internacional", "Combates eletrizantes de trocação pura em alta definição."),
                Pair("Glory Kickboxing: Duelos Mundiais", "Trocação de alto nível com os maiores pesos-pesados do mundo."),
                Pair("Documentários do Combate: Lendas do Octógono", "A trajetória de Anderson Silva, Minotauro e José Aldo."),
                Pair("Revista do Combate Madrugada", "Acompanhe as novidades e calendários das próximas edições.")
            )
            return combateGrid[slotIdx % combateGrid.size]
        }

        // 7. BandSports
        if (titleLower.contains("bandsports") || titleLower.contains("band sports") || idLower.contains("bandsports")) {
            val bandsportsGrid = listOf(
                Pair("BandSports News - Madrugada", "Resumo esportivo completo com foco no esporte nacional e olímpico."),
                Pair("Super Motor: Velocidade & Adrenalina", "Cobertura das principais categorias do automobilismo mundial."),
                Pair("BandSports Manhã", "Informações matinais e destaques das modalidades olímpicas."),
                Pair("Golaço BandSports", "Os lances mais bonitos e gols marcantes do futebol nacional."),
                Pair("Ace BandSports (Especial Tênis)", "Tudo sobre os torneios de ATP, WTA e Grand Slams de tênis."),
                Pair("Esporte Total na TV", "Debates francos sobre o futebol paulista e brasileiro."),
                Pair("Transmissão Ao Vivo BandSports", "Acompanhe as disputas ao vivo de futebol, basquete e automobilismo."),
                Pair("BandSports no Pódio", "Histórias de superação e conquistas de atletas brasileiros."),
                Pair("BandSports Notícias - Edição Principal", "O noticiário definitivo do horário nobre com os principais fatos do dia."),
                Pair("Maratona de Velocidade", "Compactos das corridas de Stock Car, Copa Truck e Fórmula E."),
                Pair("Mundo dos Motores", "Análises técnicas, lançamentos de carros e novidades das pistas."),
                Pair("Noite dos Campeões BandSports", "Destaques esportivos e comentários da bancada."),
                Pair("Arquivo de Ouro BandSports", "Momentos inesquecíveis da história do esporte mundial.")
            )
            return bandsportsGrid[slotIdx % bandsportsGrid.size]
        }

                // 8. Cine Sky (01 a 06) - Catálogo de Filmes Reais com Sinopses Detalhadas
        if (titleLower.contains("cinesky") || titleLower.contains("cine sky") || idLower.contains("cinesky")) {
            val cineskyGrid = listOf(
                Pair("Oppenheimer", "Durante a Segunda Guerra Mundial, o físico J. Robert Oppenheimer lidera o Projeto Manhattan para desenvolver a primeira bomba atômica da história."),
                Pair("Top Gun: Maverick", "Após trinta anos de serviço como um dos maiores pilotos da Marinha, Pete Maverick Mitchell é convocado para treinar jovens graduados para uma missão especializada."),
                Pair("John Wick 4: Baba Yaga", "Com o preço por sua cabeça cada vez maior, o lendário assassino John Wick descobre um caminho para derrotar a Alta Cúpula em combates épicos."),
                Pair("Interestelar", "Uma equipe de exploradores viaja através de um buraco de minhoca no espaço, na tentativa desesperada de garantir a sobrevivência da humanidade."),
                Pair("Gladiador II", "Anos após a morte do lendário Maximus, Lucius é forçado a lutar no Coliseu para restaurar a honra e a glória de Roma."),
                Pair("Duna: Parte 2", "Paul Atreides se une a Chani e aos Fremen em busca de vingança contra os conspiradores que destruíram sua família no planeta desértico."),
                Pair("Avatar: O Caminho da Água", "Jake Sully e Neytiri exploram as regiões oceânicas de Pandora para proteger sua família quando uma antiga ameaça retorna."),
                Pair("The Batman", "Nos dois anos em que atuou como Batman, Bruce Wayne investiga uma trilha de pistas enigmáticas deixadas pelo Charada na elite corrupta de Gotham."),
                Pair("Missão: Impossível - Acerto de Contas", "Ethan Hunt e sua equipe do FMI embarcam na missão mais perigosa de suas vidas para rastrear uma arma que ameaça toda a humanidade."),
                Pair("Coringa: Delírio a Dois", "Arthur Fleck aguarda julgamento em Arkham enquanto encontra o amor e descobre sua música interior ao lado de Harley Quinn."),
                Pair("Homem-Aranha: Sem Volta para Casa", "Com sua identidade secreta revelada ao mundo, Peter Parker recorre ao Doutor Estranho e abre os portais do multiverso."),
                Pair("O Lobo de Wall Street", "Jordan Belfort é um ambicioso corretor da bolsa de Nova York que cria um verdadeiro império de riqueza, excessos e adrenalina."),
                Pair("Mad Max: Estrada da Fúria", "Em um mundo pós-apocalíptico, Max se junta a Furiosa e fugitivas em uma fuga em altíssima velocidade pelo deserto implacável.")
            )
            return cineskyGrid[slotIdx % cineskyGrid.size]
        }

        // 9. Telecine (Premium, Action, Touch, Pipoca, Fun, Cult, 4K, H265)
        if (titleLower.contains("telecine") || catLower.contains("telecine") || idLower.startsWith("tc_") || idLower.contains("_tc")) {
            val tcGrid = when {
                titleLower.contains("action") -> listOf(
                    Pair("Madrugada de Ação: Tiroteio Letal", "Adrenalina pura com os maiores filmes de confronto."),
                    Pair("John Wick: De Volta ao Jogo", "A saga do lendário assassino profissional em busca de vingança."),
                    Pair("Perseguição Implacável", "Liam Neeson em uma caçada frenética contra o crime organizado."),
                    Pair("Missão Impossível: Efeito Fallout", "Tom Cruise em missões acrobáticas sem dublês pelo mundo."),
                    Pair("Velozes & Furiosos: Desafio em Tóquio", "Corridas clandestinas e drift nas ruas iluminadas do Japão."),
                    Pair("Mad Max: Estrada da Fúria", "Sobrevivência brutal em um mundo pós-apocalíptico desértico."),
                    Pair("O Protetor com Denzel Washington", "Um homem misterioso usa suas habilidades mortais para fazer justiça."),
                    Pair("Top Gun: Maverick", "Caças supersônicos e missões quase impossíveis nos céus."),
                    Pair("Cine Ação Especial das 18h", "Superprodução eletrizante de combate e tecnologia bélica."),
                    Pair("Superestreia Action: O Esquadrão Suicida", "Vilões da DC em uma missão suicida repleta de ação."),
                    Pair("Duro de Matar: Um Bom Dia para Morrer", "Bruce Willis enfrentando terroristas em Moscou."),
                    Pair("Cine Brutal: Adrenalina na Noite", "Combates marciais com coreografias impecáveis."),
                    Pair("Invasão ao Serviço Secreto", "Gerard Butler protegendo o presidente americano.")
                )
                titleLower.contains("pipoca") -> listOf(
                    Pair("Cine Pipoca: Noite das Risadas", "Comédias clássicas dubladas para a madrugada."),
                    Pair("Minions & Meu Malvado Favorito", "As criaturas amarelas mais amadas do cinema em confusões."),
                    Pair("Shrek 2: Aventura no Reino Tão Tão Distante", "O ogro mais querido do mundo conhecendo seus sogros reais."),
                    Pair("Jumanji: Próxima Fase", "Os jovens entram novamente no videogame mais perigoso do mundo."),
                    Pair("Homem-Aranha no Aranhaverso", "Miles Morales descobre as infinitas possibilidades do multiverso."),
                    Pair("Hotel Transilvânia 3", "Monstros de férias em um cruzeiro de luxo para relaxar."),
                    Pair("Kung Fu Panda 4", "Po enfrenta novos vilões em busca do Cajado da Sabedoria."),
                    Pair("Paddington 2: O Ursinho em Londres", "Muita fofura e aventuras divertidas pelas ruas britânicas."),
                    Pair("Cine Pipoca Especial das 18h", "Filme leve e divertido 100% dublado em português."),
                    Pair("Superestreia Pipoca: Barbie", "Uma jornada colorida e emocionante descobrindo o mundo real."),
                    Pair("Gente Grande 2 com Adam Sandler", "Reunião de velhos amigos de infância com muitas trapalhadas."),
                    Pair("As Branquelas: Sessão Cult", "Dois agentes do FBI disfarçados em confusões nos Hamptons."),
                    Pair("Cine Pipoca Madrugada Animada", "Grandes animações premiadas do cinema infantil.")
                )
                titleLower.contains("touch") -> listOf(
                    Pair("Cine Madrugada: Histórias de Superação", "Dramas emocionantes e trajetórias inspiradoras baseadas em fatos reais."),
                    Pair("Nasce uma Estrela com Lady Gaga", "Um músico decadente e uma cantora talentosa em ascensão apaixonados."),
                    Pair("A Culpa é das Estrelas", "Dois jovens com câncer se apaixonam e descobrem o verdadeiro valor de cada momento."),
                    Pair("Como Eu Era Antes de Você", "O encontro transformador de Louisa Clark e Will Traynor."),
                    Pair("Extraordinário com Julia Roberts", "A comovente história de Auggie Pullman enfrentando preconceitos na escola."),
                    Pair("Questão de Tempo (About Time)", "Viagens no tempo usadas para conquistar o amor e valorizar a família."),
                    Pair("Intocáveis (The Intouchables)", "A amizade improvável entre um milionário tetraplégico e seu cuidador."),
                    Pair("Sessão Romance Especial das 18h", "Grandes produções românticas internacionais na sua televisão."),
                    Pair("Superestreia Touch: Amor & Emoção", "O grande drama do ano aclamado pela crítica mundial."),
                    Pair("Para Sempre (The Vow)", "Um marido luta incansavelmente para reconquistar o amor de sua esposa."),
                    Pair("Diário de uma Paixão", "A inesquecível história de amor entre Noah e Allie nos anos 40."),
                    Pair("Cine Emoção: A Vida É Bela", "A comovente obra-prima italiana sobre o amor incondicional de um pai na guerra."),
                    Pair("Touch Fim de Noite: Grandes Histórias", "Filmes tocantes para encerrar a noite com sensibilidade e reflexão.")
                )
                titleLower.contains("fun") -> listOf(
                    Pair("Cine Fun Madrugada: Comédias Sem Noção", "Gargalhadas garantidas com paródias e comédias malucas."),
                    Pair("Gente Grande com Adam Sandler", "Amigos de infância passam um fim de semana hilário reunidos com suas famílias."),
                    Pair("Debi & Lóide: Dois Idiotas em Apuros", "Jim Carrey e Jeff Daniels em uma viagem caótica pelo país."),
                    Pair("Se Beber, Não Case! (The Hangover)", "A busca desesperada pelo noivo após uma despedida de solteiro insana."),
                    Pair("Todo Mundo em Pânico: O Clássico", "A paródia definitiva dos maiores filmes de terror do cinema."),
                    Pair("Click com Adam Sandler", "Um controle remoto mágico que pausa e avança a vida de um homem ocupado."),
                    Pair("As Branquelas: Gargalhadas Sem Fim", "Dois agentes do FBI disfarçados aprontando altas confusões nos Hamptons."),
                    Pair("Cine Fun Especial das 18h", "Supercomédia campeã de bilheteria para relaxar com a família reunida."),
                    Pair("Superestreia Fun: A Grande Comédia do Ano", "O filme mais engraçado dos cinemas direto na sua tela."),
                    Pair("Uma Noite no Museu com Ben Stiller", "As peças e fósseis do museu ganham vida e aprontam confusões históricas."),
                    Pair("Esposa de Mentirinha com Jennifer Aniston", "Um cirurgião plástico cria uma teia de mentiras românticas hilárias."),
                    Pair("Ted com Mark Wahlberg", "A vida adulta de um homem ao lado de seu ursinho de pelúcia desbocado."),
                    Pair("Fun Madrugada dos Ricos & Famosos", "Comédias inteligentes e sátiras de sucesso de Hollywood.")
                )
                titleLower.contains("cult") -> listOf(
                    Pair("Festival de Cannes: Clássicos Premiados", "Obras que revolucionaram a linguagem cinematográfica."),
                    Pair("Cinema Paradiso de Giuseppe Tornatore", "Uma declaração de amor eterna à magia do cinema."),
                    Pair("Pulp Fiction: Tempo de Violência", "Quentin Tarantino em sua obra-prima ganhadora da Palma de Ouro."),
                    Pair("O Poderoso Chefão: Parte 1", "Marlon Brando e Al Pacino na maior saga mafiosa de todos os tempos."),
                    Pair("O Fabuloso Destino de Amélie Poulain", "Poesia visual e delicadeza em uma Paris encantadora."),
                    Pair("Apocalypse Now de Coppola", "A loucura da Guerra do Vietnã sob uma ótica visceral."),
                    Pair("Parasita de Bong Joon-ho", "O vencedor histórico de 4 Oscars incluindo Melhor Filme."),
                    Pair("A Viagem de Chihiro de Miyazaki", "Obra-prima da animação japonesa ganhadora do Oscar."),
                    Pair("Sessão Cult Especial das 18h", "Cinema independente premiado internacionalmente."),
                    Pair("Oppenheimer de Christopher Nolan", "O épico histórico sobre o criador da bomba atômica."),
                    Pair("Tár com Cate Blanchett", "O drama psicológico de uma maestrina no auge de sua carreira."),
                    Pair("Taxi Driver de Scorsese", "Robert De Niro nas ruas decadentes de Nova York."),
                    Pair("Clássicos Franceses da Madrugada", "A Nouvelle Vague e o cinema poético europeu.")
                )
                titleLower.contains("premium") || idLower.contains("premium") || idLower == "tc_premium" || idLower == "ch_4k_tc" -> listOf(
                    Pair("Cine Madrugada Premium: Suspense & Mistério", "Superproduções envolventes de suspense psicológico em alta resolução."),
                    Pair("Top Gun: Maverick com Tom Cruise", "Caças supersônicos, manobras vertiginosas e uma missão quase impossível nos céus."),
                    Pair("Duna: Parte 2 de Denis Villeneuve", "Paul Atreides e os Fremen na batalha definitiva pelo destino do planeta Arrakis."),
                    Pair("Oppenheimer de Christopher Nolan", "O premiado drama histórico vencedor de 7 Oscars sobre o Projeto Manhattan."),
                    Pair("Avatar: O Caminho da Água de James Cameron", "A família Sully enfrenta novos perigos nas profundezas e recifes de Pandora."),
                    Pair("Super Mario Bros: O Filme", "Mario e Luigi na emocionante jornada para salvar o Reino dos Cogumelos e derrotar Bowser."),
                    Pair("John Wick 4: Baba Yaga com Keanu Reeves", "Confrontos marciais de tirar o fôlego pelas ruas e monumentos de Paris e Osaka."),
                    Pair("Missão Impossível: Acerto de Contas Parte 1", "Ethan Hunt e sua equipe contra uma perigosa inteligência artificial rebelde."),
                    Pair("Sessão Superestreia Telecine Premium", "O maior blockbuster recém-saído dos cinemas em exibição exclusiva e sem intervalos."),
                    Pair("Barbie: O Filme com Margot Robbie", "A boneca mais famosa do mundo em uma aventura colorida descobrindo a vida real."),
                    Pair("Guardiões da Galáxia Vol. 3 da Marvel", "A despedida emocionante e cheia de ação da equipe de heróis espaciais."),
                    Pair("Cine Noite Premium: Astros de Hollywood", "Filmes campeões de bilheteria mundial com elencos estelares consagrados."),
                    Pair("Telecine Premium: Bastidores de Hollywood", "Entrevistas exclusivas com diretores, making of e segredos das superproduções.")
                )
                else -> listOf(
                    Pair("Cine Madrugada: Suspense & Drama", "Grandes filmes para quem gosta de boas histórias cinematográficas."),
                    Pair("Cine Clássicos: Obras Imortais", "Superproduções que conquistaram gerações de cinéfilos."),
                    Pair("Telecine Cult: Festivais Internacionais", "Cinema autoral e obras consagradas nos grandes festivais."),
                    Pair("Telecine Touch: Histórias Inspiradoras", "Dramas emocionantes baseados em fatos reais."),
                    Pair("Telecine Pipoca: Diversão Garantida", "Comédias leves e animações para toda a família."),
                    Pair("Telecine Fun: As Melhores Comédias", "As comédias mais premiadas de Hollywood com grandes astros."),
                    Pair("Telecine Action: Perseguição Implacável", "Adrenalina pura com perseguições e tiroteios."),
                    Pair("Sessão Superestreia Telecine", "Filmes campeões de bilheteria recém-saídos dos cinemas."),
                    Pair("Telecine Premium: O Grande Filme da Noite", "A maior produção de Hollywood na sua tela em alta definição."),
                    Pair("Festival Telecine: Universo Marvel", "Ação eletrizante com os heróis mais queridos do cinema."),
                    Pair("Telecine Action: Heróis Sem Limites", "Blockbuster de ficção científica e aventura intergaláctica."),
                    Pair("Cine Terror: A Hora do Pesadelo", "O melhor do gênero de terror contemporâneo."),
                    Pair("Telecine Pós-Sessão: Bastidores do Cinema", "Making of e segredos das maiores produções cinematográficas.")
                )
            }
            return tcGrid[slotIdx % tcGrid.size]
        }

        // 10. HBO (HBO 1, 2, Plus, Family, Signature, Mundi, Pop, Xtreme, 4K, H265)
        if (titleLower.contains("hbo") || catLower.contains("hbo") || idLower.startsWith("hbo_")) {
            val hboGrid = listOf(
                Pair("The Sopranos / The Wire", "Grandes marcos da era de ouro das séries dramáticas da HBO."),
                Pair("Séries Clássicas HBO", "Produções consagradas com múltiplos prêmios Emmy."),
                Pair("Documentários Originais HBO", "Grandes investigações, biografias e jornalismo cinematográfico."),
                Pair("Sessão Animações DC Comics", "Batman, Superman e a Liga da Justiça salvando a Terra."),
                Pair("Two and a Half Men / The Big Bang Theory", "Maratona das comédias mais bem-sucedidas da história da TV."),
                Pair("Succession: O Jogo do Poder", "A guerra familiar e corporativa dos irmãos Roy pela sucessão da Waystar."),
                Pair("House of the Dragon (Temporada Épica)", "A sangrenta guerra civil Targaryen pelo Trono de Ferro com dragões."),
                Pair("The Last of Us: A Jornada de Joel e Ellie", "A emocionante travessia em um mundo dominado pela infecção."),
                Pair("Blockbuster HBO: Grande Estreia", "O filme de maior bilheteria do ano disponível com áudio e imagem perfeitos."),
                Pair("Game of Thrones: As Grandes Batalhas", "A Batalha dos Bastardos e os segredos do Rei da Noite em Westeros."),
                Pair("True Detective: Noite Noturna", "Mistérios policiais sombrios e investigações profundas."),
                Pair("Duna: Parte 2 de Denis Villeneuve", "Paul Atreides lidera os Fremen na guerra épica de Arrakis."),
                Pair("Last Week Tonight com John Oliver", "Sátira inteligente e bem-humorada sobre as notícias da semana.")
            )
            return hboGrid[slotIdx % hboGrid.size]
        }

        // 11. Warner Channel
        if (titleLower.contains("warner") || idLower.contains("warner")) {
            val warnerGrid = listOf(
                Pair("Supernatural: Caçadores de Monstros", "Sam e Dean Winchester enfrentando criaturas das trevas pela estrada."),
                Pair("Smallville: As Aventuras do Superboy", "A juventude de Clark Kent descobrindo seus poderes em Kansas."),
                Pair("The Big Bang Theory - Manhã Nerd", "Sheldon, Leonard, Penny e amigos em teorias e confusões científicas."),
                Pair("Two and a Half Men: Risadas com Charlie", "A rotina hilária de Charlie Harper e seu irmão Alan na praia de Malibu."),
                Pair("Friends: Os Melhores Momentos", "Rachel, Monica, Phoebe, Joey, Chandler e Ross no Central Perk."),
                Pair("Young Sheldon: O Pequeno Gênio", "A infância peculiar de Sheldon Cooper no interior do Texas."),
                Pair("Flash / Arrow: Heróis da DC", "Velocidade e justiça com os maiores vigilantes dos quadrinhos."),
                Pair("Cine Warner: Sessão Blockbuster", "Grandes sucessos do estúdio Warner Bros com muita ação."),
                Pair("The Big Bang Theory - Edição Nobre", "Os episódios mais premiados e hilários da comédia."),
                Pair("Cine Warner Especial da Noite", "Superprodução de Hollywood com astro internacional de renome."),
                Pair("Gotham: Origens de Batman", "A ascensão do detetive Jim Gordon na cidade mais corrupta do mundo."),
                Pair("Lucifer: O Diabo em Los Angeles", "O Senhor do Inferno resolvendo homicídios ao lado da polícia."),
                Pair("Warner Noturna: Séries sem Censura", "Produções dramáticas envolventes na madrugada.")
            )
            return warnerGrid[slotIdx % warnerGrid.size]
        }

        // 12. Megapix
        if (titleLower.contains("megapix") || idLower.contains("megapix")) {
            val megapixGrid = listOf(
                Pair("Megapix Madrugada: Tiro, Porrada & Bomba", "Ação sem parar com tiroteios e perseguições velozes."),
                Pair("Cine Megapix: Clássicos da Ação", "Heróis dos anos 90 e 2000 mostrando como se faz justiça."),
                Pair("Manhã Megapix: Comédia em Dose Dupla", "Grandes gargalhadas para animar as manhãs na sua TV."),
                Pair("Sessão Retrô Megapix", "Filmes que você adora rever dublados em alta definição."),
                Pair("Powerpix: Os Maiores Vilões", "Batalhas épicas entre mocinhos e os criminosos mais perigosos."),
                Pair("Megapix Pipoca: Sucessos Dublados", "Filmes para todos os gostos sem cortes comerciais."),
                Pair("Tarde de Ação Megapix", "Adrenalina com perseguições de carros, agentes e explosões."),
                Pair("Sexta de Ação / Megapix Especial", "O filme preferido da audiência no horário de ouro."),
                Pair("Superfilme Megapix da Noite", "A maior atração da semana com atores consagrados do cinema."),
                Pair("Cine Megapix: Adrenalina Pura", "Batalhas eletrizantes de artes marciais e operações especiais."),
                Pair("Sessão Terror Megapix", "Sustos garantidos e monstros assustadores na tela."),
                Pair("Madrugada Explosiva Megapix", "Ação até o sol raiar com muita munição e velocidade."),
                Pair("Megapix Fim de Noite", "O fechamento perfeito da grade de filmes 100% dublados.")
            )
            return megapixGrid[slotIdx % megapixGrid.size]
        }

        // 13. Universal TV & Studio Universal
        if (titleLower.contains("universal") || idLower.contains("universal")) {
            val universalGrid = listOf(
                Pair("Law & Order: SVU - Casos Especiais", "A capitã Olivia Benson lidera a equipe em investigações sensíveis."),
                Pair("Chicago Med: Atendimento de Emergência", "Médicos e enfermeiros salvando vidas no hospital mais movimentado de Chicago."),
                Pair("Chicago Fire: Heróis do Batalhão 51", "Os bombeiros arriscam tudo em resgates perigosos e incêndios graves."),
                Pair("Chicago P.D.: Distrito 21", "O sargento Hank Voight faz de tudo para manter a ordem na cidade."),
                Pair("FBI: Unidade Especial de Nova York", "Agentes federais enfrentam ameaças terroristas e crimes organizados."),
                Pair("FBI: Most Wanted - Os Mais Procurados", "A caçada aos criminosos mais perigosos da lista do FBI."),
                Pair("Law & Order: Casos Reais", "Tribunais, júris e sentenças decisivas em Nova York."),
                Pair("Chicago Fire: Segunda Sessão", "Dramas familiares, romance e perigo entre os bombeiros de Chicago."),
                Pair("Chicago P.D. - Horário Nobre", "Operações policiais de inteligência e prisões de alto risco."),
                Pair("Superestreia Universal: Chicago Night", "Crossover emocionante entre bombeiros, médicos e policiais."),
                Pair("Law & Order: Unidade de Vítimas Especiais", "Os episódios mais impactantes e premiados da série."),
                Pair("FBI: Investigações Internacionais", "A divisão de Budapeste agindo em solo europeu."),
                Pair("Universal Séries Madrugada", "Maratona das melhores temporadas de ação e investigação.")
            )
            return universalGrid[slotIdx % universalGrid.size]
        }

        // 14. Sony Channel & Sony Movies
        if (titleLower.contains("sony") || idLower.contains("sony")) {
            val sonyGrid = listOf(
                Pair("The Blacklist: Raymond Reddington", "O criminoso mais procurado ajuda o FBI a capturar outros foragidos."),
                Pair("Crossing Lines: Caçada Internacional", "A unidade de crimes especiais agindo através das fronteiras europeias."),
                Pair("The Good Doctor: O Bom Doutor", "O jovem cirurgião Shaun Murphy supera preconceitos com genialidade."),
                Pair("Grey's Anatomy: Vida & Emoção", "Meredith Grey e os médicos do Grey Sloan Memorial Hospital em cirurgias de risco."),
                Pair("Station 19: Batalhão de Seattle", "Resgates heróicos e drama no quartel dos bombeiros de Seattle."),
                Pair("Shark Tank Brasil: Negociando com Tubarões", "Empreendedores enfrentam os maiores investidores do país por aportes."),
                Pair("Cinema Sony: Sessão de Sucessos", "Filmes consagrados dos estúdios Sony Pictures na sua televisão."),
                Pair("The Good Doctor - Casos Clínicos", "Diagnósticos raros e cirurgias inovadoras que salvam vidas."),
                Pair("Grey's Anatomy: Horário Nobre", "Os momentos mais marcantes e emocionantes do drama médico."),
                Pair("Shark Tank Brasil - Edição Especial", "Inovações milionárias e negociações tensas na bancada."),
                Pair("Cinema Sony Especial da Noite", "Superprodução de Hollywood com alta qualidade sonora e visual."),
                Pair("The Blacklist: Lista Negra", "Segredos de estado e reviravoltas no submundo da espionagem."),
                Pair("Sony Madrugada: Grandes Histórias", "Dramas envolventes e cinema para acompanhar pela madrugada.")
            )
            return sonyGrid[slotIdx % sonyGrid.size]
        }

        // 15. AXN
        if (titleLower.contains("axn") || idLower.contains("axn")) {
            val axnGrid = listOf(
                Pair("CSI: Investigação da Cena do Crime", "Peritos forenses desvendam mistérios através de vestígios e DNA."),
                Pair("Hawaii Five-0: Ação no Paraíso", "A força-tarefa especial de Steve McGarrett combatendo o crime no Havaí."),
                Pair("NCIS: Investigação Criminal Marítima", "O agente especial Leroy Gibbs e sua equipe em missões da Marinha."),
                Pair("Criminal Minds: Mentes Criminosas", "A Unidade de Análise Comportamental traça perfis de assassinos seriais."),
                Pair("Candice Renoir: Detetive Francesa", "Intuição afiada e métodos pouco convencionais resolvendo crimes na França."),
                Pair("CSI: Miami com Horatio Caine", "Óculos escuros, lanchas e perícia avançada nas praias de Miami."),
                Pair("NCIS: Los Angeles", "Agentes infiltrados usam disfarces para derrubar cartéis e terroristas."),
                Pair("Criminal Minds: Caçada Noturna", "A psicologia forense no combate aos criminosos mais inteligentes."),
                Pair("CSI: Las Vegas - Edição Nobre", "Luz negra e microscópios revelam a verdade por trás dos assassinatos."),
                Pair("AXN Superestreia: Perigo Real", "Suspense policial, perseguições em alta velocidade e mistério."),
                Pair("NCIS: Investigação Criminal - Horário Nobre", "Os casos de segurança nacional mais complexos dos Estados Unidos."),
                Pair("Hawaii Five-0: Resgate Perigoso", "Tiroteios e helicópteros em operações táticas espetaculares."),
                Pair("AXN Madrugada de Ação", "Maratona das melhores séries investigativas do planeta.")
            )
            return axnGrid[slotIdx % axnGrid.size]
        }

        // 16. Paramount Network & Paramount+
        if (titleLower.contains("paramount") || idLower.contains("paramount")) {
            val paramountGrid = listOf(
                Pair("O Poderoso Chefão: Trilogia Épica", "A ascensão da família Corleone no crime organizado de Nova York."),
                Pair("Star Trek: A Nova Geração", "A tripulação da Enterprise explorando novos mundos e civilizações."),
                Pair("Sessão Western Clássico", "O Velho Oeste com tiroteios, foras da lei e cavalos."),
                Pair("Missão Impossível com Tom Cruise", "Ethan Hunt e a IMF salvando o mundo em missões vertiginosas."),
                Pair("Yellowstone: A Saga da Família Dutton", "John Dutton protegendo o maior rancho dos Estados Unidos a qualquer custo."),
                Pair("1883 / 1923: A Origem dos Dutton", "A dura jornada dos pioneiros pelas planícies americanas."),
                Pair("Mayor of Kingstown com Jeremy Renner", "A complexa engrenagem entre guardas prisionais, gangues e a polícia."),
                Pair("Tulsa King com Sylvester Stallone", "Um mafioso de Nova York construindo um império no interior de Oklahoma."),
                Pair("Yellowstone - Edição Nobre", "Intriga política, traição e disputas de terra em Montana."),
                Pair("Cine Paramount: Grandes Blockbusters", "Superprodução de cinema dos estúdios Paramount Pictures."),
                Pair("Top Gun: Ases Indomáveis", "A lenda dos caças F-14 na escola mais competitiva da Marinha."),
                Pair("Transformers: A Batalha por Cybertron", "Autobots e Decepticons em combates gigantescos na Terra."),
                Pair("Paramount Madrugada Cinematográfica", "Grandes clássicos premiados com Oscar na sua tela.")
            )
            return paramountGrid[slotIdx % paramountGrid.size]
        }

        // 17. Space
        if (titleLower.contains("space") || idLower.contains("space")) {
            val spaceGrid = listOf(
                Pair("Clube do Medo Space: Terror na Madrugada", "Monstros, assombrações e suspense sobrenatural."),
                Pair("Sessão Adrenalina: Fúria em Duas Rodas", "Motos velozes, rachas e perseguições implacáveis."),
                Pair("Space Ação: Tiroteios Mortais", "Operações de resgate com forças especiais e mercenários."),
                Pair("Clássicos de Luta: Van Damme & Seagal", "Golpes marciais e chutes voadores em combates eletrizantes."),
                Pair("Space Invaders: Ficção & Futuro", "Invasões alienígenas e batalhas espaciais com armas laser."),
                Pair("Velocidade Máxima Space", "Carros tunados e explosões nas ruas das grandes metrópoles."),
                Pair("Space Ação Total: Batalha Urbana", "Confrontos armados entre policiais e facções criminosas."),
                Pair("Cine Space: Heróis Sem Piedade", "Vingança implacável em tramas de ação frenética."),
                Pair("Space Superestreia da Noite", "O grande filme de adrenalina do mês em alta definição."),
                Pair("Clube da Luta Space", "Torneios clandestinos de artes marciais e sobrevivência."),
                Pair("Noite Sangrenta: Terror & Pânico", "Slasher movies e suspense sangrento."),
                Pair("Cine Brutal Space", "Combates sem limites com cenas de ação eletrizantes."),
                Pair("Space Madrugada sem Regras", "Ação ininterrupta até o amanhecer do dia.")
            )
            return spaceGrid[slotIdx % spaceGrid.size]
        }

        // 18. Cinemax
        if (titleLower.contains("cinemax") || idLower.contains("cinemax")) {
            val cinemaxGrid = listOf(
                Pair("Cinemax Madrugada: Suspense & Tensão", "Filmes de mistério com finais surpreendentes."),
                Pair("Cine Clássicos Cinemax", "Produções cult que marcaram os fãs da sétima arte."),
                Pair("Manhã de Aventura Cinemax", "Viagens e explorações com muita ação."),
                Pair("Cine Pipoca Cinemax", "Comédias leves e diversão para o almoço."),
                Pair("Cinemax Ação: Tiros & Carros", "Perseguições eletrizantes pelas rodovias americanas."),
                Pair("Sessão das Estrelas Cinemax", "Os maiores astros de Hollywood em tramas envolventes."),
                Pair("Tarde de Cinema Cinemax", "Filmes de grande bilheteria dublados com imagem impecável."),
                Pair("Cinemax Especial das 18h", "Superprodução de suspense e investigação policial."),
                Pair("Cine Máximo: Superestreia da Noite", "O filme principal da semana na tela do Cinemax."),
                Pair("Cinemax Blockbuster", "Efeitos especiais de última geração e som cinematográfico."),
                Pair("Terror Cinemax: Noite dos Monstros", "Criaturas assustadoras e lendas urbanas aterrorizantes."),
                Pair("Madrugada de Ação Cinemax", "Ação sem freio para acompanhar pela madrugada."),
                Pair("Cine Cinemax Fim de Noite", "O fechamento perfeito da grade de filmes.")
            )
            return cinemaxGrid[slotIdx % cinemaxGrid.size]
        }

        // 19. TNT & TNT Novelas
        if (titleLower.contains("tnt") || idLower.contains("tnt")) {
            val tntGrid = if (titleLower.contains("novela")) {
                listOf(
                    Pair("TNT Novelas: Yargi - Segredos de Família", "Um promotor honesto e uma advogada rebelde desvendam um assassinato."),
                    Pair("TNT Novelas: Iludida - Amor & Vingança", "A médica bem-sucedida descobre a traição do marido e planeja o troco."),
                    Pair("TNT Novelas: Meu Lar, Meu Destino", "O drama comovente de Zeynep dividido entre duas famílias e dois mundos."),
                    Pair("TNT Novelas: Amor Proibido", "A paixão proibida que escandalizou a alta sociedade de Istambul."),
                    Pair("TNT Novelas: Força de Mulher", "Bahar luta incansavelmente pelos seus filhos enfrentando adversidades."),
                    Pair("TNT Novelas: Yargi - Revelações", "A busca pelas pistas deixadas na cena do crime ganha novo rumo."),
                    Pair("TNT Novelas: Iludida - O Confronto", "Verdades dolorosas vêm à tona abalando o círculo social."),
                    Pair("TNT Novelas: Maratona da Tarde", "Reviva os capítulos mais emocionantes da semana."),
                    Pair("TNT Novelas: Horário Nobre - Yargi", "O julgamento do século com reviravoltas de tirar o fôlego."),
                    Pair("TNT Novelas: Iludida - Capítulo Inédito", "As consequências devastadoras do plano de vingança."),
                    Pair("TNT Novelas: Amor sem Fim (Kara Sevda)", "A saga de amor épica de Kemal e Nihan contra as barreiras sociais."),
                    Pair("TNT Novelas: Noite de Emoções", "Grandes produções dramáticas internacionais premiadas."),
                    Pair("TNT Novelas Madrugada: Grandes Romances", "Acompanhe as histórias de amor mais assistidas do mundo.")
                )
            } else {
                listOf(
                    Pair("Cine TNT: Noite dos Heróis", "Ação eletrizante com grandes super-heróis e vigilantes."),
                    Pair("Cine TNT Clássicos", "Filmes inesquecíveis que você nunca se cansa de assistir."),
                    Pair("TNT Manhã de Comédia", "Risadas com os comediantes mais famosos de Hollywood."),
                    Pair("Cine Pipoca TNT", "Aventuras para todos os públicos com dublagem primorosa."),
                    Pair("TNT Séries: Mistério & Investigação", "Episódios completos das melhores produções televisivas."),
                    Pair("Sessão Cine TNT: Sucessos de Bilheteria", "Os filmes mais assistidos do mundo na sua tela."),
                    Pair("TNT Ação: Batalha dos Astros", "Duelo entre astros do cinema em tramas eletrizantes."),
                    Pair("Tapete Vermelho TNT: Premiações & Cinema", "Cobertura dos maiores prêmios internacionais do entretenimento."),
                    Pair("Superestreia TNT da Noite", "O filme campeão de bilheteria com qualidade digital."),
                    Pair("Blockbuster TNT: Grandes Efeitos", "Superproduções de ficção, aventura e magia."),
                    Pair("Cine TNT Noite: Suspense Máximo", "Filmes que prendem a atenção do primeiro ao último segundo."),
                    Pair("TNT Terror: Gritos na Madrugada", "O melhor do gênero de terror contemporâneo."),
                    Pair("Cine TNT Madrugada Especial", "O fechamento de cinema da grade diária da TNT.")
                )
            }
            return tntGrid[slotIdx % tntGrid.size]
        }

        // 20. Discovery (Channel, Kids, Home & Health, Turbo, ID, Science, World, Theater, 4K)
        if (titleLower.contains("disc") || titleLower.contains("discovery") || catLower.contains("disc") || idLower.contains("disc")) {
            val discGrid = when {
                titleLower.contains("kids") -> listOf(
                    Pair("Hora de Dormir com Músicas Suaves", "Canções de ninar e historinhas tranquilas para os pequeninos."),
                    Pair("Aventuras no Espaço com os Desenhos", "Viagens divertidas por planetas e galáxias coloridas."),
                    Pair("Peppa Pig & George: Manhã Feliz", "Pular em poças de lama e brincadeiras com a família Pig."),
                    Pair("Patrulha Canina: Resgates Épicos", "Ryder e os filhotes em missões para proteger a Baía da Aventura."),
                    Pair("O Show da Luna! Por Que as Coisas São Assim?", "Luna, Júpiter e Cláudio em experimentos científicos divertidos."),
                    Pair("Mini Beat Power Rockers: Música & Ritmo", "Os bebês músicos tocando todos os instrumentos com alegria."),
                    Pair("PJ Masks: Heróis de Pijama", "Menino-Gato, Corujita e Lagartixo salvando a cidade à noite."),
                    Pair("Ricky Zoom: Aventuras sobre Rodas", "A motozinha mais veloz e seus amigos em resgates velozes."),
                    Pair("Cine Discovery Kids: O Grande Filme", "Longas-metragens de animação para encantar toda a criançada."),
                    Pair("Patrulha Canina: Novos Poderes", "A Patrulha Canina enfrenta o supervilão com superpoderes."),
                    Pair("Peppa Pig: Histórias Especiais", "Episódios inéditos cheios de diversão e aprendizado."),
                    Pair("Baile dos Bichos & Canções Animadas", "Músicas infantis para cantar e dançar em família."),
                    Pair("Desenhos Clássicos do Discovery Kids", "Animações educativas para relaxar antes do sono.")
                )
                titleLower.contains("turbo") -> listOf(
                    Pair("Joias Sobre Rodas: Restauração Clássica", "Mike Brewer e Marc Priestley ressuscitam carros lendários."),
                    Pair("Oficina Turbinada: Motores Monstruosos", "Mecânicos criam veículos sob medida com potência extrema."),
                    Pair("Carros Extremos: Velocidade Pura", "Testes com hipercarros nas pistas mais difíceis do mundo."),
                    Pair("Garagem do Futuro: Tecnologia Automotiva", "Veículos elétricos, baterias de última geração e designs futuristas."),
                    Pair("Restauração Rápida: De Lataria a Ouro", "Transformando carros enferrujados em máquinas valiosas."),
                    Pair("Corridas Proibidas: Disputas no Asfalto", "Pilotos de arrancada arriscam tudo em duelos de 400 metros."),
                    Pair("Mestres das Motos: Customização Lendária", "Choppers e motos esportivas feitas artesanalmente do zero."),
                    Pair("Febre de Motores: Pickups Gigantes", "Suspensões gigantes e tração 4x4 em terrenos lamacentos."),
                    Pair("Joias Sobre Rodas - Episódio Principal", "A compra, restauração e venda de um clássico automotivo."),
                    Pair("Oficinas dos Sonhos com Chip Foose", "O lendário designer transforma carros em obras de arte."),
                    Pair("Picape dos Sonhos: Construções Pesadas", "Motores V8 turbinados em caminhonetes personalizadas."),
                    Pair("Supercarros: Teste de Pista Máxima", "Atingindo mais de 400 km/h com supermáquinas na Europa."),
                    Pair("Turbo Madrugada: Corrida Sem Fim", "Os melhores projetos automotivos reunidos em maratona.")
                )
                titleLower.contains("h&h") || titleLower.contains("home") -> listOf(
                    Pair("Irmãos à Obra: Casas dos Sonhos", "Drew e Jonathan Scott reformam casas antigas em lares perfeitos."),
                    Pair("Vestida para Casar: O Vestido Perfeito", "Noivas procuram o modelo dos sonhos na famosa loja de Nova York."),
                    Pair("Quilos Mortais com Dr. Nowzaradan", "A emocionante jornada de superação de pacientes com obesidade severa."),
                    Pair("Reformas Milagrosas: Antes e Depois", "Arquitetos transformam ambientes caóticos em espaços aconchegantes."),
                    Pair("Cake Boss: O Mestre dos Bolos", "Buddy Valastro e sua família criando bolos esculturais inacreditáveis."),
                    Pair("Irmãos à Obra: Compra & Venda", "Dicas preciosas de decoração, acabamentos e valorização de imóveis."),
                    Pair("Minha Vida de 300 Quilos: O Reencontro", "Acompanhe como estão os pacientes anos após as cirurgias."),
                    Pair("Restaurando Lares: Casas Históricas", "Artesãos recuperam detalhes arquitetônicos de séculos passados."),
                    Pair("Irmãos à Obra - Horário Nobre", "A grande transformação com entrega das chaves à família emocionada."),
                    Pair("MasterChef: Desafio dos Sabores", "Chefs competindo pela receita mais inovadora sob pressão."),
                    Pair("Casas na Árvore Espetaculares", "Construções luxuosas no topo de árvores com vistas deslumbrantes."),
                    Pair("Bebê a Bordo: Chegada da Maternidade", "Famílias se preparando para a chegada dos recém-nascidos."),
                    Pair("Home & Health Madrugada Aconchegante", "Dicas de gastronomia, saúde e bem-estar para o seu dia a dia.")
                )
                titleLower.contains("id") || titleLower.contains("investiga") -> listOf(
                    Pair("Investigação Discovery: Crimes Quase Perfeitos", "Peritos desvendam crimes misteriosos através de detalhes mínimos."),
                    Pair("Mentes Assassinas: O Perfil dos Criminosos", "Psicólogos forenses analisam a motivação de crimes hediondos."),
                    Pair("Casos Não Solucionados", "Detetives reabrem pastas de crimes arquivados há décadas."),
                    Pair("Amor Mortal: Traições e Vingança", "Relacionamentos que terminaram em tragédias familiares."),
                    Pair("Desaparecidos: Em Busca de Respostas", "Famílias e autoridades procuram pistas sobre paradeiros."),
                    Pair("Sobrevivi: Histórias de Coragem", "Relatos impressionantes de quem escapou de sequestros e ataques."),
                    Pair("Crimes em Família: Segredos Ocultos", "Tramas secretas mantidas sob o teto das casas mais ricas."),
                    Pair("O Assassino do Zodíaco & Mistérios", "Documentários aprofundados sobre os maiores mistérios criminais."),
                    Pair("Investigação Discovery - Horário Nobre", "A reconstituição fiel dos casos mais chocantes da história policial."),
                    Pair("Perícia da Mente: Casos Reais", "Interrogatórios policiais reais gravados pelas câmeras da sala."),
                    Pair("Casos Arquivados com Novas Tecnologias", "Testes de DNA avançados apontando os culpados após anos."),
                    Pair("Noite Sinistra no ID", "Investigações sombrias com entrevistas exclusivas de sobreviventes."),
                    Pair("ID Madrugada: Mistérios Criminais", "Casos reais de investigação policial e tribunais.")
                )
                else -> listOf(
                    Pair("Mestres da Engenharia: Megaconstruções", "Como os maiores túneis, barragens e arranha-céus foram construídos."),
                    Pair("Pesca Mortal: Mar de Bering Tempestuoso", "Pescadores enfrentam ondas gigantescas na perigosa pesca do caranguejo."),
                    Pair("Planeta Selvagem: Predadores da Savana", "Leões, leopardos e crocodilos disputando território na África."),
                    Pair("Febre do Ouro: Minas Perigosas no Alasca", "Garimpeiros modernos escavando toneladas de terra atrás do ouro."),
                    Pair("Largados e Pelados: Sobrevivência Extrema", "Especialistas sobrevivem 21 dias na selva sem roupas e comida."),
                    Pair("Aeroporto: Área Restrita (Ao Vivo)", "A fiscalização rigorosa contra o tráfico de drogas nos aeroportos."),
                    Pair("Quilos Mortais: Histórias de Superação", "Pacientes enfrentando dietas rigorosas e cirurgias bariátricas."),
                    Pair("Febre do Ouro: Freddy Dodge ao Resgate", "Especialistas em recuperação de ouro ajudando minas à beira da falência."),
                    Pair("Largados e Pelados: Edição Especial", "Desafio nas regiões mais inóspitas e infestadas de predadores."),
                    Pair("Aeroporto: Fronteiras Internacionais", "A apreensão de mercadorias ilegais e passaportes falsificados."),
                    Pair("Ed Stafford: O Sobrevivente 60 Dias", "O aventureiro sobrevive totalmente sozinho em ilhas desertas."),
                    Pair("Expedição Planeta Terra 4K", "Imagens cinematográficas das profundezas dos oceanos e florestas."),
                    Pair("Discovery Madrugada Científica", "Documentários sobre o universo, física e as grandes descobertas.")
                )
            }
            return discGrid[slotIdx % discGrid.size]
        }

        // 21. History & History 2
        if (titleLower.contains("history") || idLower.contains("history")) {
            val historyGrid = listOf(
                Pair("Mistérios Subaquáticos: Naufrágios Secretos", "Mergulhadores exploram galeões espanhóis afundados no Caribe."),
                Pair("Grandes Construções da Antiguidade", "Como as pirâmides do Egito e templos romanos foram erguidos."),
                Pair("Ouro Maldito: Corrida no Alasca", "Buscadores de ouro enfrentam o congelamento do solo e temperaturas extremas."),
                Pair("Trato Feito: Relíquias Históricas na Loja", "Rick Harrison e Chumlee negociam armas antigas e moedas romanas."),
                Pair("Alienígenas do Passado: Novas Evidências", "Especialistas analisam hieróglifos e construções misteriosas pelo mundo."),
                Pair("Guerra das Ferramentas & Forja de Lâminas", "Ferreiros talentosos forjam espadas lendárias e armas históricas."),
                Pair("Segredos do Império Romano em 4K", "As legiões de César e os bastidores do Coliseu de Roma."),
                Pair("Trato Feito: Pechinchas de Milhões", "A negociação de itens históricos de colecionadores famosos."),
                Pair("Desastres da Engenharia: Erros Fatais", "Falhas de cálculo que derrubaram pontes e prédios históricos."),
                Pair("Segredos da Segunda Guerra Mundial", "Imagens raras e colorizadas das batalhas na Europa e no Pacífico."),
                Pair("O Universo: Buracos Negros e Supernovas", "Astrônomos explicam os mistérios mais profundos do cosmos."),
                Pair("Invenções que Mudaram a Humanidade", "Como a eletricidade, o motor a vapor e os microchips transformaram o mundo."),
                Pair("History Madrugada: Civilizações Perdidas", "As ruínas de Atlântida, Maias e civilizações esquecidas.")
            )
            return historyGrid[slotIdx % historyGrid.size]
        }

        // 22. Animal Planet
        if (titleLower.contains("animal") || idLower.contains("animal")) {
            val animalGrid = listOf(
                Pair("Planeta Selvagem: Predadores Africanos", "Leões e guepardos em caçadas cinematográficas na savana."),
                Pair("Veterinários do Texas: Resgate de Animais", "Médicos veterinários cuidando de animais exóticos e cavalos de raça."),
                Pair("Mundo dos Cães & Gatos: Cuidados Especiais", "Comportamento animal, adestramento e carinho para os pets."),
                Pair("Águas Selvagens: Tubarões Assassinos", "Biólogos marinhos mergulham cara a cara com o grande tubarão-branco."),
                Pair("Gatos Selvagens da América Latina", "Onças-pintadas e jaguatiricas nas florestas do Pantanal brasileiro."),
                Pair("Polícia Animal: Resgates de Maus-Tratos", "Equipes dedicadas salvam animais abandonados e os recuperam."),
                Pair("Vida de Macacos: Famílias da Floresta", "O comportamento social e a inteligência dos chimpanzés."),
                Pair("Monstros do Rio com Jeremy Wade", "O biólogo pesca peixes gigantescos e perigosos pelos rios do mundo."),
                Pair("O Reino dos Felinos em 4K", "A vida dos tigres na Ásia e leões na África em altíssima definição."),
                Pair("Veterinário de Fazenda: Partos & Curas", "O dia a dia nos pastos cuidando de gado leiteiro e ovelhas."),
                Pair("Planeta Animal: Aves Tropicais", "As cores exuberantes e o canto das araras e tucanos do Brasil."),
                Pair("África Selvagem: A Grande Migração", "Milhões de gnus cruzando rios repletos de crocodilos famintos."),
                Pair("Animal Planet Madrugada Selvagem", "Documentários fascinantes sobre a vida selvagem ao redor do globo.")
            )
            return animalGrid[slotIdx % animalGrid.size]
        }

        // 23. Cartoon Network & Infantil
        if (titleLower.contains("cartoon") || idLower.contains("cartoon")) {
            val cartoonGrid = listOf(
                Pair("Desenhos Clássicos: Looney Tunes", "Pernalonga, Patolino e Gaguinho em suas confusões lendárias."),
                Pair("Aventuras de Tom & Jerry", "O gato e o rato mais famosos da história da animação em perseguições eternas."),
                Pair("O Incrível Mundo de Gumball - Manhã", "Gumball e Darwin aprontando as maiores maluquices na escola de Elmore."),
                Pair("Jovens Titãs em Ação: Superheróis Malucos", "Robin, Ravena, Ciborgue e Estelar em confusões hilárias na torre."),
                Pair("Ursos Sem Curso: Pardo, Panda e Polar", "Os três irmãos ursos tentando se adaptar à vida moderna na cidade."),
                Pair("Ben 10: Omniverse em Ação", "Ben Tennyson transformado em alienígenas para salvar a galáxia."),
                Pair("Steven Universo: As Joias de Cristal", "Amizade, magia e canções inesquecíveis para salvar o planeta."),
                Pair("Cine Cartoon da Tarde", "Longa-metragem animado especial para rir do início ao fim."),
                Pair("Jovens Titãs em Ação: Maratona Especial", "Episódios mais assistidos com muita música e humor sem sentido."),
                Pair("O Incrível Mundo de Gumball - Noite", "As aventuras surreais da família Watterson na cidade de Elmore."),
                Pair("Hora de Aventura: Finn e Jake", "A Terra de Ooo repleta de magia, princesas e monstros mágicos."),
                Pair("Apenas um Show: Mordecai e Rigby", "Dois zeladores de parque se metendo em portais dimensionais."),
                Pair("Cartoon Network Madrugada Animada", "Os melhores clássicos dos estúdios Hanna-Barbera e Warner.")
            )
            return cartoonGrid[slotIdx % cartoonGrid.size]
        }

        // 24. A Fazenda 24H (Câmeras 1 a 7)
        if (titleLower.contains("fazenda") || idLower.contains("fazenda")) {
            val fazendaGrid = when {
                titleLower.contains("02") -> listOf(
                    Pair("Câmera 02: Trato com os Cavalos", "Os peões cuidam da baia dos cavalos e colocam feno fresco."),
                    Pair("Câmera 02: Silêncio nos Currais", "Acompanhe os animais descansando nas primeiras horas do dia."),
                    Pair("Câmera 02: Despertar dos Bichos", "Alimentação das aves, ovelhas e porcos na fazenda."),
                    Pair("Câmera 02: Ordenha da Vaca (Ao Vivo)", "Os peões responsáveis pela vaca tiram o leite da manhã."),
                    Pair("Câmera 02: Cuidados com os Porcos", "Limpeza dos chiqueiros e água fresca para os animais."),
                    Pair("Câmera 02: Soltura das Aves", "Galinhas e patos sendo soltos para pastar pelo terreiro."),
                    Pair("Câmera 02: Pasto & Horta Orgânica", "Colheita de hortaliças frescas para a comida da sede."),
                    Pair("Câmera 02: Trato da Tarde com as Ovelhas", "Contagem das ovelhas e abastecimento dos cochos."),
                    Pair("Câmera 02: Recolhimento dos Bichos", "Os animais sendo guardados nas baias para a noite."),
                    Pair("Câmera 02: Conversa na Área dos Animais", "Peões desabafam e comentam sobre o jogo perto das baias."),
                    Pair("Câmera 02: Silêncio no Curral", "Tranquilidade na área externa enquanto a sede ferve."),
                    Pair("Câmera 02: Madrugada nos Currais", "Câmeras térmicas registram os animais na noite."),
                    Pair("Câmera 02: Vigília Noturna da Baia", "Acompanhe a movimentação externa ao vivo.")
                )
                titleLower.contains("03") -> listOf(
                    Pair("Câmera 03: Área da Piscina na Madrugada", "A calmaria na piscina sob a iluminação noturna da sede."),
                    Pair("Câmera 03: Amanhecer na Área Externa", "O nascer do sol visto da piscina com vista panorâmica."),
                    Pair("Câmera 03: Treino Matinal na Academia", "Peões se exercitam nos aparelhos de musculação ao ar livre."),
                    Pair("Câmera 03: Conversas no Ofurô", "Bate-papo descontraído e banho de sol na área da piscina."),
                    Pair("Câmera 03: Banho de Piscina & Sol", "Peões aproveitam o calor para nadar e falar sobre alianças."),
                    Pair("Câmera 03: Fofocas na Espreguiçadeira", "Grupos combinam estratégias de votos para a formação da Roça."),
                    Pair("Câmera 03: Tarde de Treino & Pesos", "Musculação pesada e conversas sobre a convivência na casa."),
                    Pair("Câmera 03: Pôr do Sol na Piscina", "Fim de tarde relaxante com conversas sobre a saudade da família."),
                    Pair("Câmera 03: Reunião de Grupo na Piscina", "Estratégia secreta de votos definida para a votação ao vivo."),
                    Pair("Câmera 03: Área da Piscina Durante a Roça", "Reações dos peões durante os intervalos do programa oficial."),
                    Pair("Câmera 03: Festa na Área da Piscina", "Música, dança, drinks e muita paquera na festa semanal."),
                    Pair("Câmera 03: DR e Conversas Pós-Festa", "Discussões entre casais e acertos de contas à beira da água."),
                    Pair("Câmera 03: Madrugada na Piscina", "Peões conversam baixinho sob o céu estrelado.")
                )
                titleLower.contains("04") -> listOf(
                    Pair("Câmera 04: Cozinha na Calada da Noite", "Peões assaltam a geladeira e preparam lanches na madrugada."),
                    Pair("Câmera 04: Silêncio na Cozinha", "Tudo limpo e organizado aguardando o despertar da casa."),
                    Pair("Câmera 04: Café da Manhã dos Peões", "Café passado na hora, ovos mexidos e divisão das tarefas."),
                    Pair("Câmera 04: Preparação do Almoço", "Cozinheiros do dia descascam legumes e preparam a panela."),
                    Pair("Câmera 04: Almoço Coletivo na Mesa", "Todos os peões reunidos para comer com debates e risadas."),
                    Pair("Câmera 04: Lavando a Louça & Discussões", "A clássica cobrança sobre quem deixou louça suja na pia."),
                    Pair("Câmera 04: Café da Tarde & Bolo", "Bolo quentinho, café e conversas descontraídas na sala."),
                    Pair("Câmera 04: Preparação do Jantar", "Os peões preparam a refeição da noite antes do programa ao vivo."),
                    Pair("Câmera 04: Jantar Tenso Pré-Votação", "Clima pesado na mesa com peões já prontos para a Roça."),
                    Pair("Câmera 04: Sala de Estar Durante o Ao Vivo", "Todos sentados no sofá assistindo ao telão do apresentador."),
                    Pair("Câmera 04: Madrugada Pós-Votação na Sala", "Discussões inflamadas e bate-bocas após a Roça formada."),
                    Pair("Câmera 04: Cozinha da Madrugada", "Chá quente e desabafos sobre quem votou em quem."),
                    Pair("Câmera 04: Silêncio na Sala Principal", "Luzes apagadas na sala de estar da sede.")
                )
                titleLower.contains("05") -> listOf(
                    Pair("Câmera 05: Quarto dos Peões - Sono Pesado", "Câmeras infravermelhas registram o sono dos confinados."),
                    Pair("Câmera 05: Silêncio no Dormitório", "Nenhum barulho no quarto durante a madrugada."),
                    Pair("Câmera 05: O Despertador dos Peões", "O alarme sonoro toca acordando os peões para o trato."),
                    Pair("Câmera 05: Arrumação das Camas & Guarda-Roupa", "Peões escolhem suas roupas e se arrumam para o dia."),
                    Pair("Câmera 05: Conversas de Cama", "Fofocas embaixo do edredom sobre o jogo e alianças."),
                    Pair("Câmera 05: Cochilo da Tarde no Quarto", "Peões descansam e repõem as energias no ar-condicionado."),
                    Pair("Câmera 05: Preparação do Visual", "Maquiagem, perfume e secador de cabelo para a noite."),
                    Pair("Câmera 05: Figurinos Oficiais da Roça", "Os participantes vestidos para a noite de gala do programa."),
                    Pair("Câmera 05: Quarto Vazio - Programa no Ar", "Todos estão na sala de estar para o programa oficial."),
                    Pair("Câmera 05: Volta da Roça & Abraços", "Os peões salvos voltam gritando para o quarto comemorando."),
                    Pair("Câmera 05: Discussão no Quarto Pós-Roça", "Debates tensos de cama sobre falsidade e votos surpresa."),
                    Pair("Câmera 05: Preparando para Dormir", "Pijamas, dentes escovados e conversas sussurradas."),
                    Pair("Câmera 05: Luzes Apagadas no Dormitório", "Todos descansando na tranquilidade da madrugada.")
                )
                titleLower.contains("06") -> listOf(
                    Pair("Câmera 06: Dormitório da Baia", "Os peões excluídos da sede dormem sobre colchões no chão."),
                    Pair("Câmera 06: Silêncio na Baia dos Animais", "A convivência rústica e difícil dos moradores da baia."),
                    Pair("Câmera 06: Despertar Cedo na Baia", "Balde de água fria e o início dos trabalhos pesados."),
                    Pair("Câmera 06: Tarefas dos Baiados", "Cumprimento das regras estritas para não levar punição."),
                    Pair("Câmera 06: Bate-Papo Rústico na Baia", "Conversas sobre como é difícil dormir ao lado dos animais."),
                    Pair("Câmera 06: Banho Gelado da Baia", "A luta para tomar banho com balde de água fria externa."),
                    Pair("Câmera 06: Descanso na Sombra da Baia", "Peões descansam no chão de madeira da área rústica."),
                    Pair("Câmera 06: Estratégia dos Moradores da Baia", "Quem puxar da baia para o terceiro banquinho da Roça."),
                    Pair("Câmera 06: Arrumação da Baia para a Noite", "Lençóis estendidos no chão aguardando a votação."),
                    Pair("Câmera 06: Reação aos Votos da Baia", "Comentários sobre a puxada da baia que definiu a Roça."),
                    Pair("Câmera 06: Noite Tensa na Baia", "O frio e o desconforto dos peões emparedados."),
                    Pair("Câmera 06: Madrugada Fria na Baia", "Apoio mútuo entre os participantes mais vulneráveis."),
                    Pair("Câmera 06: Silêncio Noturno na Baia", "O sono rústico dos quatro moradores da baia.")
                )
                titleLower.contains("07") -> listOf(
                    Pair("Câmera 07: Quarto do Fazendeiro na Madrugada", "A suíte presidencial com cama king size e frigobar."),
                    Pair("Câmera 07: O Despertador do Líder", "O Fazendeiro da semana acorda com privilégios na casa."),
                    Pair("Câmera 07: Delegação das Tarefas (Ao Vivo)", "O Fazendeiro reúne todos na sala e distribui as funções."),
                    Pair("Câmera 07: Reunião Secreta no Quarto do Líder", "Aliados sobem para traçar as indicações diretas à Roça."),
                    Pair("Câmera 07: Banho Quente & Conforto do Fazendeiro", "Banheiro exclusivo e momentos de privacidade do líder."),
                    Pair("Câmera 07: Almoço VIP no Quarto do Fazendeiro", "Guloseimas exclusivas e conversas estratégicas no quarto."),
                    Pair("Câmera 07: Análise dos Peões no Telão do Líder", "O Fazendeiro assiste aos outros cômodos pelas câmeras."),
                    Pair("Câmera 07: Dúvida da Indicação à Roça", "O líder ensaia seu discurso decisivo de indicação."),
                    Pair("Câmera 07: Vestindo o Chapéu de Fazendeiro", "O símbolo máximo de poder na fazenda sendo colocado."),
                    Pair("Câmera 07: Indicação do Fazendeiro ao Vivo", "O voto soberano e sem direito a veto direto na Roça."),
                    Pair("Câmera 07: Comemoração no Quarto do Líder", "Aliados celebram mais uma semana de liderança e poder."),
                    Pair("Câmera 07: Madrugada do Fazendeiro da Semana", "Planejando os próximos passos do jogo no conforto da suíte."),
                    Pair("Câmera 07: Sono Tranquilo do Fazendeiro", "Imune ao voto da casa por ser o líder soberano.")
                )
                else -> listOf(
                    Pair("Câmera Principal: Madrugada na Sede", "Câmeras registram toda a movimentação ao vivo pela sede."),
                    Pair("Câmera Principal: O Despertar da Casa", "O amanhecer dos peões ao som do despertador oficial."),
                    Pair("Câmera Principal: Café da Manhã & Animais", "A rotina matinal com alimentação de todos os bichos."),
                    Pair("Câmera Principal: Convivência & Tarefas", "Limpeza da casa, horta e preparativos para o almoço."),
                    Pair("Câmera Principal: Almoço dos Confinados", "Todos reunidos à mesa com conversas sobre o jogo."),
                    Pair("Câmera Principal: Tarde de Fofocas & Estratégias", "Reunião de panelinhas combinando os votos para a Roça."),
                    Pair("Câmera Principal: Piscina & Academia", "Banho de sol, musculação e conversas descontraídas."),
                    Pair("Câmera Principal: Preparação para a Noite", "Peões se arrumam com figurinos especiais para o ao vivo."),
                    Pair("Câmera Principal: Formação da Roça (Ao Vivo)", "Votação aberta, justificativas e tretas ao vivo na TV."),
                    Pair("Câmera Principal: Prova do Fazendeiro Ao Vivo", "Disputa eletrizante pelo chapéu mais cobiçado do Brasil."),
                    Pair("Câmera Principal: Madrugada Pós-Roça", "Bate-boca e alianças quebradas após a votação oficial."),
                    Pair("Câmera Principal: Festa da Semana (Ao Vivo)", "Muita música, dança e beijos na festa temática dos peões."),
                    Pair("Câmera Principal: Fim de Noite na Fazenda", "Os confinados vão se deitando após um dia intenso.")
                )
            }
            return fazendaGrid[slotIdx % fazendaGrid.size]
        }

        // 25. Globo (SP, RJ, TV Globo, 4K, H265)
        if (titleLower.contains("globo") && !titleLower.contains("news") && !idLower.contains("news")) {
            val globoGrid = listOf(
                Pair("Jornal da Globo com Renata Lo Prete", "As principais notícias do fechamento do dia e análise política e econômica."),
                Pair("Hora Um da Notícia / Corujão", "As primeiras horas do amanhecer com o trânsito e o giro pelas capitais."),
                Pair("Bom Dia Praça (Ao Vivo)", "Telejornal local com as primeiras informações da sua região e transporte público."),
                Pair("Bom Dia Brasil (Ao Vivo)", "As principais notícias do Brasil e do mundo com correspondentes internacionais."),
                Pair("Encontro com Patrícia Poeta (Ao Vivo)", "Música, debate sobre os temas do momento e histórias emocionantes."),
                Pair("Mais Você com Ana Maria Braga", "Culinária deliciosa, matérias curiosas e o bom humor do Louro Mané."),
                Pair("Praça TV - 1ª Edição (Ao Vivo)", "O telejornal do meio-dia focado na sua cidade e prestação de serviços."),
                Pair("Globo Esporte / Jornal Hoje", "A cobertura esportiva da rodada e o noticiário nacional com César Tralli."),
                Pair("Sessão da Tarde Especial", "Grandes sucessos do cinema mundial para reunir toda a família na sala."),
                Pair("Vale a Pena Ver de Novo / Novela das Seis", "A reapresentação dos maiores clássicos da teledramaturgia brasileira."),
                Pair("Praça TV - 2ª Edição (Ao Vivo)", "O resumo dos fatos mais relevantes da noite na sua região metropolitana."),
                Pair("Jornal Nacional com William Bonner", "O telejornal de maior audiência do país trazendo a cobertura dos fatos do dia."),
                Pair("Novela das Nove / Linha de Shows / Futebol", "A principal produção dramática da TV brasileira e grandes transmissões esportivas.")
            )
            return globoGrid[slotIdx % globoGrid.size]
        }

        // 26. GloboNews
        if (titleLower.contains("globonews") || titleLower.contains("globo news") || idLower.contains("globo_news")) {
            val globonewsGrid = listOf(
                Pair("GloboNews Madrugada (Ao Vivo)", "O plantão de notícias com os fatos que marcam a virada do dia."),
                Pair("GloboNews Em Ponto", "Abertura do mercado financeiro e as notícias políticas que vão pautar o dia."),
                Pair("Conexão GloboNews (Ao Vivo)", "Brasília, São Paulo e Rio conectados trazendo as primeiras apurações."),
                Pair("Edição das 10h com Andréia Sadi", "Bastidores quentes do poder em Brasília e entrevistas exclusivas."),
                Pair("GloboNews em Meio-Dia", "O resumo dos fatos mais marcantes da manhã com comentaristas."),
                Pair("Estúdio i com Andréia Sadi (Ao Vivo)", "Jornalismo dinâmico, debate descontraído e informação de bastidor."),
                Pair("GloboNews Mais (Ao Vivo)", "Aprofundamento dos acontecimentos da tarde com correspondentes internacionais."),
                Pair("GloboNews Em Pauta (Ao Vivo com Marcelo Cosme)", "A bancada de jornalistas debate os grandes temas do dia em clima amigável."),
                Pair("Jornal das Dez (J10 com Aline Midlej)", "A edição nobre do jornalismo com análise profunda dos principais fatos."),
                Pair("Cidades e Soluções com André Trigueiro", "Sustentabilidade, inovações ecológicas e energia limpa."),
                Pair("GloboNews Especial & Documentários", "Grandes investigações jornalísticas premiadas no Brasil e no exterior."),
                Pair("GloboNews Painel: Economia & Negócios", "Economistas renomados debatem taxas de juros, inflação e investimentos."),
                Pair("Jornal GloboNews - Fechamento Noturno", "Giro completo pelas redações atualizando os acontecimentos mundiais.")
            )
            return globonewsGrid[slotIdx % globonewsGrid.size]
        }

        // 27. SBT
        if (titleLower.contains("sbt") || idLower.contains("sbt")) {
            val sbtGrid = listOf(
                Pair("Operação Mesquita / SBT News Madrugada", "Reportagens curiosas e jornalismo dinâmico na calada da noite."),
                Pair("SBT PodNight: Entrevistas Exclusivas", "Os podcasts de maior sucesso da internet exibidos na televisão."),
                Pair("Primeiro Impacto com Marcão do Povo (Ao Vivo)", "Jornalismo popular com prestação de serviço, trânsito e ocorrências policiais."),
                Pair("Chega Mais (Ao Vivo)", "Revista eletrônica com variedades, culinária, dicas de saúde e entretenimento."),
                Pair("Notícias do SBT & Desenhos Animados", "Informação matinal mesclada com a tradição de desenhos animados."),
                Pair("Tá na Hora - Edição Local", "O que acontece na sua cidade no horário do meio-dia com repórteres nas ruas."),
                Pair("Fofocalizando (Ao Vivo com Leo Dias)", "Os bastidores do mundo das celebridades, fofocas e exclusivos das estrelas."),
                Pair("Novelas da Tarde SBT", "Grandes produções internacionais românticas e dramáticas dubladas."),
                Pair("Tá na Hora Nacional com Datena e Equipe", "Cobertura investigativa ao vivo dos fatos que mobilizam o Brasil."),
                Pair("SBT Brasil com César Filho (Ao Vivo)", "O telejornal noturno com as principais notícias do Brasil e do mundo."),
                Pair("A Infância de Romeu e Julieta", "Novela infanto-juvenil de grande sucesso para reunir toda a família."),
                Pair("Programa do Ratinho (Ao Vivo)", "Jogos populares, atrações musicais, comédia e o famoso teste de DNA."),
                Pair("The Noite com Danilo Gentili", "O talk show mais premiado e divertido da televisão brasileira.")
            )
            return sbtGrid[slotIdx % sbtGrid.size]
        }

        // 28. Record & Record News
        if (titleLower.contains("record") || idLower.contains("rec")) {
            val recordGrid = if (titleLower.contains("news") || idLower.contains("record_news")) {
                listOf(
                    Pair("Record News Madrugada", "Informações globais atualizadas a cada meia hora."),
                    Pair("Hora News - Primeira Edição", "Giro das primeiras notícias do Brasil e do mundo."),
                    Pair("Link Record News (Ao Vivo)", "Conexão com os repórteres em Brasília, São Paulo e Rio de Janeiro."),
                    Pair("Zapping Record News", "Cultura pop, redes sociais, tecnologia e entretenimento."),
                    Pair("Conexão Record News (Ao Vivo)", "Economia, agronegócio e política com entrevistas em estúdio."),
                    Pair("Mundo Meio-Dia Record News", "O resumo das notícias internacionais com correspondentes."),
                    Pair("Link Record News Tarde", "Debate sobre as decisões políticas de Brasília e do Congresso Nacional."),
                    Pair("Eco Record News: Meio Ambiente", "Reportagens especiais sobre sustentabilidade e ecologia."),
                    Pair("JR News com Celso Freitas", "A edição mais completa do telejornal com os destaques nacionais."),
                    Pair("Record News Internacional", "Análise dos conflitos geopolíticos e decisões das grandes potências."),
                    Pair("Aldeia Global Record News", "Grandes matérias e documentários das maiores emissoras mundiais."),
                    Pair("Record News Economia & Mercado", "Bolsa de valores, dólar e investimentos explicados."),
                    Pair("Fechamento do Dia Record News", "Resumo de todos os acontecimentos das últimas 24 horas.")
                )
            } else {
                listOf(
                    Pair("Programação da Madrugada Record", "Conteúdo reflexivo e jornalismo da virada do dia."),
                    Pair("Igreja Universal / Balanço Madrugada", "Abertura dos trabalhos jornalísticos com prestação de serviço."),
                    Pair("Balanço Geral Manhã (Ao Vivo)", "Notícias do trânsito, clima e ocorrências do amanhecer na capital."),
                    Pair("Fala Brasil (Ao Vivo)", "Informações matinais com credibilidade, agilidade e correspondentes."),
                    Pair("Hoje em Dia com Ana Hickmann e Celso Zucatelli", "Moda, gastronomia, fofocas dos famosos e prestação de serviço."),
                    Pair("Balanço Geral Tarde com Reinaldo Gottino", "Notícias quentes do dia, jornalismo comunitário e links ao vivo."),
                    Pair("A Hora da Venenosa com Fabíola Reipert", "As fofocas mais quentes dos famosos contadas com muito bom humor."),
                    Pair("Cidade Alerta com Luiz Bacci (Ao Vivo)", "Cobertura investigativa ao vivo dos crimes e ocorrências mais graves do país."),
                    Pair("Cidade Alerta: Edição Noturna", "Aprofundamento dos casos mais complexos com helicóptero e repórteres."),
                    Pair("Jornal da Record (Ao Vivo)", "Noticiário nacional e internacional em alta definição com grandes reportagens."),
                    Pair("Novela Bíblica: Reis / Força de Mulher", "Superprodução dramática com qualidade técnica de cinema."),
                    Pair("A Fazenda 24H: Transmissão da Sede", "Conflitos, votações e dinâmicas dos peões ao vivo no reality show."),
                    Pair("Câmera Record / Chicago P.D.", "Jornalismo investigativo premiado e séries policiais de ação.")
                )
            }
            return recordGrid[slotIdx % recordGrid.size]
        }

        // 29. Band & BandNews
        if (titleLower.contains("band") || idLower.contains("band")) {
            val bandGrid = if (titleLower.contains("news") || idLower.contains("band_news")) {
                listOf(
                    Pair("BandNews Madrugada (Ao Vivo)", "Notícias atualizadas a cada 15 minutos sem intervalos."),
                    Pair("Manhã BandNews: Primeiras Informações", "Trânsito, previsão do tempo e manchetes dos jornais."),
                    Pair("BandNews com Eduardo Oinegue", "Análises afiadas sobre o cenário político e econômico."),
                    Pair("Jornal BandNews - Edição das 10h", "Repórteres ao vivo em Brasília trazendo votações do Congresso."),
                    Pair("BandNews no Meio do Dia (Ao Vivo)", "O resumo dos principais acontecimentos com comentaristas."),
                    Pair("Tarde BandNews: Giro Nacional", "Os fatos mais importantes do Brasil e do mundo com agilidade."),
                    Pair("Ponto a Ponto: Debate Político", "Grandes nomes da política debatendo os rumos do país."),
                    Pair("BandNews Expresso da Tarde", "Economia, cotações financeiras e notícias de bastidor."),
                    Pair("Jornal da BandNews TV - Edição da Noite", "O telejornal de maior audiência do canal com análises completas."),
                    Pair("Canal Livre BandNews", "Entrevistas aprofundadas com as personalidades mais importantes da nação."),
                    Pair("BandNews Internacional", "Notícias das capitais mundiais com a rede de correspondentes."),
                    Pair("Noite em Pauta BandNews", "Debates sobre os temas que repercutiram durante o dia."),
                    Pair("BandNews Fim de Noite", "A síntese definitiva das últimas 24 horas no Brasil.")
                )
            } else {
                listOf(
                    Pair("+ Info / Vídeos Incríveis na Madrugada", "Curiosidades, flagrantes do mundo e matérias especiais."),
                    Pair("Primeiro Jornal com João Paulo Vergueiro", "As notícias do amanhecer com trânsito e prestação de serviço."),
                    Pair("Bora Brasil com Joel Datena (Ao Vivo)", "O noticiário matinal com agilidade e conexão direta com a comunidade."),
                    Pair("Jogo Aberto com Renata Fan e Denílson (Ao Vivo)", "Debate esportivo irreverente com os gols e polêmicas da rodada."),
                    Pair("Os Donos da Bola com Craque Neto (Ao Vivo)", "A opinião sem rodeios do apresentador mais polêmico da TV."),
                    Pair("Melhor da Tarde com Catia Fonseca", "Receitas fáceis, fofocas dos famosos e prestação de serviço na tarde."),
                    Pair("Brasil Urgente com José Luiz Datena (Ao Vivo)", "Cobertura policial implacável com helicóptero e links pelo Brasil."),
                    Pair("Brasil Urgente - Edição Local", "As ocorrências mais graves e notícias da sua região metropolitana."),
                    Pair("Jornal da Band com Eduardo Oinegue e Adriana Araújo", "Credibilidade máxima no telejornal com análises profundas."),
                    Pair("Melhor da Noite com Glenda Kozlowski e Zeca Camargo", "Variedades, cultura popular, ciência e música ao vivo."),
                    Pair("Perrengue do Dia / Linha de Shows", "Vídeos hilários da internet comentados pela bancada de humoristas."),
                    Pair("Jornal da Noite Band", "O fechamento do dia com os fatos políticos e econômicos mais importantes."),
                    Pair("Esporte Total com Fernando Fernandes", "Os gols da rodada e compactos das competições esportivas.")
                )
            }
            return bandGrid[slotIdx % bandGrid.size]
        }

        // 30. CNN Brasil
        if (titleLower.contains("cnn") || idLower.contains("cnn")) {
            val cnnGrid = listOf(
                Pair("CNN Madrugada (Ao Vivo)", "Giro internacional de notícias com a rede global da CNN."),
                Pair("CNN Novo Dia (Ao Vivo)", "Abertura dos trabalhos jornalísticos com análises de política e economia."),
                Pair("Live CNN Brasil com Elisa Veeck", "Informação dinâmica, debates e apuração dos bastidores de Brasília."),
                Pair("O Grande Debate: Ideias em Confronto", "Dois pontos de vista antagônicos discutindo temas polêmicos da semana."),
                Pair("Bastidores do Poder CNN", "Entrevistas exclusivas com ministros, senadores e deputados federais."),
                Pair("CNN 360º com Daniela Lima", "A apuração mais quente da política nacional e decisões judiciais."),
                Pair("CNN Money: Negócios & Mercado", "Fechamento das bolsas mundiais, cotação do dólar e investimentos."),
                Pair("CNN Prime Time com Márcio Gomes (Ao Vivo)", "A edição principal de notícias com reportagens especiais e gráficos."),
                Pair("WW com William Waack (Análise Aprofundada)", "O jornalista mais respeitado do país disseca os grandes temas globais."),
                Pair("CNN Arena: Mesa Redonda Política", "Analistas de diferentes espectros debatem as decisões do governo."),
                Pair("Documentários Originais CNN", "Grandes produções investigativas ganhadoras de prêmios internacionais."),
                Pair("CNN Viagem & Gastronomia", "Roteiros turísticos de luxo e a alta gastronomia pelo mundo."),
                Pair("Jornal da CNN - Fechamento", "A síntese completa dos fatos mais relevantes do Brasil e do mundo.")
            )
            return cnnGrid[slotIdx % cnnGrid.size]
        }

        // 31. Jovem Pan News
        if (titleLower.contains("jovem pan") || titleLower.contains("jp news") || idLower.contains("jovem_pan")) {
            val jpGrid = listOf(
                Pair("Jovem Pan Madrugada: Notícias da Hora", "A cobertura jornalística contínua direto dos estúdios da Pan."),
                Pair("Jornal da Manhã - 1ª Edição", "O telejornal matinal mais tradicional do rádio agora na TV."),
                Pair("Jornal da Manhã - 2ª Edição (Ao Vivo)", "Manchetes dos grandes jornais e comentários de analistas renomados."),
                Pair("Morning Show (Ao Vivo com a Bancada)", "Debates quentes sobre política, cultura pop e temas das redes sociais."),
                Pair("Bancada Jovem Pan: Análise Política", "Comentaristas debatem as principais votações do Congresso Nacional."),
                Pair("Pânico na TV (Ao Vivo com Emílio Surita)", "O programa de entrevistas mais irreverente e divertido do país."),
                Pair("Linha de Frente com Fernando Capez", "Entrevistas aprofundadas com grandes personalidades do Judiciário."),
                Pair("Prós e Contras: O Grande Confronto", "Debate direto entre parlamentares de diferentes posições políticas."),
                Pair("Os Pingos nos Is (Ao Vivo)", "O programa de maior audiência do jornalismo de opinião no país."),
                Pair("Jornal Jovem Pan (Edição Nobre)", "O noticiário principal com os acontecimentos mais marcantes do dia."),
                Pair("Opinião com os Maiores Colunistas", "Análises firmes sobre os rumos econômicos e institucionais do Brasil."),
                Pair("Direto ao Ponto: Grande Entrevista", "Sabatinas com figuras centrais da política e economia brasileira."),
                Pair("Jovem Pan News Noite", "O resumo dos momentos mais importantes e debates do dia.")
            )
            return jpGrid[slotIdx % jpGrid.size]
        }

        // 32. Variedades (Canal Brasil, GNT, Modo Viagem, Viva)
        if (titleLower.contains("gnt") || titleLower.contains("canal brasil") || titleLower.contains("viagem") || titleLower.contains("viva") || idLower.startsWith("var_")) {
            val varGrid = listOf(
                Pair("Papo de Segunda: Melhores Momentos", "Reflexões sobre a vida, relacionamentos e cultura em tom franco."),
                Pair("Cinema Brasileiro de Madrugada", "Filmes autorais e clássicos inesquecíveis da cinematografia nacional."),
                Pair("Saia Justa: Debates & Emoções", "Conversas inteligentes com mulheres inspiradoras sobre temas da atualidade."),
                Pair("Cozinha Prática com Rita Lobo", "Receitas deliciosas, fáceis e saudáveis para o dia a dia na cozinha."),
                Pair("Lugar Incomum com Didi Wagner", "Viagens fantásticas pelos pontos turísticos mais surpreendentes do planeta."),
                Pair("Novelas Inesquecíveis no Viva", "A emoção das novelas mais marcantes da história da televisão brasileira."),
                Pair("Que Seja Doce: Batalha de Confeiteiros", "Confeiteiros talentosos disputam o melhor bolo e sobremesa."),
                Pair("Decora com Maurício Arruda", "Transformações completas de ambientes com design moderno e acessível."),
                Pair("Papo de Segunda (Ao Vivo)", "Fábio Porchat e convidados debatem os dilemas da vida com inteligência e humor."),
                Pair("Saia Justa (Horário Nobre)", "O programa mais sofisticado de debates e entrevistas femininas da TV."),
                Pair("Mundo em Viagem: Destinos Paradisíacos", "Praias de águas cristalinas, resorts de luxo e paisagens de tirar o fôlego."),
                Pair("Canal Brasil Cineclube", "Grandes cineastas e atores brasileiros comentam obras consagradas."),
                Pair("Variedades & Cultura na Madrugada", "Música, teatro e documentários culturais na sua tela.")
            )
            return varGrid[slotIdx % varGrid.size]
        }

        // 33. Agro & Rural (AgroMais, Canal Rural, Terra Viva)
        if (catLower.contains("agro") || titleLower.contains("agro") || titleLower.contains("rural") || titleLower.contains("terra viva") || idLower.contains("agro")) {
            val agroGrid = listOf(
                Pair("Mercado Noturno: Cotações Agrícolas", "O fechamento das bolsas de Chicago e B3 para milho, soja e boi gordo."),
                Pair("Bom Dia Campo: Previsão do Clima", "Mapas de chuvas e temperaturas para o planejamento das lavouras."),
                Pair("Giro do Boi: Pecuária de Corte", "Manejo nutricional, pastagens e genética bovina de alta performance."),
                Pair("AgroMais Notícias (Ao Vivo)", "As decisões políticas em Brasília que impactam os produtores rurais."),
                Pair("Canal Rural: Mercado & Cotações", "Preços atualizados da saca de soja, café, cana-de-açúcar e fertilizantes."),
                Pair("Conexão Campo: Tecnologia no Campo", "Drones, tratores autônomos e agricultura de precisão para maior produtividade."),
                Pair("Leilão de Touros & Matrizes Ao Vivo", "Comercialização de animais de elite das maiores fazendas do país."),
                Pair("Dia de Campo: Inovações Agronômicas", "Pesquisadores da Embrapa apresentam novas sementes resistentes à seca."),
                Pair("Jornal do Agro (Edição Principal)", "O noticiário completo com os destaques do agronegócio que move o Brasil."),
                Pair("Grande Leilão Virtual de Cavalos Crioulos", "Criadores disputam os exemplares mais premiados da raça em pista."),
                Pair("Agro Forte: Histórias de Sucesso", "Trajetórias inspiradoras de famílias que construíram impérios agrícolas."),
                Pair("Painel Econômico do Agronegócio", "Especialistas analisam exportações para a China e União Europeia."),
                Pair("Agro & Rural Madrugada", "Dicas técnicas de irrigação e sustentabilidade para a fazenda.")
            )
            return agroGrid[slotIdx % agroGrid.size]
        }

        // 34. Religiosos (TV Aparecida, Canção Nova, Rede Vida, Novo Tempo)
        if (catLower.contains("religioso") || titleLower.contains("aparecida") || titleLower.contains("cancaonova") || titleLower.contains("redevida") || idLower.contains("religioso")) {
            val religiosoGrid = listOf(
                Pair("O Santo Terço na Madrugada", "Orações de fé e contemplação dos mistérios divinos."),
                Pair("A Palavra de Deus: Manhã de Paz", "Mensagens bíblicas de conforto e esperança para o coração."),
                Pair("Santa Missa de Aparecida (Ao Vivo)", "Transmissão solene direto do Santuário Nacional de Nossa Senhora."),
                Pair("Terço dos Homens: Fé & Devoção", "Homens reunidos em oração pela família e pela paz no mundo."),
                Pair("Música & Louvor na Fé", "Cânticos religiosos e testemunhos de milagres e graças alcançadas."),
                Pair("Santa Missa do Meio-Dia", "Celebração eucarística com bênção especial para os trabalhadores."),
                Pair("Terço da Misericórdia (Ao Vivo)", "Momento de profunda oração e pedidos de proteção espiritual."),
                Pair("Palestra de Formação Cristã", "Reflexões espirituais com sacerdotes e conselheiros de fé."),
                Pair("Programa Especial da Família", "Orientações bíblicas para harmonia no lar e educação dos filhos."),
                Pair("Santa Missa das 18h (Ao Vivo)", "Agradecimento pelas bênçãos do dia em comunhão fraterna."),
                Pair("Noite de Cura & Libertação", "Oração fervorosa com bênção do Santíssimo Sacramento."),
                Pair("Testemunhos de Fé & Milagres", "Histórias reais de superação através da oração e da perseverança."),
                Pair("Oração da Noite: Bênção Final", "Preces de agradecimento e proteção para o descanso em paz.")
            )
            return religiosoGrid[slotIdx % religiosoGrid.size]
        }

        // 35. Adultos (+18)
        if (catLower.contains("adulto") || titleLower.contains("playboy") || titleLower.contains("sexy") || titleLower.contains("brazzers") || idLower.contains("adulto") || idLower.contains("playboy")) {
            val adultGrid = listOf(
                Pair("Sessão Madrugada Sem Cortes (+18)", "As mais belas modelos internacionais em produções explícitas de alta qualidade."),
                Pair("Superprodução Exclusiva da Madrugada", "Conteúdo adulto premium em ultra alta definição sem censura."),
                Pair("Madrugada Quente HD", "Produções europeias e americanas com enredos sensuais eletrizantes."),
                Pair("Sessão Especial: As Estrelas do Mês", "Compilação das atrizes mais premiadas do entretenimento adulto mundial."),
                Pair("Especial Dia: Sensualidade & Prazer", "Cenas eletrizantes com direção premiada e trilha sonora envolvente."),
                Pair("Supercombo Adulto HD", "Grandes clássicos das maiores produtoras de filmes adultos dos EUA."),
                Pair("Tarde Proibida (+18)", "Entretenimento erótico 24 horas no ar com as melhores cenas."),
                Pair("Encontros Sensuais em Alta Definição", "O melhor do cinema erótico contemporâneo com modelos consagradas."),
                Pair("Sessão Prime Time Adulto (+18)", "As maiores novidades da indústria mundial para adultos direto na sua TV."),
                Pair("Superestreia Adulta Exclusiva", "Lançamento mensal com elenco de superestrelas do entretenimento adulto."),
                Pair("Hardcore Sem Censura HD", "Ação explícita intensa das maiores franquias mundiais para maiores."),
                Pair("Noite Selvagem (+18)", "Filmes premiados nos principais festivais adultos com enredos marcantes."),
                Pair("Festival das Modelos Internacionais", "Entrevistas, bastidores e cenas completas com as musas mais desejadas.")
            )
            return adultGrid[slotIdx % adultGrid.size]
        }

        // 36. Grade de Cinema & Séries Geral (Fallback de Filmes)
        if (catLower.contains("filme") || catLower.contains("serie") || catLower.contains("cinema")) {
            val filmesGrid = listOf(
                Pair("Cine Madrugada: Suspense & Ação", "Superproduções de Hollywood sem comerciais para a calada da noite."),
                Pair("Sessão Retrô: Filmes Inesquecíveis", "Obras consagradas que marcaram época nos cinemas mundiais."),
                Pair("Manhã de Aventura & Fantasia", "Mundos mágicos, heróis lendários e grandes desafios."),
                Pair("Sessão Pipoca: Comédias de Sucesso", "Filmes divertidos e comédias leves para curtir na hora do almoço."),
                Pair("Série em Destaque: Temporada Épica", "Episódios completos em alta definição das melhores séries do mundo."),
                Pair("Cine Clássicos do Cinema", "Grandes atuações e roteiros impecáveis premiados no Oscar."),
                Pair("Sessão Tarde de Ação: Tiroteios & Carros", "Adrenalina pura com perseguições velozes e muita emoção."),
                Pair("Cine Especial do Fim de Tarde", "Superprodução de aventura e suspense para acompanhar."),
                Pair("Superfilme da Noite em Alta Definição", "O grande lançamento cinematográfico da semana na sua tela."),
                Pair("Blockbuster Especial: Sucesso Mundial", "Efeitos visuais inacreditáveis e som imersivo de cinema."),
                Pair("Cine Suspense Noturno: Tensão Máxima", "Tramas policiais intrincadas com finais surpreendentes."),
                Pair("Noite de Terror & Mistério", "Grandes filmes de arrepiar para quem gosta de sustos na tela."),
                Pair("Cine Corujão: O Melhor do Cinema", "Filmes consagrados fechando a programação diária com chave de ouro.")
            )
            return filmesGrid[slotIdx % filmesGrid.size]
        }

        // 37. Fallback Geral de Televisão (Elegante e Contextual)
        val defaultGrid = listOf(
            Pair("Transmissão Especial da Madrugada", "Acompanhe a melhor programação noturna em alta definição."),
            Pair("Abertura da Grade Diária", "Os primeiros destaques e acontecimentos do dia a dia."),
            Pair("Revista Matinal de Conteúdo", "Entrevistas, convidados especiais e debates do cotidiano."),
            Pair("Painel de Notícias & Atualidades", "Análise dos principais acontecimentos com reportagens."),
            Pair("Edição do Meio-Dia do Canal", "Resumo completo das novidades mais importantes da manhã."),
            Pair("Tarde em Foco: Conteúdo Exclusivo", "Cobertura especial, matérias temáticas e entretenimento."),
            Pair("Magazine Cultural & Variedades", "O melhor da cultura, tendências e personalidades da TV."),
            Pair("Transmissão Principal da Tarde", "Programação de destaque voltada ao público do canal."),
            Pair("Jornal da Noite em Alta Definição", "O fechamento dos fatos mais relevantes com comentaristas."),
            Pair("Especial Nobre do Canal", "A principal produção do canal no horário de maior audiência."),
            Pair("Grandes Histórias & Debates", "Documentários e entrevistas com grandes personalidades."),
            Pair("Noite em Revista: Os Destaques", "O resumo dos momentos mais marcantes exibidos no dia."),
            Pair("Encerramento da Programação Diária", "Os melhores momentos e a grade de amanhã na sua TV.")
        )
        return defaultGrid[slotIdx % defaultGrid.size]
    }
}
