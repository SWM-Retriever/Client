package org.retriever.dailypet.ui.signup.group

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreateFamilyBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.group.GroupInfo
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.signup.EditTextState
import org.retriever.dailypet.ui.signup.EditTextValidateState
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular
import java.util.ArrayList

class CreateGroupFragment : BaseFragment<FragmentCreateFamilyBinding>() {

    private val groupViewModel by activityViewModels<GroupViewModel>()

    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId
    private var progressList: ArrayList<String> = arrayListOf("프로필","그룹","반려동물")

    private val args: CreateGroupFragmentArgs by navArgs()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreateFamilyBinding {
        return FragmentCreateFamilyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        observeGroupNameState()
        observeGroupRoleNameState()
        observeNextButtonState()
        initCheckFamilyName()
        initPostFamilyInfo()
        initModifyFamily()
        initEditText()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
        binding.signUpProgressbar.setStateDescriptionData(progressList)
    }

    private fun observeGroupNameState() {
        groupViewModel.groupNameState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                EditTextValidateState.DEFAULT_STATE -> {
                    setGroupNameDefaultView()
                }
                EditTextValidateState.VALID_STATE -> {
                    setGroupNameValidView()
                }
                EditTextValidateState.INVALID_STATE -> {
                    setGroupNameInValidView()
                }
                EditTextValidateState.USED_STATE -> {
                    setGroupNameUsedView()
                }
            }
        }
    }

    private fun setGroupNameDefaultView() = with(binding) {
        groupNameValidateText.visibility = View.INVISIBLE
        groupNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.grey_blue_edittext)
    }

    private fun setGroupNameValidView() = with(binding) {
        groupNameValidateText.text = getString(R.string.valid_groupname_text)
        groupNameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
        groupNameValidateText.visibility = View.VISIBLE
        groupNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
    }

    private fun setGroupNameInValidView() = with(binding) {
        groupNameValidateText.text = getString(R.string.invalid_groupname_text)
        groupNameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
        groupNameValidateText.visibility = View.VISIBLE
        groupNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
    }

    private fun setGroupNameUsedView() = with(binding) {
        groupNameValidateText.text = getString(R.string.already_used_groupname_text)
        groupNameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
        groupNameValidateText.visibility = View.VISIBLE
        groupNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
    }

    private fun observeGroupRoleNameState() = with(binding) {
        groupViewModel.groupRoleNameState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                EditTextState.DEFAULT_STATE -> {
                    groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.grey_blue_edittext)
                }
                EditTextState.VALID_STATE -> {
                    groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
                }
                EditTextState.INVALID_STATE -> {
                    groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                }
            }
        }
    }

    private fun observeNextButtonState() {
        groupViewModel.nextButtonState.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                setNextButtonValidView()
            } else {
                setNextButtonInValidView()
            }
        }
    }

    private fun setNextButtonValidView() = with(binding) {
        nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
        nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        nextButton.isClickable = true
    }

    private fun setNextButtonInValidView() = with(binding) {
        nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
        nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_light_grey))
        nextButton.isClickable = false
    }

    private fun initCheckFamilyName() = with(binding) {
        groupViewModel.groupNameResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    groupViewModel.setGroupNameState(EditTextValidateState.VALID_STATE)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    when (response.code) {
                        INVALID_FAMILY_NAME, FAILED_FAMILY -> {
                            groupViewModel.setGroupNameState(EditTextValidateState.USED_STATE)
                        }
                        CODE_ERROR -> {
                            Toast.makeText(requireContext(), "서버 에러입니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }

    private fun initPostFamilyInfo() = with(binding) {
        groupViewModel.registerGroupResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        Toast.makeText(requireContext(), "그룹이 성공적으로 생성되었습니다", Toast.LENGTH_SHORT).show()
                        GlobalApplication.prefs.familyId = response.data?.familyId ?: -1

                        val action = CreateGroupFragmentDirections.actionCreateFamilyFragmentToCreatePetFragment(false)
                        root.findNavController().navigate(action)
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        when (response.code) {
                            FAILED_FAMILY -> {
                                Toast.makeText(requireContext(), "그룹 생성에 실패하였습니다", Toast.LENGTH_SHORT).show()
                            }
                            CODE_ERROR -> {
                                Toast.makeText(requireContext(), "서버 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initModifyFamily() = with(binding) {
        groupViewModel.modifyGroupResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    response.data?.let { saveSharedPreferences(it.familyId, it.familyName, it.invitationCode, it.groupType) }
                    root.findNavController().popBackStack()
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun saveSharedPreferences(
        familyId: Int,
        familyName: String,
        invitationCode: String,
        groupType: String,
    ) {
        GlobalApplication.prefs.apply {
            this.familyId = familyId
            this.groupName = familyName
            this.invitationCode = invitationCode
            this.groupType = groupType
        }
    }

    private fun initEditText() = with(binding) {
        groupNicknameEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                if (groupNicknameEdittext.text.isNotBlank()) {
                    groupViewModel.setGroupRoleNameState(EditTextState.VALID_STATE)
                } else {
                    groupViewModel.setGroupRoleNameState(EditTextState.INVALID_STATE)
                }
            }
        })
    }

    private fun buttonClick() = with(binding) {

        groupNameCheckText.setOnClickListener {
            val groupName = groupNameEdittext.text.toString()

            if (groupName.isBlank()) {
                groupViewModel.setGroupNameState(EditTextValidateState.INVALID_STATE)
            } else {
                checkValidGroupName(groupName)
            }
        }

        nextButton.setOnClickListener {
            if (args.isFromMyPage) {
                modifyFamily()
            } else {
                postFamilyInfo()
            }
        }

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }

    }

    private fun checkValidGroupName(groupName: String) {
        groupViewModel.postCheckFamilyName(jwt, groupName)
    }

    private fun modifyFamily() = with(binding) {
        val familyName = groupNameEdittext.text.toString()
        val roleName = groupNicknameEdittext.text.toString()
        val groupInfo = GroupInfo(familyName, roleName)

        groupViewModel.modifyFamily(familyId, jwt, groupInfo)
    }

    private fun postFamilyInfo() = with(binding) {
        val familyName = groupNameEdittext.text.toString()
        val roleName = groupNicknameEdittext.text.toString()
        val groupInfo = GroupInfo(familyName, roleName)

        groupViewModel.postFamily(jwt, groupInfo)
    }

    companion object {
        private const val FAILED_FAMILY = 400
        private const val INVALID_FAMILY_NAME = 409
        private const val CODE_ERROR = 500
    }

}