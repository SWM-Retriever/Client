package org.retriever.dailypet.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import org.retriever.dailypet.R
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageLogo = findViewById<ImageView>(R.id.img_logo)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fadein)
        imageLogo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        if (error is KakaoSdkError && error.isInvalidTokenError()) {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else {
                            //기타 에러
                        }
                    }
                    else {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, DURATION)
    }
    companion object {
        private const val DURATION : Long = 1500
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
}