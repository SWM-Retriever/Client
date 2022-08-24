package org.retriever.dailypet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import org.retriever.dailypet.LoginActivity
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentHomeBinding
import org.retriever.dailypet.databinding.FragmentMyPageBinding
import org.retriever.dailypet.models.App

class MyPageFragment : Fragment(), View.OnClickListener {
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
    }

    private fun setOnClickListener() {
        val btnLogout = binding.btnLogout
        btnLogout.setOnClickListener(this)

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
        }
    }

    companion object {
        fun instance() = MyPageFragment()
    }

    private fun logout(){
        App.prefs.init()
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

}