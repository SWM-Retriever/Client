package org.retriever.dailypet.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentMyPageBinding
import org.retriever.dailypet.ui.base.BaseFragment

class MyPageFragment : BaseFragment<FragmentMyPageBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPageBinding {
        return FragmentMyPageBinding.inflate(inflater, container, false)
    }

    /*private fun onShareClicked() {
        val nickname = GlobalApplication.prefs.nickname

        Log.e("ABC", nickname.toString())

        val message = "[반려하루]\n$nickname 님이 00그룹의 초대장을 보냈어요\n" + R.string.invitation_message_text.toString()
        val code = message + "12345678"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, code)
        startActivity(Intent.createChooser(intent, "초대코드 공유하기"))
    }*/

}