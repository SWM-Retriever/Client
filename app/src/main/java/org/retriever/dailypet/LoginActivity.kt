/** @author Sehun Ahn **/

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
import com.kakao.sdk.common.model.ApplicationContextInfo
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.launch
import org.retriever.dailypet.databinding.ActivityLoginBinding
import org.retriever.dailypet.interfaces.RetrofitService
import org.retriever.dailypet.models.Message
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
    private var CODE_MEMBER = 200
    private var CODE_NOT_MEMBER = 400
    private var TAG = "LOGIN"
    private var context = this
    private var BASE_URL = "https://dailypet.p.rapidapi.com/"
    private var KEY = "455e42b91cmshc6a9672a01080d5p13c40ajsn2e2c01284a4c"
    private var HOST = "dailypet.p.rapidapi.com"

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
            kakaoLogin()
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
                        Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "카카오 로그아웃 성공")
                    }
                }
            }
            // 네이버 로그아웃
            NaverIdLoginSDK.logout()
        }

        /* 연동해제 버튼 */
        binding.btnUnlink.setOnClickListener{
            // 카카오 연동해제
            kakaoUnlink()
            // 네이버 연동해제
            naverUnlink()
        }
    }

    private fun checkIsMember(name: String?, email: String?){
        val callPostIsMember = retrofitService.postIsMember(KEY, HOST, "sehun", "aaa")
        callPostIsMember.enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == CODE_MEMBER){ // 이미 회원일때
                        // jwt 인증
                    }
                }
                else{
                    if(response.code() == CODE_NOT_MEMBER){ // 신규 가입일때
                        val nextIntent = Intent(applicationContext, RegisterProfileActivity::class.java)
                        nextIntent.putExtra("userName",name)
                        nextIntent.putExtra("userEmail",email)
                        startActivity(nextIntent)
                    }
                }
            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                Log.e(TAG, "연결 실패")
            }
        })
    }

    private fun kakaoLogin(){
        lifecycleScope.launch {
            try {
                var token = UserApiClient.loginWithKakao(context)
                Log.d(TAG, "Token:  > $token")
                /* 사용자 정보 요청*/
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        // 회원 확인 분기
                        checkIsMember(user.kakaoAccount?.profile?.nickname, user.kakaoAccount?.email)
                    }
                }
//                Intent(this@LoginActivity, RegisterProfileActivity::class.java).also{
//                    startActivity(it)
//                    finish()
//                }
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
                        // 회원 확인 분기
                        checkIsMember(result.profile?.name.toString(),result.profile?.email.toString())
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

    private fun kakaoUnlink(){
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e(TAG, "연동 해제 실패", error)
            }
            else {
                Toast.makeText(this, "SNS 연동이 해제되었습니다", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "카카오 연동해제 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }
    private fun naverUnlink(){
        NidOAuthLogin().callDeleteTokenApi(context, object : OAuthLoginCallback {
            override fun onSuccess() {
                //서버에서 토큰 삭제에 성공한 상태입니다.
                Log.d(TAG, "네이버 연동해제 성공")
            }
            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없음
                Log.d(TAG, "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.d(TAG, "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없음
                onFailure(errorCode, message)
            }
        })
    }
}

