package org.retriever.dailypet.ui.signup.profile

import android.app.Activity.RESULT_OK
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreateProfileBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.signup.EditTextValidateState
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular
import java.io.File

class CreateProfileFragment : BaseFragment<FragmentCreateProfileBinding>() {

    private val profileViewModel by activityViewModels<ProfileViewModel>()

    private lateinit var registerProfile: RegisterProfile

    private var nickname = ""
    private var imageUrl = ""
    private var file: File? = null
    private lateinit var fileUri: Uri

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                RESULT_OK -> {
                    fileUri = data?.data!!
                    file = File(fileUri.path ?: "")
                    binding.profilePhotoImageview.load(file)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }

            }
        }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreateProfileBinding {
        return FragmentCreateProfileBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRegisterProfile()
        initProgressCircular()
        initNickNameView()
        initProfileView()
        watchEdittext()
        buttonClick()
        observeNickNameViewState()
        observeRegisterButtonState()
        observePreSignedUrlResponse()
        observeImageUrlResponse()
    }

    private fun initRegisterProfile() {
        val args: CreateProfileFragmentArgs by navArgs()
        registerProfile = args.registerProfile
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initNickNameView() = with(binding) {
        profileViewModel.nickNameResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    profileViewModel.setNickNameState(EditTextValidateState.VALID_STATE)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)

                    when (response.code) {
                        CODE_INVALID_NICKNAME -> {
                            profileViewModel.setNickNameState(EditTextValidateState.USED_STATE)
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

    private fun initProfileView() = with(binding) {
        profileViewModel.registerProfileResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        val jwt = response.data?.jwtToken
                        GlobalApplication.prefs.jwt = jwt
                        GlobalApplication.prefs.nickname = nickname
                        Toast.makeText(requireContext(), "프로필 등록에 성공하였습니다", Toast.LENGTH_SHORT).show()
                        root.findNavController().navigate(R.id.action_createProfileFragment_to_selectFamilyTypeFragment)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "프로필 등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
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
                profileViewModel.setNickNameState(EditTextValidateState.DEFAULT_STATE)
            }
        })
    }

    private fun buttonClick() = with(binding) {

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }

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
            val nickname = profileNicknameEdittext.text.toString()
            checkValidNickName(nickname)
        }

        registerCompleteButton.setOnClickListener {
            registerProfile.nickName = profileNicknameEdittext.text.toString()

            if (file != null) {
                profileViewModel.getPreSignedUrl(S3_PATH, file!!.name)
            } else {
                postProfileInfo(registerProfile)
            }
        }

    }

    private fun checkValidNickName(nickName: String)  {
        if (nickName.isBlank()) {
            profileViewModel.setNickNameState(EditTextValidateState.INVALID_STATE)
        } else {
            profileViewModel.postCheckProfileNickname(nickName)
        }
    }

    private fun observeNickNameViewState() {
        profileViewModel.editTextValidateState.observe(viewLifecycleOwner) {
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
        profileViewModel.setRegisterButtonState(false)
    }

    private fun setValidState() = with(binding) {
        validateNicknameText.visibility = View.VISIBLE
        validateNicknameText.text = resources.getString(R.string.valid_nickname_text)
        validateNicknameText.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
        profileNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
        profileViewModel.setRegisterButtonState(true)
    }

    private fun setInValidState() = with(binding) {
        validateNicknameText.visibility = View.VISIBLE
        validateNicknameText.text = resources.getString(R.string.invalid_nickname_text)
        validateNicknameText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
        profileNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
        profileViewModel.setRegisterButtonState(false)
    }

    private fun setUsedState() = with(binding) {
        validateNicknameText.visibility = View.VISIBLE
        validateNicknameText.text = resources.getString(R.string.already_used_nickname_text)
        validateNicknameText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
        profileNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
        profileViewModel.setRegisterButtonState(false)
    }

    private fun observeRegisterButtonState() {
        profileViewModel.registerButtonState.asLiveData().observe(viewLifecycleOwner) {
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

    private fun observePreSignedUrlResponse() {
        profileViewModel.preSignedUrlResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    imageUrl = response.data?.originalUrl ?: ""
                    registerProfile.profileImageUrl = imageUrl

                    file?.let {
                        val requestBody = it.path.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val multipartBody = MultipartBody.Part.createFormData("file", it.name, requestBody)
                        profileViewModel.putImageUrl("image/jpeg", response.data?.preSignedUrl ?: "", multipartBody)
                    }
                }
                is Resource.Error -> Unit
            }
        }
    }

    private fun observeImageUrlResponse() {
        profileViewModel.putImageUrlResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    postProfileInfo(registerProfile)
                }
                is Resource.Error -> Unit
            }
        }
    }

    private fun postProfileInfo(registerProfile: RegisterProfile) {
        profileViewModel.postProfile(registerProfile)
    }

    companion object {
        private const val TAG = "CREATE PROFILE"
        private const val CODE_INVALID_NICKNAME = 409
        private const val CODE_FAIL = 500
        private const val S3_PATH = "MEMBER"
    }

}