package com.mytv.app.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val releaseNotes: String,
    val apkUrl: String
)

object AppUpdateManager {

    private const val TAG = "AppUpdateManager"
    const val GITHUB_UPDATE_URL = "https://raw.githubusercontent.com/denneritba/wdtech-tv/main/version.json"
    const val GITHUB_UPDATE_URL_FALLBACK = "https://raw.githubusercontent.com/denneritba/wdtech-tv/master/version.json"

    private var activeDownloadJob: Job? = null

    fun checkForUpdates(activity: Activity, isManualCheck: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentVersionCode = getCurrentVersionCode(activity)
            val currentVersionName = getCurrentVersionName(activity)

            var info: UpdateInfo? = null

            for (targetUrl in listOf(GITHUB_UPDATE_URL, GITHUB_UPDATE_URL_FALLBACK)) {
                try {
                    val url = URL(targetUrl)
                    val connection = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 6000
                        readTimeout = 6000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", "WDTECH-TV/2.1.3")
                        instanceFollowRedirects = true
                    }

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                        val json = JSONObject(responseText)
                        info = UpdateInfo(
                            versionCode = json.optInt("versionCode", 0),
                            versionName = json.optString("versionName", currentVersionName),
                            releaseNotes = json.optString("releaseNotes", "Novos canais e melhorias de transmissao."),
                            apkUrl = json.optString("apkUrl", "")
                        )
                        break
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Checagem: ${e.message}")
                }
            }

            withContext(Dispatchers.Main) {
                if (activity.isFinishing || activity.isDestroyed) return@withContext

                if (info != null && info.versionCode > currentVersionCode && info.apkUrl.isNotEmpty()) {
                    showUpdateAvailableDialog(activity, info, currentVersionName)
                }
            }
        }
    }

    private fun showUpdateAvailableDialog(activity: Activity, info: UpdateInfo, currentVersionName: String) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.mytv.app.R.layout.dialog_update_available)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val txtTitle = dialog.findViewById<TextView>(com.mytv.app.R.id.txt_update_title)
        val btnConfirm = dialog.findViewById<Button>(com.mytv.app.R.id.btn_update_confirm)
        val btnDismiss = dialog.findViewById<Button>(com.mytv.app.R.id.btn_update_dismiss)

        txtTitle?.text = "Existe uma nova atualização"

        btnConfirm?.apply {
            text = "SIM"
            setOnClickListener {
                dialog.dismiss()
                startDownloadAndInstall(activity, info)
            }
        }

        btnDismiss?.apply {
            text = "NÃO"
            setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
        btnConfirm?.requestFocus()
    }

    private fun startDownloadAndInstall(activity: Activity, info: UpdateInfo) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.mytv.app.R.layout.dialog_download_progress)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val txtSubtitle = dialog.findViewById<TextView>(com.mytv.app.R.id.txt_download_subtitle)
        val pbProgress = dialog.findViewById<ProgressBar>(com.mytv.app.R.id.pb_download_progress)
        val txtPercent = dialog.findViewById<TextView>(com.mytv.app.R.id.txt_download_percent)
        val txtBytes = dialog.findViewById<TextView>(com.mytv.app.R.id.txt_download_bytes)
        val btnCancel = dialog.findViewById<Button>(com.mytv.app.R.id.btn_download_cancel)

        txtSubtitle.text = "Baixando WDTECH TV v${info.versionName}..."

        btnCancel.setOnClickListener {
            activeDownloadJob?.cancel()
            dialog.dismiss()
            Toast.makeText(activity, "Download cancelado.", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
        btnCancel.requestFocus()

        activeDownloadJob = CoroutineScope(Dispatchers.IO).launch {
            var input: InputStream? = null
            var output: FileOutputStream? = null
            var connection: HttpURLConnection? = null

            try {
                val apkFile = File(
                    activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "wdtech_tv_${info.versionName}.apk"
                )

                if (apkFile.exists()) {
                    apkFile.delete()
                }

                var currentUrl = info.apkUrl
                var redirectCount = 0
                while (redirectCount < 6) {
                    val url = URL(currentUrl)
                    connection = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 15000
                        readTimeout = 30000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", "Mozilla/5.0 (Android TV; WDTECH)")
                        instanceFollowRedirects = true
                    }
                    val respCode = connection.responseCode
                    if (respCode == HttpURLConnection.HTTP_MOVED_PERM ||
                        respCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                        respCode == HttpURLConnection.HTTP_SEE_OTHER ||
                        respCode == 307 || respCode == 308) {
                        val location = connection.getHeaderField("Location")
                        if (!location.isNullOrEmpty()) {
                            currentUrl = location
                            connection.disconnect()
                            redirectCount++
                            continue
                        }
                    }
                    break
                }

                val conn = connection ?: throw Exception("Nao foi possivel conectar ao servidor de atualizacao.")
                val totalLength = conn.contentLength
                val inStream = conn.inputStream
                input = inStream
                output = FileOutputStream(apkFile)

                val buffer = ByteArray(8192)
                var downloaded: Long = 0
                var count: Int
                var lastUpdate = 0L

                while (inStream.read(buffer).also { count = it } != -1) {
                    output.write(buffer, 0, count)
                    downloaded += count

                    val now = System.currentTimeMillis()
                    if (now - lastUpdate > 100 || downloaded == totalLength.toLong()) {
                        lastUpdate = now
                        val percent = if (totalLength > 0) ((downloaded * 100) / totalLength).toInt() else 0
                        val mbDownloaded = downloaded / (1024f * 1024f)
                        val mbTotal = totalLength / (1024f * 1024f)

                        withContext(Dispatchers.Main) {
                            pbProgress.progress = percent
                            txtPercent.text = "$percent%"
                            txtBytes.text = String.format(Locale.US, "%.1f MB / %.1f MB", mbDownloaded, mbTotal)
                        }
                    }
                }

                output.flush()

                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    Toast.makeText(activity, "Download concluído! Abrindo instalador...", Toast.LENGTH_SHORT).show()
                    installApk(activity, apkFile)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro download: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    Toast.makeText(
                        activity,
                        "Não foi possível baixar a atualização pela nuvem.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                try {
                    input?.close()
                    output?.close()
                    connection?.disconnect()
                } catch (_: Exception) {}
            }
        }
    }

    fun installApk(context: Context, apkFile: File) {
        try {
            if (!apkFile.exists()) return
            val apkUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Falha instalador: ${e.message}")
        }
    }

    private fun getCurrentVersionCode(context: Context): Int {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            }
        } catch (e: Exception) { 0 }
    }

    private fun getCurrentVersionName(context: Context): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "2.1.3"
        } catch (e: Exception) { "2.1.3" }
    }
}
