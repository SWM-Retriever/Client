package org.retriever.dailypet
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.launch
import org.retriever.dailypet.databinding.ActivityLoginBinding
import org.retriever.dailypet.interfaces.RetrofitService
import org.retriever.dailypet.models.PostTest
import org.retriever.dailypet.models.UserAccount
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, resources.getString(R.string.kakao_native_app_key))
        NaverIdLoginSDK.initialize(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), "반려하루")
    }
}

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var retrofit : Retrofit
    private lateinit var retrofitService : RetrofitService
    private var TAG = "LOGIN"
    private var context = this
    private var BASE_URL = "https://test11639.p.rapidapi.com/"
    private var KEY = ""
    private var HOST = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        /* API Init */
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(RetrofitService::class.java)

        /* 카카오 로그인 버튼 */
        binding.btnKakaoLogin.setOnClickListener {
            if (AuthApiClient.instance.hasToken()) {
                val nextIntent = Intent(this, RegisterProfileActivity::class.java)
                startActivity(nextIntent)
            } else {
                //로그인 필요
                kakaoLogin()
            }

        }
        /* 네이버 로그인 버튼 */
        binding.btnNaverLogin.setOnClickListener{
            naverLogin()
        }

        /* 로그아웃 버튼 */
        binding.btnLogout.setOnClickListener{
            // 카카오 로그아웃
            if(AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e(TAG, "카카오 로그아웃 실패", error)
                    } else {
                        Log.d(TAG, "카카오 로그아웃 성공")
                    }
                }
            }
            // 네이버 로그아웃
            NaverIdLoginSDK.logout()
        }
    }

    private fun postAccessToken(){
        val callPostTest = retrofitService.postTest(KEY, HOST, "ashpurple")
        callPostTest.enqueue(object : Callback<PostTest> {
            override fun onResponse(call: Call<PostTest>, response: Response<PostTest>) {
                if(response.isSuccessful) {
                    val result: String = response.body().toString()
                    Log.d("Test", result)
                    Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(applicationContext, "POST Response 실패", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PostTest>, t: Throwable) {
                Toast.makeText(applicationContext, "POST 실패", Toast.LENGTH_SHORT).show()
            }
        })
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

    private fun naverLogin(){
        val oAuthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                Log.d(TAG, "Naver Access Token : ${NaverIdLoginSDK.getAccessToken()}")
                Log.d(TAG, "Naver Refresh Token : ${NaverIdLoginSDK.getRefreshToken()}")
                Log.d(TAG, "Naver Expire Dates : ${NaverIdLoginSDK.getExpiresAt()}")
                // 네이버 로그인 API 호출 성공 시 유저 정보를 가져온다
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        Log.d(TAG, "네이버 로그인 유저 정보 - 이름 : ${result.profile?.name.toString()}")
                        Log.d(TAG, "네이버 로그인 유저 정보 - 이메일 : ${result.profile?.email.toString()}")
                        Intent(this@LoginActivity, RegisterProfileActivity::class.java).also{
                            startActivity(it)
                            finish()
                        }
                    }

                    override fun onError(errorCode: Int, message: String) {
                    }
                    override fun onFailure(httpStatus: Int, message: String) {
                    }

                })
            }
            override fun onError(errorCode: Int, message: String) {
            }
            override fun onFailure(httpStatus: Int, message: String) {
            }
        }
        NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
    }
}

