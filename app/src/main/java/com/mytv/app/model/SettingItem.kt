package com.mytv.app.model

data class SettingItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    var badgeText: String,
    val description: String,
    var actionButtonText: String
)
