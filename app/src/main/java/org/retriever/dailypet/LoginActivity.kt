package org.retriever.dailypet
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import org.retriever.dailypet.databinding.ActivityLoginBinding

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, resources.getString(R.string.kakao_native_app_key))
    }
}

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var TAG = "LOGIN"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)
        val context = this

        binding.btnKakaoLogin.setOnClickListener {
            var kakaoLogin : Boolean = false
            lifecycleScope.launch {
                try {
                    var token = UserApiClient.loginWithKakao(context)
                    Log.d(TAG, "Token:  > $token")
                    Intent(this@LoginActivity, RegisterProfileActivity::class.java).also{
                        startActivity(it)
                        finish()
                    }
                } catch (error: Throwable) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.d("LoginActivity", "사용자가 명시적으로 취소")
                    } else {
                        Log.e("LoginActivity", "인증 에러 발생", error)
                    }
                }
            }
        }
        binding.btnNaverLogin.setOnClickListener{
            val nextIntent = Intent(this, RegisterProfileActivity::class.java)
            startActivity(nextIntent)
        }
    }
}

