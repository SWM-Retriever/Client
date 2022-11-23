package org.retriever.dailypet.ui.mypage.adapter

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.BottomSheetProfileModifyBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.mypage.ModifyProfile
import org.retriever.dailypet.ui.mypage.MyPageViewModel
import org.retriever.dailypet.ui.signup.EditTextValidateState
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular
import java.io.File

@AndroidEntryPoint
class ProfileModifyBottomSheet : BottomSheetDialogFragment() {

    private val myPageViewModel by viewModels<MyPageViewModel>()

    private var _binding: BottomSheetProfileModifyBinding? = null
    private val binding get() = _binding!!

    private var nickName = ""
    private var profileImageUrl = ""

    private var imageUrl = ""
    private var file: File? = null
    private lateinit var fileUri: Uri
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetProfileModifyBinding.inflate(inflater, container, false)

        nickName = arguments?.getString("nickName") ?: ""
        profileImageUrl = arguments?.getString("profileImageUrl") ?: ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        buttonClick()
        initNickNameView()
        watchEdittext()
        observeNickNameViewState()
        observeRegisterButtonState()
        observePostImageResponse()
        observeModifyResponse()
    }

    private fun initView() {
        with(binding) {
            if (profileImageUrl.isNotBlank()) {
                profilePhotoImageview.load(profileImageUrl)
            }
            profileNicknameEdittext.setText(nickName)
            myPageViewModel.setNickNameState(EditTextValidateState.VALID_STATE)
            hideProgressCircular(progressCircular)
        }
    }

    private fun buttonClick() {
        with(binding) {
            createProfilePhotoButton.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop()
                    .compress(2048)
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }

            profileNicknameCheckButton.setOnClickListener {
                nickName = profileNicknameEdittext.text.toString()
                checkValidNickName(nickName)
            }

            registerCompleteButton.setOnClickListener {
                if (file != null) {
                    file?.let {
                        val requestFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                        val multipartBody = MultipartBody.Part.createFormData("image", it.name, requestFile)
                        myPageViewModel.postImage(S3_PATH, multipartBody)
                    }
                } else {
                    modifyProfile()
                }
            }
        }
    }

    private fun initNickNameView() = with(binding) {
        myPageViewModel.nickNameResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    myPageViewModel.setNickNameState(EditTextValidateState.VALID_STATE)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)

                    when (response.code) {
                        CODE_INVALID_NICKNAME -> {
                            if (nickName == arguments?.getString("nickName")) {
                                myPageViewModel.setNickNameState(EditTextValidateState.VALID_STATE)
                            } else {
                                myPageViewModel.setNickNameState(EditTextValidateState.USED_STATE)
                            }
                        }
                        CODE_FAIL -> {
                            Log.e(TAG, "SERVER ERROR")
                            Toast.makeText(requireContext(), "API 서버 에러", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun watchEdittext() {
        binding.profileNicknameEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                myPageViewModel.setNickNameState(EditTextValidateState.DEFAULT_STATE)
            }
        })
    }

    private fun checkValidNickName(nickName: String) {
        if (nickName.isBlank()) {
            myPageViewModel.setNickNameState(EditTextValidateState.INVALID_STATE)
        } else {
            myPageViewModel.postCheckProfileNickname(nickName)
        }
    }

    private fun observeNickNameViewState() {
        myPageViewModel.editTextValidateState.observe(viewLifecycleOwner) {
            when (it) {
                EditTextValidateState.DEFAULT_STATE -> {
                    setDefaultState()
                }
                EditTextValidateState.VALID_STATE -> {
                    setValidState()
                }
                EditTextValidateState.INVALID_STATE -> {
                    setInValidState()
                }
                EditTextValidateState.USED_STATE -> {
                    setUsedState()
                }
            }
        }
    }

    private fun setDefaultState() = with(binding) {
        validateNicknameText.visibility = View.INVISIBLE
        profileNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.grey_blue_edittext)
        myPageViewModel.setRegisterButtonState(false)
    }

    private fun setValidState() = with(binding) {
        validateNicknameText.visibility = View.VISIBLE
        validateNicknameText.text = resources.getString(R.string.valid_nickname_text)
        validateNicknameText.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
        profileNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
        myPageViewModel.setRegisterButtonState(true)
    }

    private fun setInValidState() = with(binding) {
        validateNicknameText.visibility = View.VISIBLE
        validateNicknameText.text = resources.getString(R.string.invalid_nickname_text)
        validateNicknameText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
        profileNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
        myPageViewModel.setRegisterButtonState(false)
    }

    private fun setUsedState() = with(binding) {
        validateNicknameText.visibility = View.VISIBLE
        validateNicknameText.text = resources.getString(R.string.already_used_nickname_text)
        validateNicknameText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
        profileNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
        myPageViewModel.setRegisterButtonState(false)
    }

    private fun observeRegisterButtonState() {
        myPageViewModel.registerButtonState.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                setValidRegisterButton()
            } else {
                setInValidRegisterButton()
            }
        }
    }

    private fun setValidRegisterButton() = with(binding) {
        registerCompleteButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
        registerCompleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        registerCompleteButton.isClickable = true
    }

    private fun setInValidRegisterButton() = with(binding) {
        registerCompleteButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
        registerCompleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_light_grey))
        registerCompleteButton.isClickable = false
    }

    private fun observePostImageResponse() {
        myPageViewModel.postImageResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> Unit

                is Resource.Success -> {
                    imageUrl = response.data?.imageUrl ?: ""
                    modifyProfile()
                }
                is Resource.Error -> Toast.makeText(requireContext(), "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun observeModifyResponse() = with(binding){
        myPageViewModel.modifyProfile.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(binding.progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(binding.progressCircular)
                    GlobalApplication.prefs.nickname = response.data?.nickName
                    GlobalApplication.prefs.profileImageUrl = response.data?.profileImageUrl
                    Toast.makeText(requireContext(), "프로필이 수정되었습니다", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                is Resource.Error -> {
                    hideProgressCircular(binding.progressCircular)
                }
            }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    fileUri = data?.data!!
                    file = File(fileUri.path ?: "")
                    binding.profilePhotoImageview.load(file)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "사진 등록 취소", Toast.LENGTH_SHORT).show()
                }

            }
        }

    private fun modifyProfile() {
        val photoUrl = imageUrl.ifEmpty { profileImageUrl }
        val modifyProfile = ModifyProfile(
            binding.profileNicknameEdittext.text.toString(),
            photoUrl
        )
        myPageViewModel.modifyProfile(jwt, modifyProfile)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        private const val TAG = "PROFILE MODIFY"
        private const val CODE_INVALID_NICKNAME = 409
        private const val CODE_FAIL = 500
        private const val S3_PATH = "MEMBER"
    }

}