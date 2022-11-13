package org.retriever.dailypet.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
    private lateinit var onBackCallBack: OnBackPressedCallback
    private var name = ""
    private var email = ""
    private var domain = ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCallBack()
        initLogin()
        buttonClick()
    }

    private fun initCallBack() {
        onBackCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackCallBack)
    }

    private fun initLogin() = with(binding) {
        hideProgressCircular(binding.progressCircular)

        loginViewModel.loginResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)

                        val nickName = response.data?.nickName ?: ""
                        val jwt = response.data?.jwtToken ?: ""
                        val familyId = response.data?.familyId ?: -1
                        val groupName = response.data?.familyName ?: ""
                        val invitationCode = response.data?.invitationCode ?: ""
                        val groupType = response.data?.groupType ?: ""
                        val profileImageUrl = response.data?.profileImageUrl ?: ""

                        saveSharedPreference(
                            nickName,
                            jwt,
                            familyId,
                            groupName,
                            invitationCode,
                            groupType,
                            profileImageUrl,
                        )
                        initProgress(jwt)
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        when (response.code) {
                            CODE_NEW_MEMBER -> {
                                val registerProfile = RegisterProfile(
                                    nickName = name,
                                    email = email,
                                    domain = domain,
                                    deviceToken = getDeviceToken(),
                                    profileImageUrl = ""
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
    }

    private fun saveSharedPreference(
        nickName: String,
        jwt: String,
        familyId: Int,
        familyName: String,
        invitationCode: String,
        groupType: String,
        profileImageUrl: String
    ) {
        GlobalApplication.prefs.apply {
            this.nickname = nickName
            this.jwt = jwt
            this.familyId = familyId
            this.groupName = familyName
            this.invitationCode = invitationCode
            this.groupType = groupType
            this.profileImageUrl = profileImageUrl
        }
    }

    private fun initProgress(jwt: String) = with(binding) {
        loginViewModel.getProgressStatus(jwt)

        loginViewModel.progressStatusResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
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
    }

    private fun getDeviceToken(): String {
        var deviceToken = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                deviceToken = task.result
                Log.d("ABC", deviceToken)
            }
        }
        return deviceToken
    }

    private fun buttonClick() = with(binding) {

        kakaoLoginButton.setOnClickListener {
            kakaoLogin()
        }

        naverLoginButton.setOnClickListener {
            naverLogin()
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

    companion object {
        private const val CODE_NEW_MEMBER = 401
        private const val CODE_OTHER_DOMAIN = 400
        private const val CODE_ERROR = 500
        private const val TAG = "LOGIN ACTIVITY"
        private const val KAKAO_DOMAIN = "KAKAO"
        private const val NAVER_DOMAIN = "NAVER"
    }

}

