package com.mytv.app

import android.app.Application
import android.content.Context
import android.util.Log

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Trava global contra crashes inesperados (Watchdog de Estabilidade)
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("WDTECH_FATAL", "Exceção não tratada capturada na thread [${thread.name}]: ${throwable.message}", throwable)
            try {
                val prefs = getSharedPreferences("wdtech_crash_log", Context.MODE_PRIVATE)
                prefs.edit().putString("last_crash", throwable.stackTraceToString()).apply()
            } catch (_: Throwable) {}

            // Se for em thread secundária/coroutine, nunca encerra o app
            if (thread.name != "main") {
                Log.w("WDTECH_FATAL", "Crash prevenido na thread secundária: ${thread.name}")
                return@setDefaultUncaughtExceptionHandler
            }

            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
