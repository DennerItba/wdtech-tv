package com.mytv.app.model

import java.io.Serializable

data class Channel(
    val id: String,
    val number: String,
    val title: String,
    val category: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val description: String? = null,
    val isHls: Boolean = true,
    val quality: String = "HD",
    var currentProgram: EpgProgram? = null,
    var nextProgram: EpgProgram? = null,
    var hasPlayback: Boolean = false,
    var playbackDays: Int = 0,
    var playbackStreamId: String? = null
) : Serializable

data class ChannelCategory(
    val id: String,
    val name: String,
    val iconRes: Int = 0,
    val isLocked: Boolean = false,
    val channels: List<Channel>
) : Serializable
