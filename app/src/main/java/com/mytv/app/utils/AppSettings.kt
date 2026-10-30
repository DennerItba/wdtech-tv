package com.mytv.app.utils

import android.content.Context
import android.os.Build
import androidx.media3.ui.AspectRatioFrameLayout
import coil.Coil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object AppSettings {

    private const val PREFS_NAME = "wdtech_tv_settings"
    private const val KEY_BUFFER_MODE = "buffer_mode"
    private const val KEY_ASPECT_RATIO = "aspect_ratio"
    private const val KEY_PARENTAL_PIN = "parental_pin"

    // Buffer Modes
    const val BUFFER_FAST = "FAST"           // 4s
    const val BUFFER_BALANCED = "BALANCED"   // 12s
    const val BUFFER_STABLE = "STABLE"       // 25s

    fun getBufferMode(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_BUFFER_MODE, BUFFER_BALANCED) ?: BUFFER_BALANCED
    }

    fun cycleBufferMode(context: Context): String {
        val current = getBufferMode(context)
        val next = when (current) {
            BUFFER_BALANCED -> BUFFER_STABLE
            BUFFER_STABLE -> BUFFER_FAST
            else -> BUFFER_BALANCED
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BUFFER_MODE, next)
            .apply()
        return next
    }

    fun getBufferLabel(mode: String): String {
        return when (mode) {
            BUFFER_FAST -> "Rápido (4s)"
            BUFFER_STABLE -> "Anti-Travamento (25s)"
            else -> "Equilibrado (12s)"
        }
    }

    fun getAspectRatio(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_ASPECT_RATIO, AspectRatioFrameLayout.RESIZE_MODE_FIT)
    }

    fun cycleAspectRatio(context: Context): Int {
        val current = getAspectRatio(context)
        val next = when (current) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_ASPECT_RATIO, next)
            .apply()
        return next
    }

    fun getAspectRatioLabel(mode: Int): String {
        return when (mode) {
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> "Zoom (Sem Bordas)"
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> "Esticar (Preencher)"
            else -> "Original (16:9)"
        }
    }

    fun getParentalPin(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PARENTAL_PIN, "0000") ?: "0000"
    }

    fun setParentalPin(context: Context, newPin: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PARENTAL_PIN, newPin)
            .apply()
    }

    @OptIn(coil.annotation.ExperimentalCoilApi::class)
    suspend fun clearCache(context: Context): Long = withContext(Dispatchers.IO) {
        var bytesFreed = 0L
        try {
            // Limpa cache Coil
            Coil.imageLoader(context).diskCache?.clear()
            Coil.imageLoader(context).memoryCache?.clear()

            // Limpa diretórios temporários
            val cacheDirs = listOfNotNull(context.cacheDir, context.externalCacheDir)
            for (dir in cacheDirs) {
                bytesFreed += deleteDir(dir)
            }
            System.gc()
        } catch (_: Exception) {}
        bytesFreed
    }

    private fun deleteDir(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var size = 0L
        val children = dir.listFiles() ?: return 0L
        for (file in children) {
            if (file.isDirectory) {
                size += deleteDir(file)
            } else {
                size += file.length()
                file.delete()
            }
        }
        return size
    }

    suspend fun testConnectionLatency(): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val testUrl = "http://64.31.23.207:80/player_api.php?username=59471019&password=60849037"
        val start = System.currentTimeMillis()
        try {
            val url = URL(testUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "HEAD"
                setRequestProperty("User-Agent", "IPTVSmartersPro")
            }
            val code = conn.responseCode
            val elapsed = System.currentTimeMillis() - start
            val ok = (code in 200..399)
            Pair(ok, elapsed)
        } catch (_: Exception) {
            Pair(false, -1L)
        }
    }

    fun getSystemInfo(): String {
        val model = Build.MODEL
        val brand = Build.BRAND
        val androidVer = Build.VERSION.RELEASE
        val sdk = Build.VERSION.SDK_INT
        return "Dispositivo: $brand $model • Android $androidVer (API $sdk)\nServidor CDN: 64.31.23.207 (Porta 80) • Status: Ativo"
    }
}
