package org.retriever.dailypet
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, resources.getString(R.string.kakao_native_app_key))
    }
}

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val keyHash = Utility.getKeyHash(this)
        Log.e("Key ",keyHash)
        val context = this
        val kakaoLoginButton = findViewById<ImageButton>(R.id.btn_kakaoLogin)
        var naverLoginButton = findViewById<ImageButton>(R.id.btn_naverLogin)

        kakaoLoginButton.setOnClickListener {
            lateinit var token : OAuthToken
            lifecycleScope.launch {
                try {
                    token = UserApiClient.loginWithKakao(context)
                    Log.e("LoginActivity", "Token:  > $token")
                } catch (error: Throwable) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.d("LoginActivity", "사용자가 명시적으로 취소")
                    } else {
                        Log.e("LoginActivity", "인증 에러 발생", error)
                    }
                }
            }
            if(token != null) {
                val nextIntent = Intent(this, RegisterProfileActivity::class.java)
                startActivity(nextIntent)
            }
        }
        naverLoginButton.setOnClickListener{
            val nextIntent = Intent(this, RegisterProfileActivity::class.java)
            startActivity(nextIntent)
        }
    }
}

