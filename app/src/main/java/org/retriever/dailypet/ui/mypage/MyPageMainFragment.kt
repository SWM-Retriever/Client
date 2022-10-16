package org.retriever.dailypet.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentMyPageMainBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

@AndroidEntryPoint
class MyPageMainFragment : BaseFragment<FragmentMyPageMainBinding>() {

    private val myPageViewModel by activityViewModels<MyPageViewModel>()
    private val nickname = GlobalApplication.prefs.nickname ?: ""
    private val groupName = GlobalApplication.prefs.groupName ?: ""
    private val invitationCode = GlobalApplication.prefs.invitationCode ?: ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPageMainBinding {
        return FragmentMyPageMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        buttonClick()
        observeWithdrawalResponse()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding) {
        groupInviteText.setOnClickListener {
            onShareClicked()
        }

        appReviewText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity("https://play.google.com/store/apps/details?id=com.dxx.firenow")
            root.findNavController().navigate(action)
        }

        notificationText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity(NOTIFICATION_URL)
            root.findNavController().navigate(action)
        }

        reportText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity(REPORT_URL)
            root.findNavController().navigate(action)
        }

        termsOfServiceText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity(TERMS_URL)
            root.findNavController().navigate(action)
        }

        privacyText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity(PRIVACY_URL)
            root.findNavController().navigate(action)
        }

        openSourceLicenseText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity(OPENSOURCE_URL)
            root.findNavController().navigate(action)
        }

        logoutButton.setOnClickListener {
            logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        withdrawalButton.setOnClickListener {
            withdrawal()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout() {
        GlobalApplication.prefs.initJwt()
        // 카카오 로그아웃
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("ABC", "카카오 로그아웃 실패", error)
                } else {
                    Toast.makeText(context, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                    Log.d("ABC", "카카오 로그아웃 성공")
                }
            }
        }
        // 네이버 로그아웃
        NaverIdLoginSDK.logout()
    }

    private fun onShareClicked() {
        val code = getString(R.string.invitation_message_text, nickname, groupName, invitationCode)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, code)
        startActivity(Intent.createChooser(intent, "초대코드 공유하기"))
    }

    private fun withdrawal() {
        var jwt = ""

        GlobalApplication.prefs.apply {
            jwt = this.jwt ?: ""

            this.initNickname()
            this.initJwt()
            this.initFamilyId()
            this.initGroupName()
            this.initInvitationCode()
            this.initGroupType()
            this.initProfileImageUrl()
        }
        myPageViewModel.deleteMemberWithdrawal(jwt)
    }

    private fun observeWithdrawalResponse() {
        myPageViewModel.withdrawalResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(binding.progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(binding.progressCircular)
                }
                is Resource.Error -> {
                    hideProgressCircular(binding.progressCircular)
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    companion object {
        private const val NOTIFICATION_URL = "https://showy-king-303.notion.site/2b97d48c4a434e019c1058800f7a48fe"
        private const val REPORT_URL = "https://the-form.io/forms/survey/response/fe418f0f-0ab2-46ce-80d1-d5a8188e5247"
        private const val TERMS_URL = "https://showy-king-303.notion.site/2b97d48c4a434e019c1058800f7a48fe"
        private const val PRIVACY_URL = "https://showy-king-303.notion.site/457fcbd6d0f84beb8367394d7f92094a"
        private const val OPENSOURCE_URL = "https://showy-king-303.notion.site/2074dab89c564325b7cba36291ce60f2"
    }

}