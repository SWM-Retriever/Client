package org.retriever.dailypet.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentFindGroupBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.bottomsheet.AddProfileBottomSheet
import org.retriever.dailypet.ui.signup.viewmodel.FindGroupViewModel
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular

class FindGroupFragment : BaseFragment<FragmentFindGroupBinding>() {

    private val findGroupViewModel by activityViewModels<FindGroupViewModel>()
    private var isValidCode = false
    private var groupName = ""
    private var groupCnt = 0
    private var familyId = 0

    private val jwt = GlobalApplication.prefs.jwt ?: ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFindGroupBinding {
        return FragmentFindGroupBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initFindGroupView()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initFindGroupView() = with(binding){
        findGroupViewModel.getGroupInfoResponse.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    inviteCodeValidateText.text = "유효한 초대코드입니다"
                    inviteCodeValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
                    inputCodeEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
                    isValidCode = true
                    groupName = response.data?.familyName.toString()
                    groupCnt = response.data?.familyMemberCount ?: 0
                    familyId = response.data?.familyId ?: 0
                    setVisibility()
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    when(response.code){
                        CODE_INVALID -> {
                            inviteCodeValidateText.text = "유효하지 않은 초대코드입니다"
                        }
                        CODE_SERVER_ERROR ->{
                            Toast.makeText(requireContext(), "서버 에러입니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    inviteCodeValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                    inputCodeEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                    isValidCode = false
                    setVisibility()
                }
            }

        }
    }

    private fun buttonClick() = with(binding) {

        searchInviteButton.setOnClickListener {
            val inviteCode = inputCodeEdittext.text.toString()
            if (inviteCode.isBlank()) {
                inviteCodeValidateText.text = "초대코드를 입력해주세요"
                inviteCodeValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                inputCodeEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                isValidCode = false
                setVisibility()
            } else {
                checkValidCode(inviteCode)
            }
        }

        enterGroupButton.setOnClickListener {
            if (isValidCode) {
                showAddProfileBottomSheetDialog()
            }
            else{
                Toast.makeText(requireContext(), "유효한 코드를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }
    }

    private fun checkValidCode(invitationCode: String){
        findGroupViewModel.getGroupInfo(invitationCode, jwt)
    }

    private fun setVisibility() = with(binding) {
        if (isValidCode) {
            imgFamilyPhoto.visibility = View.VISIBLE
            groupNameText.visibility = View.VISIBLE
            enterGroupButton.visibility = Button.VISIBLE
            findGroupInvisibleCommentText.visibility = View.VISIBLE
            groupNameText.text = groupName
        } else {
            imgFamilyPhoto.visibility = View.INVISIBLE
            groupNameText.visibility = View.INVISIBLE
            enterGroupButton.visibility = Button.INVISIBLE
            findGroupInvisibleCommentText.visibility = View.INVISIBLE
        }
    }

    private fun showAddProfileBottomSheetDialog() {
        val addProfileSheetFragment = AddProfileBottomSheet()

        val bundle = Bundle()
        val invitationCode = binding.inputCodeEdittext.text.toString()
        bundle.putString("invitationCode", invitationCode)
        bundle.putInt("familyId", familyId)

        addProfileSheetFragment.arguments = bundle
        addProfileSheetFragment.show(childFragmentManager, addProfileSheetFragment.tag)
    }

    companion object {
        private const val CODE_INVALID = 400
        private const val CODE_SERVER_ERROR = 500
    }

}