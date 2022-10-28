package org.retriever.dailypet.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreateFamilyBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.family.FamilyInfo
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.signup.viewmodel.FamilyViewModel
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular

class CreateFamilyFragment : BaseFragment<FragmentCreateFamilyBinding>() {

    private val familyViewModel by activityViewModels<FamilyViewModel>()

    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId

    private val args: CreateFamilyFragmentArgs by navArgs()

    private var isValidGroupName = false
    private var isValidRoleName = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreateFamilyBinding {
        return FragmentCreateFamilyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initCheckFamilyName()
        initPostFamilyInfo()
        initModifyFamily()
        initEditText()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initCheckFamilyName() = with(binding) {
        familyViewModel.familyNameResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    groupNameValidateText.text = getString(R.string.valid_groupname_text)
                    groupNameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
                    groupNameValidateText.visibility = View.VISIBLE
                    groupNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
                    isValidGroupName = true
                    submitCheck()
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    when (response.code) {
                        INVALID_FAMILY_NAME, FAILED_FAMILY-> {
                            groupNameValidateText.text = getString(R.string.already_used_groupname_text)
                            groupNameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                            groupNameValidateText.visibility = View.VISIBLE
                            groupNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                            isValidGroupName = false
                            submitCheck()
                        }
                        CODE_ERROR -> {
                            Toast.makeText(requireContext(), "서버 에러입니다", Toast.LENGTH_SHORT).show()
                            submitCheck()
                        }
                    }
                }
            }

        }
    }

    private fun initPostFamilyInfo() = with(binding) {
        familyViewModel.registerFamilyResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        Toast.makeText(requireContext(), "그룹이 성공적으로 생성되었습니다", Toast.LENGTH_SHORT).show()
                        GlobalApplication.prefs.familyId = response.data?.familyId ?: -1

                        val action = CreateFamilyFragmentDirections.actionCreateFamilyFragmentToCreatePetFragment(false)
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
                        submitCheck()
                    }
                }
            }
        }
    }

    private fun initModifyFamily() = with(binding) {
        familyViewModel.modifyFamilyResponse.observe(viewLifecycleOwner) { response ->
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
                isValidRoleName = if (groupNicknameEdittext.text.isNotBlank()) {
                    groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
                    true
                } else {
                    groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                    false
                }

                submitCheck()
            }

        })
    }

    private fun buttonClick() = with(binding) {

        groupNameCheckText.setOnClickListener {
            val groupName = groupNameEdittext.text.toString()

            if (groupName.isBlank()) {
                groupNameValidateText.text = getString(R.string.invalid_groupname_text)
                groupNameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                groupNameValidateText.visibility = View.VISIBLE
                groupNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                isValidGroupName = false
                submitCheck()
            } else {
                checkValidGroupName(groupName)
            }
        }

        nextButton.setOnClickListener {
            if (submitCheck()) {
                if (args.isFromMyPage) {
                    modifyFamily()
                } else {
                    postFamilyInfo()
                }
            } else {
                Toast.makeText(requireContext(), "모두 입력하여 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }

    }

    private fun checkValidGroupName(groupName: String) {
        familyViewModel.postCheckFamilyName(jwt, groupName)
    }

    private fun submitCheck(): Boolean {
        return if (isValidGroupName && isValidRoleName) {
            binding.nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            binding.nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.nextButton.isClickable = true
            true
        } else {
            binding.nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
            binding.nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_light_grey))
            binding.nextButton.isClickable = false
            false
        }
    }

    private fun modifyFamily() = with(binding) {
        val familyName = groupNameEdittext.text.toString()
        val roleName = groupNicknameEdittext.text.toString()
        val familyInfo = FamilyInfo(familyName, roleName)

        familyViewModel.modifyFamily(familyId, jwt, familyInfo)
    }

    private fun postFamilyInfo() = with(binding) {
        val familyName = groupNameEdittext.text.toString()
        val roleName = groupNicknameEdittext.text.toString()
        val familyInfo = FamilyInfo(familyName, roleName)

        familyViewModel.postFamily(jwt, familyInfo)
    }

    companion object {
        private const val FAILED_FAMILY = 400
        private const val INVALID_FAMILY_NAME = 409
        private const val CODE_ERROR = 500
    }
}