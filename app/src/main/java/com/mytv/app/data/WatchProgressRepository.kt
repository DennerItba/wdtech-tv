package com.mytv.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mytv.app.model.Channel
import com.mytv.app.model.EpgProgram
import java.io.Serializable

data class WatchProgress(
    val mediaId: String,           // Chave primária (ID do filme, série ou canal)
    val title: String,
    val category: String,
    val progressPercent: Int,      // De 0 a 100
    val positionMs: Long = 0L,     // Posição exata em milissegundos para retomar
    val durationMs: Long = 0L,     // Duração total em milissegundos
    val lastWatchedAt: Long = System.currentTimeMillis(), // Timestamp para ordenar
    val coverImage: String? = null, // URL do poster ou logo
    val streamUrl: String = "",    // URL de reprodução direta
    val isSeries: Boolean = false
) : Serializable {

    fun toChannel(): Channel {
        val now = System.currentTimeMillis()
        val safePos = if (positionMs > 0) positionMs else 60000L
        val safeDur = if (durationMs > safePos) durationMs else safePos + 60000L
        return Channel(
            id = mediaId,
            number = if (isSeries) "🍿" else "▶",
            title = title,
            category = category,
            streamUrl = streamUrl,
            logoUrl = coverImage,
            description = "Progresso salvo: $progressPercent% assistido.",
            quality = "FHD",
            isHls = streamUrl.contains(".m3u8"),
            progressPercent = progressPercent,
            resumePositionMs = positionMs,
            currentProgram = EpgProgram(
                title = title,
                category = category,
                startTime = "Retomar",
                endTime = "$progressPercent%",
                startTimestamp = now - safePos,
                endTimestamp = now + (safeDur - safePos),
                synopsis = "Progresso salvo: $progressPercent% assistido. Pressione OK para continuar assistindo."
            )
        )
    }
}

object WatchProgressRepository {

    private const val PREFS_NAME = "wdtechtv_watch_progress_prefs"
    private const val KEY_PROGRESS_MAP = "key_progress_map"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Synchronized
    fun saveProgress(
        context: Context,
        mediaId: String,
        title: String,
        category: String,
        progressPercent: Int,
        positionMs: Long = 0L,
        durationMs: Long = 0L,
        coverImage: String? = null,
        streamUrl: String = "",
        isSeries: Boolean = false
    ) {
        if (mediaId.isEmpty()) return

        val map = getAllProgressMap(context).toMutableMap()

        // Se assistiu mais de 95%, remove da fila de continuar assistindo
        if (progressPercent >= 95) {
            map.remove(mediaId)
        } else {
            val progress = WatchProgress(
                mediaId = mediaId,
                title = title,
                category = category,
                progressPercent = progressPercent.coerceIn(0, 100),
                positionMs = positionMs,
                durationMs = durationMs,
                lastWatchedAt = System.currentTimeMillis(),
                coverImage = coverImage,
                streamUrl = streamUrl,
                isSeries = isSeries
            )
            map[mediaId] = progress
        }

        saveAllProgressMap(context, map)
    }

    @Synchronized
    fun getProgress(context: Context, mediaId: String): WatchProgress? {
        val map = getAllProgressMap(context)
        return map[mediaId]
    }

    @Synchronized
    fun getContinueWatchingList(context: Context): List<WatchProgress> {
        val map = getAllProgressMap(context)
        return map.values.sortedByDescending { it.lastWatchedAt }
    }

    @Synchronized
    fun removeProgress(context: Context, mediaId: String) {
        val map = getAllProgressMap(context).toMutableMap()
        if (map.remove(mediaId) != null) {
            saveAllProgressMap(context, map)
        }
    }

    @Synchronized
    fun clearAll(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    private fun getAllProgressMap(context: Context): Map<String, WatchProgress> {
        val json = getPrefs(context).getString(KEY_PROGRESS_MAP, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, WatchProgress>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun saveAllProgressMap(context: Context, map: Map<String, WatchProgress>) {
        val json = gson.toJson(map)
        getPrefs(context).edit().putString(KEY_PROGRESS_MAP, json).apply()
    }
}
