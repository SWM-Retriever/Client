package org.retriever.dailypet
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var imageLogo = findViewById<ImageView>(R.id.img_logo)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fadein)
        imageLogo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }, DURATION)
    }
    companion object {
        private const val DURATION : Long = 1500
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
}