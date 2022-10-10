package org.retriever.dailypet.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.databinding.FragmentMyPageBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.ui.mypage.MyPageViewModel

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>() {

    private val myPageViewModel by activityViewModels<MyPageViewModel>()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPageBinding {
        return FragmentMyPageBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        init()
    }

    private fun init() {
        myPageViewModel.withdrawalResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {

                }
                is Resource.Error -> {

                }
            }

        }
    }

    private fun setOnClickListener() {

        binding.btnLogout.setOnClickListener {
            logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnWithdrawal.setOnClickListener {
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

    private fun withdrawal() {
        val jwt = GlobalApplication.prefs.jwt ?: ""
        GlobalApplication.prefs.initJwt()
        GlobalApplication.prefs.initFamilyId()
        GlobalApplication.prefs.initPetIdList()
        Log.e("", jwt)
        myPageViewModel.deleteMemberWithdrawal(jwt)
    }

}