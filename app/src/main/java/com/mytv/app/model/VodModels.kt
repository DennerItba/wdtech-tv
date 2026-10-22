package com.mytv.app.model

import java.io.Serializable

data class VodCategory(
    val id: String,
    val name: String,
    val isAdult: Boolean = false,
    val isSeries: Boolean = false
) : Serializable

data class VodItem(
    val id: String,
    val name: String,
    val posterUrl: String? = null,
    val rating: String? = null,
    val ratingScore: Float = 0f,
    val categoryId: String,
    val categoryName: String = "",
    val containerExtension: String = "mp4",
    val plot: String? = null,
    val year: String? = null,
    val genre: String? = null,
    val isSeries: Boolean = false,
    val streamUrl: String = ""
) : Serializable {

    fun getFormattedRating(): String {
        return if (!rating.isNullOrEmpty() && rating != "0") {
            "⭐ $rating"
        } else {
            "⭐ 8.0"
        }
    }

    fun toChannel(): Channel {
        val now = System.currentTimeMillis()
        return Channel(
            id = if (isSeries) "vod_ep_$id" else "vod_movie_$id",
            number = if (isSeries) "🍿" else "🎬",
            title = name,
            category = if (isSeries) "Séries • $categoryName" else "Filmes • $categoryName",
            streamUrl = streamUrl,
            logoUrl = posterUrl,
            description = plot ?: "Conteúdo em alta definição sob demanda.",
            quality = "FHD",
            isHls = streamUrl.contains(".m3u8"),
            currentProgram = EpgProgram(
                title = name,
                category = if (isSeries) "Séries" else "Filmes",
                startTime = year ?: "2025",
                endTime = getFormattedRating(),
                startTimestamp = now - 3600000L,
                endTimestamp = now + 7200000L,
                synopsis = plot ?: "Superprodução em alta definição sob demanda no WDTECH TV.",
                rating = "14+"
            )
        )
    }
}

data class VodEpisode(
    val id: String,
    val title: String,
    val episodeNum: Int,
    val seasonNum: Int,
    val streamUrl: String,
    val plot: String? = null,
    val duration: String? = null,
    val containerExtension: String = "mp4"
) : Serializable {

    fun getDisplayName(): String {
        return if (title.startsWith("S", ignoreCase = true) || title.startsWith("Ep", ignoreCase = true)) {
            title
        } else {
            "Episódio $episodeNum • $title"
        }
    }

    fun toChannel(seriesName: String, seriesPoster: String?): Channel {
        val now = System.currentTimeMillis()
        return Channel(
            id = "vod_ep_$id",
            number = "E$episodeNum",
            title = "$seriesName - ${getDisplayName()}",
            category = "Séries • Temporada $seasonNum",
            streamUrl = streamUrl,
            logoUrl = seriesPoster,
            description = plot ?: "Episódio $episodeNum da Temporada $seasonNum de $seriesName.",
            quality = "FHD",
            isHls = streamUrl.contains(".m3u8"),
            currentProgram = EpgProgram(
                title = "$seriesName (${getDisplayName()})",
                category = "Séries",
                startTime = "Temp $seasonNum",
                endTime = "Ep $episodeNum",
                startTimestamp = now - 3600000L,
                endTimestamp = now + 3600000L,
                synopsis = plot ?: "Episódio em alta definição de $seriesName.",
                rating = "14+"
            )
        )
    }
}

data class VodSeason(
    val seasonNum: Int,
    val title: String,
    val episodes: List<VodEpisode>
) : Serializable
