package com.mytv.app.model

import java.io.Serializable

data class EpgProgram(
    val title: String,
    val category: String,
    val startTime: String,     // Ex: "20:30"
    val endTime: String,       // Ex: "21:45"
    val startTimestamp: Long,  // millis
    val endTimestamp: Long,    // millis
    val synopsis: String = "",
    val rating: String = "L"
) : Serializable {

    fun getProgressPercent(): Int {
        val now = System.currentTimeMillis()
        if (now <= startTimestamp) return 0
        if (now >= endTimestamp) return 100
        val total = endTimestamp - startTimestamp
        if (total <= 0) return 0
        return (((now - startTimestamp) * 100) / total).toInt().coerceIn(0, 100)
    }

    fun getRemainingMinutes(): Int {
        val now = System.currentTimeMillis()
        if (now >= endTimestamp) return 0
        return (((endTimestamp - now) / 60000)).toInt().coerceAtLeast(1)
    }

    fun getFormattedTimeRange(): String = "$startTime - $endTime"
}
