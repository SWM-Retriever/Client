/** @author Sehun Ahn **/

package org.retriever.dailypet.test.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.MainActivity
import org.retriever.dailypet.TermOfServiceActivity
import org.retriever.dailypet.databinding.ActivityLoginBinding
import org.retriever.dailypet.loginWithKakao
import org.retriever.dailypet.test.model.Resource
import org.retriever.dailypet.test.model.login.Member
import org.retriever.dailypet.test.ui.base.BaseActivity
import org.retriever.dailypet.test.util.hideProgressCircular
import org.retriever.dailypet.test.util.showProgressCircular

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>({ ActivityLoginBinding.inflate(it) }) {

    private val loginViewModel by viewModels<LoginViewModel>()

    private var name = ""
    private var email = ""
    private var domain = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        buttonClick()
    }

    private fun initView() = with(binding) {
        hideProgressCircular(binding.progressCircular)

        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    val jwt = response.data?.jwtToken
                    GlobalApplication.prefs.jwt = jwt
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    when (response.code) {
                        CODE_NEW_MEMBER -> {
                            val nextIntent =
                                Intent(this@LoginActivity, TermOfServiceActivity::class.java)
                            nextIntent.putExtra("userName", name)
                            nextIntent.putExtra("userEmail", email)
                            nextIntent.putExtra("domain", domain)
                            startActivity(nextIntent) // 프로필 등록 페이지
                        }
                        CODE_OTHER_DOMAIN -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "다른 SNS로 가입된 이메일입니다\n가입된 SNS로 로그인해주세요",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        CODE_ERROR -> {
                            Toast.makeText(this@LoginActivity, "API 서버 에러", Toast.LENGTH_SHORT)
                                .show()
                            Log.e(TAG, "SERVER ERROR")
                        }
                    }
                }
            }
        }
    }

    private fun buttonClick() = with(binding) {
        /* 카카오 로그인 버튼 */
        btnKakaoLogin.setOnClickListener {
            kakaoLogin()
        }
        /* 네이버 로그인 버튼 */
        btnNaverLogin.setOnClickListener {
            naverLogin()
        }
        /* 로그아웃 버튼 */
        btnLogout.setOnClickListener {
            GlobalApplication.prefs.init()
            // 카카오 로그아웃
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e(TAG, "카카오 로그아웃 실패", error)
                    } else {
                        Toast.makeText(this@LoginActivity, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "카카오 로그아웃 성공")
                    }
                }
            }
            // 네이버 로그아웃
            NaverIdLoginSDK.logout()
        }

        /* 연동해제 버튼 */
        btnUnlink.setOnClickListener {
            GlobalApplication.prefs.init()
            // 카카오 연동해제
            kakaoUnlink()
            // 네이버 연동해제
            naverUnlink()
        }
    }

    private fun postIsMember(name: String, email: String, domain: String) {
        loginViewModel.postIsMember(Member(name, email, domain))
    }

    private fun kakaoLogin() {
        lifecycleScope.launch {
            try {
                val token = UserApiClient.loginWithKakao(this@LoginActivity)
                Log.d(TAG, "Token:  > $token")
                /* 사용자 정보 요청*/
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        // 회원 확인 분기
                        name = user.kakaoAccount?.profile?.nickname ?: ""
                        email = user.kakaoAccount?.email ?: ""
                        domain = KAKAO_DOMAIN

                        if (name.isNotBlank() && email.isNotBlank()) {
                            postIsMember(name, email, "KAKAO")
                        } else {
                            Log.e(TAG, "카카오 프로필 조회 실패")
                        }
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

    private fun naverLogin() {
        val oAuthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                Log.d(TAG, "Naver Access Token : ${NaverIdLoginSDK.getAccessToken()}")
                Log.d(TAG, "Naver Refresh Token : ${NaverIdLoginSDK.getRefreshToken()}")
                Log.d(TAG, "Naver Expire Dates : ${NaverIdLoginSDK.getExpiresAt()}")
                // 네이버 로그인 API 호출 성공 시 유저 정보를 가져온다
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        // 회원 확인 분기
                        name = result.profile?.name ?: ""
                        email = result.profile?.email ?: ""
                        domain = NAVER_DOMAIN
                        postIsMember(name, email, domain)
                    }

                    override fun onError(errorCode: Int, message: String) = Unit
                    override fun onFailure(httpStatus: Int, message: String) = Unit
                })
            }

            override fun onError(errorCode: Int, message: String) = Unit
            override fun onFailure(httpStatus: Int, message: String) = Unit
        }
        NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
    }

    private fun kakaoUnlink() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e(TAG, "연동 해제 실패", error)
            } else {
                Toast.makeText(this, "SNS 연동이 해제되었습니다", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "카카오 연동해제 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }

    private fun naverUnlink() {
        NidOAuthLogin().callDeleteTokenApi(this, object : OAuthLoginCallback {
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

    companion object {
        private const val CODE_NEW_MEMBER = 401
        private const val CODE_OTHER_DOMAIN = 400
        private const val CODE_ERROR = 500
        private const val TAG = "LOGIN ACTIVITY"
        private const val KAKAO_DOMAIN = "KAKAO"
        private const val NAVER_DOMAIN = "NAVER"
    }
}

