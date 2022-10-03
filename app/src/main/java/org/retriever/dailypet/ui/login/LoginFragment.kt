package org.retriever.dailypet.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.firebase.messaging.FirebaseMessaging
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
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentLoginBinding
import org.retriever.dailypet.loginWithKakao
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.login.Member
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private var name = ""
    private var email = ""
    private var domain = ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLogin()
        buttonClick()
    }

    private fun initLogin() = with(binding) {
        hideProgressCircular(binding.progressCircular)

        loginViewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    val jwt = response.data?.jwtToken ?: ""
                    GlobalApplication.prefs.jwt = jwt

                    initProgress(jwt)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    when (response.code) {
                        CODE_NEW_MEMBER -> {
                            val registerProfile = RegisterProfile(
                                nickname = name,
                                email = email,
                                domain = domain,
                                deviceToken = getDeviceToken()
                            )

                            val action = LoginFragmentDirections.actionLoginFragmentToTermOfServiceFragment(registerProfile)
                            root.findNavController().navigate(action)
                        }
                        CODE_OTHER_DOMAIN -> {
                            Toast.makeText(requireContext(), "다른 SNS로 가입된 이메일입니다\n가입된 SNS로 로그인해주세요", Toast.LENGTH_SHORT).show()
                        }
                        CODE_ERROR -> {
                            Toast.makeText(requireContext(), "API 서버 에러", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "SERVER ERROR")
                        }
                    }
                }
            }
        }
    }

    private fun initProgress(jwt: String) = with(binding) {
        loginViewModel.getProgressStatus(jwt)

        loginViewModel.progressStatusResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    when (response.data?.status) {
                        "PROFILE" -> root.findNavController().navigate(R.id.action_loginFragment_to_selectFamilyTypeFragment)
                        "GROUP" -> root.findNavController().navigate(R.id.action_loginFragment_to_createPetFragment)
                        else -> root.findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                    }
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun getDeviceToken(): String {
        var deviceToken = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                deviceToken = task.result
            }
        }

        return deviceToken
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
            GlobalApplication.prefs.jwtInit()
            // 카카오 로그아웃
            if (com.kakao.sdk.auth.AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e(TAG, "카카오 로그아웃 실패", error)
                    } else {
                        Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "카카오 로그아웃 성공")
                    }
                }
            }
            // 네이버 로그아웃
            NaverIdLoginSDK.logout()
        }

        /* 연동해제 버튼 */
        btnUnlink.setOnClickListener {
            GlobalApplication.prefs.jwtInit()
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
                val token = UserApiClient.loginWithKakao(requireContext())
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
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
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
        NaverIdLoginSDK.authenticate(requireContext(), oAuthLoginCallback)
    }

    private fun kakaoUnlink() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e(TAG, "연동 해제 실패", error)
            } else {
                Toast.makeText(requireContext(), "SNS 연동이 해제되었습니다", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "카카오 연동해제 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }

    private fun naverUnlink() {
        NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
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

