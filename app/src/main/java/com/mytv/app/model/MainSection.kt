package com.mytv.app.model

import com.mytv.app.R
import java.io.Serializable

enum class MainSection(val id: String, val title: String, val iconRes: Int) : Serializable {
    TV("sec_tv", "TV", R.drawable.ic_tv_aberta),
    FILMES("sec_filmes", "FILMES", R.drawable.ic_movie_vod),
    SERIES("sec_series", "SERIES", R.drawable.ic_series_vod),
    INFANTIL("sec_infantil", "INFANTIL", R.drawable.ic_star_kids),
    FUTEBOL("sec_futebol", "FUTEBOL", R.drawable.ic_sports_tv)
}
