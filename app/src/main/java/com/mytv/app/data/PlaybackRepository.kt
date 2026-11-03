package com.mytv.app.data

import com.mytv.app.model.Channel
import com.mytv.app.model.EpgProgram
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object PlaybackRepository {

    private const val PLAYBACK_SERVER_BASE = "http://64.31.23.207:80/live/59471019/60849037/"
    private val timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

    // Mapeamento dos 72 canais que possuem arquivo de 7 dias na nuvem
    val PLAYBACK_STREAM_MAP: Map<String, String> = mapOf(
        // Globo (Principal, Backups e Regionais)
        "globo_sp" to "5298295",
        "globo_sp_fhd" to "5298295",
        "ch_158" to "5298295",
        "ch_h265_globo" to "5298295",
        "globo_sp_backup" to "5298258",
        "globo_sp_4k" to "5676596",
        "globo_nac_fhd" to "5299056",
        "globo_rj" to "5298281",
        "globo_mg" to "5298256",
        "globo_ba" to "5299064",
        "globo_df" to "5299093",
        "globo_rbs" to "5299120",
        "globo_pr" to "5298290",
        "globo_news" to "5298077",

        // SporTV & Fórmula 1 Oficial
        "sportv_1" to "3460200",
        "sportv_fhd" to "3460200",
        "ch_f1" to "3460200",
        "ch_4k_sportv" to "5676601",
        "ch_h265_sportv" to "5676601",
        "sportv_2" to "3460203",
        "sportv_3" to "3460206",
        "ch_bandsports" to "3460152",
        "combate" to "8714",

        // Telecine (IDs Corrigidos e 100% Funcionais)
        "tc_premium" to "5378594",
        "tc_action" to "5378580",
        "tc_touch" to "5378597",
        "tc_pipoca" to "5378590",
        "tc_fun" to "5378586",
        "tc_cult" to "5378583",
        "ch_4k_tc" to "5676607",
        "ch_h265_tc" to "5676607",

        // Viva / Globoplay Novelas
        "var_viva" to "518595",

        // NASA / Ciência / Cosmos
        "ch_4k_nasa" to "5378645",
        "ch_space" to "5378645",
        "ch_4k_disc" to "5676591",
        "ch_discovery" to "5676591",

        // HBO
        "hbo_main" to "514107",
        "hbo_1" to "514107",
        "hbo_2" to "514107",
        "ch_4k_hbo" to "514107",
        "ch_h265_hbo" to "514107",

        // Abertos & Notícias
        "sbt_sp" to "5298267",
        "rec_sp" to "5298246",
        "band_sp" to "5298244",
        "cnn_brasil" to "71839",
        "jovem_pan_news" to "134024"
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