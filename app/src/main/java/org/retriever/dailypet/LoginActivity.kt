/** @author Sehun Ahn **/

package org.retriever.dailypet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.kakao.sdk.auth.AuthApiClient
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
import org.retriever.dailypet.models.App
import org.retriever.dailypet.models.Member
import org.retriever.dailypet.models.Message
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var retrofit : Retrofit
    private lateinit var retrofitService : RetrofitService
    private val CODE_NEW_MEMBER = 401
    private val CODE_OTHER_DOMAIN = 400
    private val TAG = "LOGIN ACTIVITY"
    private var context = this
    private lateinit var BASE_URL : String
    private val KEY = "455e42b91cmshc6a9672a01080d5p13c40ajsn2e2c01284a4c"
    private val HOST = "dailypet.p.rapidapi.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init(){
        /* API Init */
        BASE_URL = getString(R.string.URL)
        Log.e(TAG, BASE_URL)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
    //        .addConverterFactory(ScalarsConverterFactory.create())
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

    private fun checkIsMember(name: String, email: String, domain: String){
        val call = retrofitService.postIsMember(Member(name, email, domain))
        call.enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) { // 이미 회원일때
                    /* jwt 발급 */
                    val jwt = "test"
                    App.prefs.token = jwt
                    val nextIntent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(nextIntent) // 메인 페이지
                }
                else{
                    when(response.code()){
                        CODE_NEW_MEMBER->{ // 신규 가입
                            val nextIntent = Intent(applicationContext, TermOfServiceActivity::class.java)
                            nextIntent.putExtra("userName",name)
                            nextIntent.putExtra("userEmail",email)
                            startActivity(nextIntent) // 프로필 등록 페이지
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                t.message?.let { Log.e(TAG, it) }
                Log.e(TAG, "API 통신 실패")
            }
        })
    }

    private fun kakaoLogin(){
        lifecycleScope.launch {
            try {
                val token = UserApiClient.loginWithKakao(context)
                Log.d(TAG, "Token:  > $token")
                /* 사용자 정보 요청*/
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        // 회원 확인 분기
                        val name = user.kakaoAccount?.profile?.nickname
                        val email = user.kakaoAccount?.email
                        if(name != null && email != null) checkIsMember(name, email, "KAKAO")
                        else Log.e(TAG, "카카오 프로필 조회 실패")
                    }
                }
            } catch (error: Throwable) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    Log.d(TAG, "사용자가 명시적으로 취소")
                } else {
                    Log.e(TAG, "인증 에러 발생", error)
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
                        checkIsMember(result.profile?.name.toString(),result.profile?.email.toString(),"NAVER")
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

