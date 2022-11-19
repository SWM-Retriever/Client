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
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
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

    private var nickname = GlobalApplication.prefs.nickname ?: ""
    private var groupName = GlobalApplication.prefs.groupName ?: ""
    private var invitationCode = GlobalApplication.prefs.invitationCode ?: ""
    private var profileImageUrl = GlobalApplication.prefs.profileImageUrl ?: ""
    private var logoutDialog: MaterialAlertDialogBuilder? = null
    private var withdrawalDialog: MaterialAlertDialogBuilder? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPageMainBinding {
        return FragmentMyPageMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initProfile()
        initGroupType()
        buttonClick()
        observeWithdrawalResponse()
    }

    override fun onResume() {
        super.onResume()
        initInfo()
        initGroupType()
    }

    private fun initProfile() = with(binding) {
        userNickName.text = nickname
        if(profileImageUrl.isNotEmpty()){
            userCircleImage.load(profileImageUrl)
        }
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initGroupType() = with(binding) {
        if ((GlobalApplication.prefs.groupType ?: "") == FAMILY) {
            manageGroupText.visibility = View.VISIBLE
            underManageGroupDivide.visibility = View.VISIBLE
            groupInviteText.visibility = View.VISIBLE
            underGroupInviteDivide.visibility = View.VISIBLE
            groupMakeText.visibility = View.GONE
        } else {
            manageGroupText.visibility = View.GONE
            underManageGroupDivide.visibility = View.GONE
            groupInviteText.visibility = View.GONE
            underGroupInviteDivide.visibility = View.GONE
            groupMakeText.visibility = View.VISIBLE
        }
    }

    private fun initInfo(){
        nickname = GlobalApplication.prefs.nickname ?: ""
        groupName = GlobalApplication.prefs.groupName ?: ""
        invitationCode = GlobalApplication.prefs.invitationCode ?: ""
        profileImageUrl = GlobalApplication.prefs.profileImageUrl ?: ""
    }

    private fun buttonClick() = with(binding) {
        manageGroupText.setOnClickListener {
            root.findNavController().navigate(R.id.action_myPageMainFragment_to_myPageDetailActivity)
        }

        groupInviteText.setOnClickListener {
            onShareClicked()
        }

        groupMakeText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToCreateFamilyFragment2(true)
            root.findNavController().navigate(action)
        }

        appReviewText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity(PLAY_STORE_URL)
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

        marketingText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity(MARKETING_URL)
            root.findNavController().navigate(action)
        }

        openSourceLicenseText.setOnClickListener {
            val action = MyPageMainFragmentDirections.actionMyPageMainFragmentToWebViewActivity(OPENSOURCE_URL)
            root.findNavController().navigate(action)
        }

        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        withdrawalButton.setOnClickListener {
            showWithdrawalDialog()
        }
    }

    private fun showLogoutDialog() {
        logoutDialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialAlertDialog)
            .setTitle(getString(R.string.logout_dialog_title_text))
            .setMessage(getString(R.string.logout_dialog_message_text))
            .setNegativeButton(getString(R.string.dialog_no_text)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_yes_text)) { _, _ ->
                logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }

        logoutDialog?.show()
    }

    private fun showWithdrawalDialog() {
        withdrawalDialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialAlertDialog)
            .setTitle(getString(R.string.withdrawal_dialog_title_text))
            .setMessage(getString(R.string.withdrawal_dialog_message_text))
            .setNegativeButton(getString(R.string.dialog_no_text)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_yes_text)) { _, _ ->
                withdrawal()
                kakaoUnlink()
                naverUnlink()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }

        withdrawalDialog?.show()
    }

    private fun logout() {
        GlobalApplication.prefs.initJwt()
        // 카카오 로그아웃
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("ABC", "카카오 로그아웃 실패", error)
                } else {
                    Log.d("ABC", "카카오 로그아웃 성공")
                }
            }
        }
        // 네이버 로그아웃
        NaverIdLoginSDK.logout()
        Toast.makeText(context, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
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
                    if(response.code == canNotWithdrawal){
                        Toast.makeText(requireContext(), getString(R.string.can_not_withdrawal_comment), Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }


    private fun kakaoUnlink() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e(TAG, "카카오 연동 해제 실패", error)
            } else {
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

    override fun onDestroyView() {
        super.onDestroyView()

        logoutDialog = null
        withdrawalDialog = null
    }

    companion object {
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.retriever.dailypet"
        private const val NOTIFICATION_URL = "https://showy-king-303.notion.site/2b97d48c4a434e019c1058800f7a48fe"
        private const val REPORT_URL = "https://the-form.io/forms/survey/response/fe418f0f-0ab2-46ce-80d1-d5a8188e5247"
        private const val TERMS_URL = "https://showy-king-303.notion.site/df847ac24e894e4a837717776a7dd4b7"
        private const val PRIVACY_URL = "https://showy-king-303.notion.site/c3dd318460424ae5ae0d13ebef8cdc48"
        private const val MARKETING_URL = "https://showy-king-303.notion.site/25ae8e794dc44c6a801adcfb8850ea0f"
        private const val OPENSOURCE_URL = "https://showy-king-303.notion.site/719843c38acb40efb8efab7059a38564"
        private const val FAMILY = "FAMILY"
        private const val TAG = "MY_PAGE"
        private const val canNotWithdrawal = 403
    }

}