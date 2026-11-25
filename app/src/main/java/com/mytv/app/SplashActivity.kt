package com.mytv.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
            packageManager.getPackageInfo(packageName, 0).versionName ?: "2.9.9"
        } catch (_: Exception) {
            "2.9.9"
        }
        txtVersionBadge.text = "VERSÃO $versionName"
        txtFooter.text = "WDTECH TV v$versionName • 4K Ultra HD • Guia EPG Integrado • Leanback Edition"
    }

    private fun setupReadyState() {
        // Garante que TODOS os elementos fiquem 100% visíveis imediatamente
        // Evita qualquer tela preta por animações ou alpha zerado em Android TV
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
        // Transição suave e ultra rápida para a tela principal (sem travamento)
        handler.postDelayed({
            navigateToMain()
        }, 650L)

        // Trava de segurança absoluta (Watchdog): nunca permite ficar preso na Splash
        handler.postDelayed({
            navigateToMain()
        }, 1200L)
    }

    private fun navigateToMain() {
        if (hasNavigated) return
        hasNavigated = true
        handler.removeCallbacksAndMessages(null)

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
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
