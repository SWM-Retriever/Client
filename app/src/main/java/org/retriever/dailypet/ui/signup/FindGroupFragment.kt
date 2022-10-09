package org.retriever.dailypet.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentFindGroupBinding
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.main.MainActivity
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding

class FindGroupFragment : BaseFragment<FragmentFindGroupBinding>() {

    private var isValidCode = false
    private var groupName = ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFindGroupBinding {
        return FragmentFindGroupBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding) {

        searchInviteButton.setOnClickListener {
            val inviteCode = inputCodeEdittext.text.toString()
            if (inviteCode.isBlank()) {
                inviteCodeValidateText.text = "유효하지 않은 초대코드입니다"
                inviteCodeValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                inputCodeEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                isValidCode = false
                setVisibility()
            } else {
                inviteCodeValidateText.text = "유효한 초대코드입니다"
                inviteCodeValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
                inputCodeEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
                isValidCode = true
                setVisibility()
                // TODO 가족 코드 검증 로직
            }
        }

        /* Register Family Member Page*/
        enterGroupButton.setOnClickListener {
            if (isValidCode) {
                val nextIntent = Intent(requireContext(), MainActivity::class.java)
                nextIntent.putExtra("FamilyName", groupName)
                startActivity(nextIntent) // 가족 구성원 등록 페이지로 이동
            }
        }

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }
    }

    private fun setVisibility() = with(binding) {
        if (isValidCode) {
            imgFamilyPhoto.visibility = View.VISIBLE
            groupNameText.visibility = View.VISIBLE
            enterGroupButton.visibility = Button.VISIBLE
            findGroupInvisibleCommentText.visibility = View.VISIBLE
        } else {
            imgFamilyPhoto.visibility = View.INVISIBLE
            groupNameText.visibility = View.INVISIBLE
            enterGroupButton.visibility = Button.INVISIBLE
            findGroupInvisibleCommentText.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val CODE_NICKNAME = 200
        private const val CODE_FAIL = 400
    }

}