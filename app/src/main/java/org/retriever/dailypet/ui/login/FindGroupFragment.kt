package org.retriever.dailypet.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentFindGroupBinding
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular

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

        btnSearchInviteCode.setOnClickListener {
            val inviteCode = editTextCode.text.toString()
            if (inviteCode.isBlank()) {
                textInviteCodeValidate.text = "올바른 코드를 입력해주세요"
                textInviteCodeValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                editTextCode.background = ContextCompat.getDrawable(requireContext(), R.drawable.fail_edittext)
                isValidCode = false
                setVisibility()
            } else {
                // TODO 가족 코드 검증 로직
            }
        }

        /* Register Family Member Page*/
        btnAddProfile.setOnClickListener {
            if (isValidCode) {
//                val nextIntent = Intent(requireContext(), EnterFamilyActivity::class.java)
//                nextIntent.putExtra("FamilyName", groupName)
//                startActivity(nextIntent) // 가족 구성원 등록 페이지로 이동
            }
        }
    }

    private fun setVisibility() = with(binding) {
        if (isValidCode) {
            imgFamilyPhoto.visibility = View.VISIBLE
            textFamilyName.visibility = View.VISIBLE
            btnAddProfile.visibility = Button.VISIBLE
            textInvisibleComment.visibility = View.VISIBLE
        } else {
            imgFamilyPhoto.visibility = View.INVISIBLE
            textFamilyName.visibility = View.INVISIBLE
            btnAddProfile.visibility = Button.INVISIBLE
            textInvisibleComment.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val CODE_NICKNAME = 200
        private const val CODE_FAIL = 400
    }

}