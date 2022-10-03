package org.retriever.dailypet.ui.login

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
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreateFamilyBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.login.FamilyInfo
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class CreateFamilyFragment : BaseFragment<FragmentCreateFamilyBinding>() {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val jwt = GlobalApplication.prefs.jwt ?: ""

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
        initEditText()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initCheckFamilyName() = with(binding) {
        loginViewModel.familyNameResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    textValidate.text = getString(R.string.valid_groupname_text)
                    textValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
                    editTextGroupName.background = ContextCompat.getDrawable(requireContext(), R.drawable.success_edittext)
                    isValidGroupName = true
                    submitCheck()
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    when (response.code) {
                        INVALID_FAMILY_NAME -> {
                            textValidate.text = getString(R.string.already_used_groupname_text)
                            textValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                            editTextGroupName.background = ContextCompat.getDrawable(requireContext(), R.drawable.fail_edittext)
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
        loginViewModel.registerFamilyResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    Toast.makeText(requireContext(), "그룹이 성공적으로 생성되었습니다", Toast.LENGTH_SHORT).show()
                    GlobalApplication.prefs.familyId = response.data?.familyId ?: -1
                    root.findNavController().navigate(R.id.action_createFamilyFragment_to_createPetFragment)
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

    private fun initEditText() = with(binding) {
        editTextGroupNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(p0: Editable?) {
                if (editTextGroupNickname.text.isNotBlank()) {
                    editTextGroupNickname.background = ContextCompat.getDrawable(requireContext(), R.drawable.whiteblue_click_button)
                    isValidRoleName = true
                } else {
                    editTextGroupNickname.background = ContextCompat.getDrawable(requireContext(), R.drawable.fail_edittext)
                    isValidRoleName = true
                }

                submitCheck()
            }

        })
    }

    private fun buttonClick() = with(binding) {

        btnValidCheck.setOnClickListener {
            val groupName = editTextGroupName.text.toString()

            if (groupName.isBlank()) {
                textValidate.text = getString(R.string.invalid_nickname_text)
                textValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                editTextGroupName.background = ContextCompat.getDrawable(requireContext(), R.drawable.fail_edittext)
                isValidGroupName = false
                submitCheck()
            } else {
                checkValidGroupName(groupName)
            }
        }

        btnCreate.setOnClickListener {
            if (submitCheck()) {
                postFamilyInfo()
            } else {
                Toast.makeText(requireContext(), "모두 입력하여 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        imgbtnBack.setOnClickListener {
            root.findNavController().popBackStack()
        }

    }

    private fun checkValidGroupName(groupName: String) {
        loginViewModel.postCheckFamilyName(jwt, groupName)
    }

    private fun submitCheck(): Boolean {
        return if (isValidGroupName && isValidRoleName) {
            binding.btnCreate.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            binding.btnCreate.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btnCreate.isClickable = true
            true
        } else {
            binding.btnCreate.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
            binding.btnCreate.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            binding.btnCreate.isClickable = false
            false
        }
    }

    private fun postFamilyInfo() = with(binding) {
        val familyName = editTextGroupName.text.toString()
        val roleName = editTextGroupNickname.text.toString()
        val familyInfo = FamilyInfo(familyName, roleName)

        loginViewModel.postFamily(jwt, familyInfo)
    }

    companion object {
        private const val FAILED_FAMILY = 400
        private const val INVALID_FAMILY_NAME = 409
        private const val CODE_ERROR = 500
    }
}