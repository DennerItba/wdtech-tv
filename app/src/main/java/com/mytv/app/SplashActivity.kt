package com.mytv.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var txtStatus: TextView
    private lateinit var imgLogo: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private var hasNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_splash)

        txtStatus = findViewById(R.id.txt_splash_status)
        imgLogo = findViewById(R.id.img_splash_logo)

        // Exibe a versão atual dinamicamente a partir do pacote
        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "2.1.0"
        } catch (_: Exception) {
            "2.1.0"
        }
        findViewById<TextView>(R.id.txt_splash_version_badge)?.text = "VERSÃO $versionName"
        findViewById<TextView>(R.id.txt_splash_footer)?.text = "WDTECH TV v$versionName • 4K Ultra HD • Guia EPG Integrado"

        // Animação de entrada suave no logotipo futurista
        imgLogo.alpha = 0f
        imgLogo.scaleX = 0.8f
        imgLogo.scaleY = 0.8f
        imgLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(900)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Ciclo de mensagens de inicialização
        handler.postDelayed({
            txtStatus.text = "Carregando grade com 150+ canais..."
        }, 600)

        handler.postDelayed({
            txtStatus.text = "Sincronizando Guia Eletrônico de Programação (EPG)..."
        }, 1200)

        handler.postDelayed({
            txtStatus.text = "Tudo pronto! Entrando na WDTECH TV..."
        }, 1800)

        // Transição para a tela principal
        handler.postDelayed({
            navigateToMain()
        }, 2200)
    }

    private fun navigateToMain() {
        if (hasNavigated) return
        hasNavigated = true
        handler.removeCallbacksAndMessages(null)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Pressionar OK / Enter no controle remoto pula a tela de splash imediatamente
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            navigateToMain()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
