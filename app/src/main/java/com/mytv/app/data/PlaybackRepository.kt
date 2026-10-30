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
        "tc_premium" to "72362",
        "tc_action" to "72346",
        "tc_touch" to "72366",
        "tc_pipoca" to "72358",
        "tc_fun" to "72354",
        "tc_cult" to "72350",
        "ch_4k_tc" to "90237",
        "ch_h265_tc" to "72363",

        // HBO
        "hbo_main" to "72118",
        "hbo_1" to "72118",
        "hbo_2" to "72110",
        "hbo_plus" to "72125",
        "hbo_family" to "72114",
        "hbo_sig" to "72134",
        "hbo_signature" to "72134",
        "hbo_mundi" to "72121",
        "hbo_pop" to "72129",
        "hbo_xtreme" to "72138",
        "ch_4k_hbo" to "90247",
        "ch_h265_hbo" to "72119",

        // Filmes & Séries
        "warner_ch" to "72431",
        "warner_channel" to "72431",
        "megapix_ch" to "72166",
        "megapix" to "72166",
        "ch_universal_tv" to "72419",
        "ch_universal" to "72419",
        "ch_studio_universal" to "72334",
        "ch_studio" to "72334",
        "ch_sony_channel" to "71824",
        "ch_sony" to "71824",
        "ch_sony_movies" to "71824",
        "ch_cinemax" to "71833",
        "ch_space" to "72317",
        "ch_tnt" to "72375",
        "ch_284" to "72342",
        "ch_tnt_novelas" to "72342",
        "ch_axn" to "71781",
        "ch_paramount" to "72217",

        // Documentários
        "ch_299" to "71862", // Discovery Channel
        "ch_discovery_kids" to "71871",
        "ch_296" to "71867", // Discovery H&H
        "ch_297" to "71883", // Discovery Turbo
        "disc_id" to "72150", // ID
        "disc_science" to "71875",
        "disc_theater" to "71879",
        "disc_world" to "71887",
        "ch_4k_disc" to "103510",
        "ch_animal_planet" to "71772",
        "disc_animal" to "71772",
        "disc_tlc" to "72371",
        "ch_history" to "72146",
        "ch_history_2" to "72106",

        // Infantil
        "ch_cartoon" to "71828",
        
        // Variedades
        "var_multishow" to "131193",
        "var_gnt" to "72102",
        "var_viagem" to "72162",

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

        val list = mutableListOf<EpgProgram>()

        for (slotIdx in EpgRepository.timeSlots.indices) {
            val slot = EpgRepository.timeSlots[slotIdx]
            val startMillis = dayStartMillis + (slot.first * 60 * 1000L)
            val endMillis = dayStartMillis + (slot.second * 60 * 1000L)

            val startH = String.format("%02d:%02d", slot.first / 60, slot.first % 60)
            val endH = String.format("%02d:%02d", (slot.second / 60) % 24, slot.second % 60)
            val meta = EpgRepository.getProgramMeta(channel, slotIdx)

            list.add(
                EpgProgram(
                    title = meta.first,
                    category = channel.category,
                    startTime = startH,
                    endTime = endH,
                    startTimestamp = startMillis,
                    endTimestamp = endMillis,
                    synopsis = meta.second.ifEmpty {
                        "Transmissão gravada na íntegra no canal ${channel.title}. Disponível para assistir do início pelo Playback 7 Dias na nuvem."
                    }
                )
            )
        }

        // Retorna em ordem cronológica (manhã -> noite) conforme foto de referência
        return list
    }
}