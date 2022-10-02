package org.retriever.dailypet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.test.ui.login.LoginActivity
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentMyPageBinding
import org.retriever.dailypet.test.model.Resource
import org.retriever.dailypet.test.ui.login.LoginViewModel
import org.retriever.dailypet.test.ui.mypage.MyPageViewModel

@AndroidEntryPoint
class MyPageFragment : Fragment(), View.OnClickListener {

    private val myPageViewModel by activityViewModels<MyPageViewModel>()
    private val TAG = "MyPageFragment"
    private lateinit var binding: FragmentMyPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        init()
    }

    private fun init(){
        myPageViewModel.withdrawalResponse.observe(viewLifecycleOwner)    { response->
            when(response){
                is Resource.Loading ->{

                }
                is Resource.Success->{

                }
                is Resource.Error->{

                }
            }

        }
    }

    private fun setOnClickListener() {
        val btnLogout = binding.btnLogout
        btnLogout.setOnClickListener(this)
        val btnWithdrawal = binding.btnWithdrawal
        btnWithdrawal.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_logout -> {
                logout()
                activity?.let{
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.btn_withdrawal ->{
                withdrawal()
                activity?.let{
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    companion object {
        fun instance() = MyPageFragment()
    }

    private fun logout(){
        GlobalApplication.prefs.init()
        // 카카오 로그아웃
        if(AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "카카오 로그아웃 실패", error)
                } else {
                    Toast.makeText(context, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "카카오 로그아웃 성공")
                }
            }
        }
        // 네이버 로그아웃
        NaverIdLoginSDK.logout()
    }

    private fun withdrawal(){
        val jwt = GlobalApplication.prefs.jwt ?: ""
        GlobalApplication.prefs.init()
        Log.e("",jwt)
        myPageViewModel.deleteMemberWithdrawal(jwt)
    }

}