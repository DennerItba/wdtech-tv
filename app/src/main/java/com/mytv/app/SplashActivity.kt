package com.mytv.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mytv.app.data.ChannelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var splashRoot: View
    private lateinit var layoutLogoContainer: FrameLayout
    private lateinit var layoutBrandTitle: LinearLayout
    private lateinit var txtBrandSubtitle: TextView
    private lateinit var txtVersionBadge: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtStatus: TextView
    private lateinit var splashCenterLayout: LinearLayout
    private lateinit var txtFooter: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var hasNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_splash)

        bindViews()
        setupVersionInfo()
        setupReadyState()
        scheduleNavigation()
    }

    private fun bindViews() {
        splashRoot = findViewById(R.id.splash_root_layout)
        layoutLogoContainer = findViewById(R.id.layout_logo_container)
        layoutBrandTitle = findViewById(R.id.layout_brand_title)
        txtBrandSubtitle = findViewById(R.id.txt_brand_subtitle)
        txtVersionBadge = findViewById(R.id.txt_splash_version_badge)
        progressBar = findViewById(R.id.splash_progress)
        txtStatus = findViewById(R.id.txt_splash_status)
        splashCenterLayout = findViewById(R.id.splash_center_layout)
        txtFooter = findViewById(R.id.txt_splash_footer)
    }

    private fun setupVersionInfo() {
        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "3.2.2"
        } catch (_: Exception) {
            "3.2.2"
        }
        txtVersionBadge.text = "VERSÃO $versionName"
        txtFooter.text = "WDTECH TV v$versionName • 4K Ultra HD • Guia EPG Integrado • Leanback Edition"
    }

    private fun setupReadyState() {
        layoutLogoContainer.alpha = 1f
        layoutLogoContainer.scaleX = 1f
        layoutLogoContainer.scaleY = 1f
        layoutLogoContainer.translationY = 0f

        layoutBrandTitle.alpha = 1f
        layoutBrandTitle.translationY = 0f

        txtBrandSubtitle.alpha = 1f
        txtVersionBadge.alpha = 1f
        progressBar.alpha = 1f
        progressBar.progress = 85
        txtStatus.alpha = 1f
        txtStatus.text = "⚡ Carregando canais e catálogo..."
    }

    private fun scheduleNavigation() {
        // Pré-aquece a leitura de canais do assets em segundo plano (zero travamento na tela inicial)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ChannelRepository.getCategories()
            } catch (t: Throwable) {
                Log.e("WDTECH_SPLASH", "Erro no pré-carregamento em background", t)
            }
        }

        // Transição suave para a tela principal
        handler.postDelayed({
            navigateToMain()
        }, 750L)

        // Trava de segurança (Watchdog): nunca permite ficar preso na Splash
        handler.postDelayed({
            navigateToMain()
        }, 1500L)
    }

    private fun navigateToMain() {
        if (hasNavigated) return
        hasNavigated = true
        handler.removeCallbacksAndMessages(null)

        try {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } catch (t: Throwable) {
            Log.e("WDTECH_SPLASH", "Erro ao iniciar MainActivity", t)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Qualquer tecla pressionada no controle remoto avança imediatamente
        navigateToMain()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
