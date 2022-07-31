package org.retriever.dailypet
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
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
    private var context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.btnKakaoLogin.setOnClickListener {
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
                            //로그인 필요
                            kakaoLogin()
                        }
                        else {
                            //기타 에러
                        }
                    }
                    else {
                        //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                            if (error != null) {
                                Log.e(TAG, "토큰 정보 보기 실패", error)
                            }
                            else if (tokenInfo != null) {
                                Log.i(TAG, "토큰 정보 보기 성공" +
                                        "\n회원번호: ${tokenInfo.id}" +
                                        "\n만료시간: ${tokenInfo.expiresIn} 초")
                            }
                        }
                        val nextIntent = Intent(this, RegisterProfileActivity::class.java)
                        startActivity(nextIntent)
                    }
                }
            }
            else {
                //로그인 필요
                kakaoLogin()
            }

        }
        binding.btnNaverLogin.setOnClickListener{
            val nextIntent = Intent(this, RegisterProfileActivity::class.java)
            startActivity(nextIntent)
        }

        binding.btnLogout.setOnClickListener{
            // 카카오 로그아웃
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.d(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }
        }
    }

    private fun kakaoLogin(){
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
}

