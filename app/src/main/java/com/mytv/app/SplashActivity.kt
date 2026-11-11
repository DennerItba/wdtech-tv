package com.mytv.app

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var splashRoot: View
    private lateinit var viewAura: View
    private lateinit var layoutLogoContainer: FrameLayout
    private lateinit var viewHalo: View
    private lateinit var viewShimmer: View
    private lateinit var layoutBrandTitle: LinearLayout
    private lateinit var txtBrandSubtitle: TextView
    private lateinit var txtVersionBadge: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtStatus: TextView
    private lateinit var splashCenterLayout: LinearLayout
    private lateinit var txtFooter: TextView

    private val animators = mutableListOf<ValueAnimator>()
    private val handler = Handler(Looper.getMainLooper())
    private var hasNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_splash)

        bindViews()
        setupVersionInfo()
        setupInitialStates()
        startCinematicOpening()
    }

    private fun bindViews() {
        splashRoot = findViewById(R.id.splash_root_layout)
        viewAura = findViewById(R.id.view_splash_aura)
        layoutLogoContainer = findViewById(R.id.layout_logo_container)
        viewHalo = findViewById(R.id.view_splash_halo)
        viewShimmer = findViewById(R.id.view_splash_shimmer)
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
            packageManager.getPackageInfo(packageName, 0).versionName ?: "2.7.2"
        } catch (_: Exception) {
            "2.7.2"
        }
        txtVersionBadge.text = "VERSÃO $versionName"
        txtFooter.text = "WDTECH TV v$versionName • 4K Ultra HD • Guia EPG Integrado • Leanback Edition"
    }

    private fun setupInitialStates() {
        // Estado inicial de entrada com transparência e posições preparatórias
        layoutLogoContainer.alpha = 0f
        layoutLogoContainer.scaleX = 0.25f
        layoutLogoContainer.scaleY = 0.25f
        layoutLogoContainer.translationY = -40f

        viewHalo.alpha = 0f

        layoutBrandTitle.alpha = 0f
        layoutBrandTitle.translationY = 30f

        txtBrandSubtitle.alpha = 0f
        txtVersionBadge.alpha = 0f
        progressBar.alpha = 0f
        txtStatus.alpha = 0f
    }

    private fun startCinematicOpening() {
        // --- 1. Pulsação Contínua da Aura de Fundo (Breathing Glow) ---
        val auraAnim = ValueAnimator.ofFloat(0.9f, 1.15f).apply {
            duration = 1800
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val v = anim.animatedValue as Float
                viewAura.scaleX = v
                viewAura.scaleY = v
                viewAura.alpha = 0.40f + (v - 0.9f) * 1.6f
            }
        }
        auraAnim.start()
        animators.add(auraAnim)

        // --- 2. Halo Orbital de Expansão Contínua (Neon Ripple) ---
        val haloAnim = ValueAnimator.ofFloat(0.85f, 1.35f).apply {
            duration = 1500
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = DecelerateInterpolator()
            addUpdateListener { anim ->
                val scale = anim.animatedValue as Float
                viewHalo.scaleX = scale
                viewHalo.scaleY = scale
                // Esmaece conforme se expande
                val fraction = anim.animatedFraction
                viewHalo.alpha = (1f - fraction) * 0.85f
            }
        }
        haloAnim.start()
        animators.add(haloAnim)

        // --- 3. Entrada Explosiva 3D com Mola do Emblema Futurista (0 a 850ms) ---
        layoutLogoContainer.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setDuration(850)
            .setInterpolator(OvershootInterpolator(1.35f))
            .start()

        // --- 4. Shimmer / Varredura de Feixe de Luz Transversal (950ms) ---
        handler.postDelayed({
            if (isFinishing || hasNavigated) return@postDelayed
            viewShimmer.visibility = View.VISIBLE
            viewShimmer.translationX = -130f
            viewShimmer.alpha = 0f
            viewShimmer.animate()
                .alpha(0.85f)
                .translationX(130f)
                .setDuration(550)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    viewShimmer.visibility = View.GONE
                }
                .start()
        }, 900)

        // --- 5. Revelação da Tipografia e Identidade Visual (700ms a 1400ms) ---
        handler.postDelayed({
            if (isFinishing || hasNavigated) return@postDelayed
            layoutBrandTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }, 650)

        handler.postDelayed({
            if (isFinishing || hasNavigated) return@postDelayed
            txtBrandSubtitle.animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }, 950)

        handler.postDelayed({
            if (isFinishing || hasNavigated) return@postDelayed
            txtVersionBadge.animate()
                .alpha(1f)
                .setDuration(450)
                .start()
        }, 1150)

        handler.postDelayed({
            if (isFinishing || hasNavigated) return@postDelayed
            progressBar.animate()
                .alpha(1f)
                .setDuration(400)
                .start()
            txtStatus.animate()
                .alpha(1f)
                .setDuration(400)
                .start()

            // Preenchimento Suave da Barra de Progresso (0 -> 100%)
            val progressAnim = ObjectAnimator.ofInt(progressBar, "progress", 0, 100).apply {
                duration = 1600
                interpolator = AccelerateDecelerateInterpolator()
            }
            progressAnim.start()
            animators.add(progressAnim)
        }, 1250)

        // --- 6. Progressão Dinâmica das Mensagens de Status ---
        handler.postDelayed({
            updateStatusText("⚡ Conectando ao cluster de transmissão seguro...")
        }, 300)

        handler.postDelayed({
            updateStatusText("🚀 Carregando canais em alta definição...")
        }, 1100)

        handler.postDelayed({
            updateStatusText("✨ Sincronizando grade e Guia EPG...")
        }, 1850)

        handler.postDelayed({
            updateStatusText("🔥 Bem-vindo ao WDTECH TV!")
        }, 2500)

        // --- 7. Transição Cinematográfica de Saída (Zoom-Through) ---
        handler.postDelayed({
            if (isFinishing || hasNavigated) return@postDelayed
            splashCenterLayout.animate()
                .scaleX(1.18f)
                .scaleY(1.18f)
                .alpha(0f)
                .setDuration(380)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    navigateToMain()
                }
                .start()
        }, 2850)
    }

    private fun updateStatusText(newText: String) {
        if (isFinishing || hasNavigated) return
        txtStatus.animate()
            .alpha(0.2f)
            .setDuration(120)
            .withEndAction {
                txtStatus.text = newText
                txtStatus.animate()
                    .alpha(1f)
                    .setDuration(160)
                    .start()
            }
            .start()
    }

    private fun navigateToMain() {
        if (hasNavigated) return
        hasNavigated = true
        handler.removeCallbacksAndMessages(null)
        animators.forEach { it.cancel() }
        animators.clear()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Pressionar OK / Enter no controle remoto pula a animação imediatamente
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
            keyCode == KeyEvent.KEYCODE_ENTER ||
            keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER
        ) {
            navigateToMain()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        animators.forEach { it.cancel() }
        animators.clear()
    }
}
