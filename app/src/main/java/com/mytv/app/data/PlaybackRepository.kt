package com.mytv.app.data

import com.mytv.app.model.Channel
import com.mytv.app.model.EpgProgram
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object PlaybackRepository {

    private const val PLAYBACK_SERVER_BASE = "http://vivlar.me:80/live/497238/KK8Kdy/"
    private val timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

    // Mapeamento dos 72 canais que possuem arquivo de 7 dias na nuvem
    val PLAYBACK_STREAM_MAP: Map<String, String> = mapOf(
        // SporTV & Esportes
        "sportv_1" to "72332",
        "sportv_2" to "131176",
        "sportv_3" to "131113",
        "sportv_4" to "72332",
        "ch_4k_sportv" to "72332",
        "ch_h265_sportv" to "131176",
        "ch_bandsports" to "71803",
        "combate" to "71845",

        // Premiere
        "prem_clubes" to "72265",
        "prem_2" to "72239",
        "prem_3" to "72243",
        "prem_4" to "72247",
        "prem_5" to "72251",
        "prem_6" to "72255",
        "prem_7" to "72259",
        "prem_8" to "72265",
        "ch_4k_prem" to "72265",
        "ch_h265_prem" to "72239",

        // ESPN
        "ch_290" to "71922", // ESPN
        "ch_291" to "71922", // ESPN 2
        "ch_292" to "71909", // ESPN 3
        "ch_364" to "71960", // ESPN 4
        "ch_293" to "71955", // ESPN 5
        "ch_365" to "71955", // ESPN 6
        "espn_extra" to "71960",
        "ch_h265_espn" to "71922",

        // Telecine
        "tc_premium" to "72364",
        "tc_action" to "72348",
        "tc_touch" to "72368",
        "tc_pipoca" to "72360",
        "tc_fun" to "72356",
        "tc_cult" to "72352",
        "ch_4k_tc" to "72364",
        "ch_h265_tc" to "72348",

        // HBO
        "hbo_1" to "72120",
        "hbo_2" to "72120",
        "hbo_plus" to "72127",
        "hbo_family" to "72120",
        "hbo_signature" to "72120",
        "hbo_mundi" to "72120",
        "hbo_pop" to "72120",
        "hbo_xtreme" to "72120",
        "ch_4k_hbo" to "72120",
        "ch_h265_hbo" to "72120",

        // Filmes & Séries
        "warner_channel" to "72433",
        "megapix" to "72169",
        "ch_universal" to "72421",
        "ch_studio" to "72336",
        "ch_sony" to "71826",
        "ch_sony_movies" to "71826",
        "ch_cinemax" to "71835",
        "ch_space" to "72319",
        "ch_tnt" to "72378",
        "ch_tnt_novelas" to "72344",
        "ch_axn" to "71783",
        "ch_paramount" to "72219",

        // Documentários
        "ch_299" to "71865", // Discovery Channel
        "ch_discovery_kids" to "71873",
        "ch_296" to "71869", // Discovery H&H
        "ch_297" to "71885", // Discovery Turbo
        "disc_id" to "72152", // ID
        "disc_science" to "71877",
        "disc_theater" to "71881",
        "disc_world" to "71889",
        "ch_4k_disc" to "71881",
        "ch_animal_planet" to "71775",
        "disc_animal" to "71775",
        "disc_tlc" to "72373",
        "ch_history" to "72148",
        "ch_history_2" to "72108",

        // Infantil
        "ch_cartoon" to "71831",

        // Abertos & Notícias
        "globo_sp" to "72598",
        "globo_rj" to "131163",
        "globo_news" to "71999",
        "ch_h265_globo" to "72598",
        "sbt_sp" to "72598",
        "rec_sp" to "72973",
        "rec_rj" to "72594",
        "band_sp" to "72597",
        "band_news" to "71791",
        "cnn_brasil" to "71839",
        "jovem_pan_news" to "134024",
        "record_news" to "72272",

        // Variedades
        "var_canal_brasil" to "71818",
        "var_gnt" to "71818",
        "var_viagem" to "71818",

        // Adultos (+18)
        "ch_playboy" to "72222"
    )

    fun hasPlayback(channel: Channel): Boolean {
        return channel.hasPlayback || PLAYBACK_STREAM_MAP.containsKey(channel.id)
    }

    fun enrichChannelWithPlayback(channel: Channel): Channel {
        val streamId = PLAYBACK_STREAM_MAP[channel.id]
        if (streamId != null) {
            channel.hasPlayback = true
            channel.playbackDays = 7
            channel.playbackStreamId = streamId
        }
        return channel
    }

    fun getPlaybackUrl(channel: Channel, offsetSeconds: Long): String {
        val streamId = channel.playbackStreamId ?: PLAYBACK_STREAM_MAP[channel.id] ?: return channel.streamUrl
        val clampedOffset = offsetSeconds.coerceAtLeast(60).coerceAtMost(7 * 24 * 3600)
        return "${PLAYBACK_SERVER_BASE}${streamId}.m3u8?timeshift=${clampedOffset}"
    }

    fun getProgramPlaybackUrl(channel: Channel, program: EpgProgram): String {
        val nowSeconds = System.currentTimeMillis() / 1000
        val progSeconds = program.startTimestamp / 1000
        val offset = (nowSeconds - progSeconds).coerceAtLeast(60)
        return getPlaybackUrl(channel, offset)
    }

    /**
     * Retorna a lista de programas passados dos últimos dias para este canal
     */
    fun getProgramHistory(channel: Channel, daysAgo: Int = 0): List<EpgProgram> {
        val cal = Calendar.getInstance(timeZone)
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val dayFormat = SimpleDateFormat("dd/MM", Locale.getDefault()).apply { timeZone = PlaybackRepository.timeZone }
        val dayLabel = when (daysAgo) {
            0 -> "Hoje"
            1 -> "Ontem"
            else -> dayFormat.format(cal.time)
        }

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dayStartMillis = cal.timeInMillis

        val now = System.currentTimeMillis()
        val list = mutableListOf<EpgProgram>()

        for (slotIdx in EpgRepository.timeSlots.indices) {
            val slot = EpgRepository.timeSlots[slotIdx]
            val startMillis = dayStartMillis + (slot.first * 60 * 1000L)
            val endMillis = dayStartMillis + (slot.second * 60 * 1000L)

            // Só lista programas que já foram exibidos ou que estão em andamento
            if (startMillis < now) {
                val startH = String.format("%02d:%02d", slot.first / 60, slot.first % 60)
                val endH = String.format("%02d:%02d", (slot.second / 60) % 24, slot.second % 60)
                val meta = EpgRepository.getProgramMeta(channel, slotIdx)

                list.add(
                    EpgProgram(
                        title = "${meta.first} ($dayLabel)",
                        category = channel.category,
                        startTime = startH,
                        endTime = endH,
                        startTimestamp = startMillis,
                        endTimestamp = endMillis,
                        synopsis = meta.second.ifEmpty {
                            "Programa gravado na íntegra transmitido em $dayLabel no canal ${channel.title}. Disponível para assistir do início pelo Playback 7 Dias."
                        }
                    )
                )
            }
        }

        // Mais recentes primeiro
        return list.reversed()
    }
}
