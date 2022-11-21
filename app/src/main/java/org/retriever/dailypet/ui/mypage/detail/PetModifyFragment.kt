package org.retriever.dailypet.ui.mypage.detail

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentPetModifyBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.ModifyPetRequest
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.signup.EditTextState
import org.retriever.dailypet.ui.signup.EditTextValidateState
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular
import java.io.File

class PetModifyFragment : BaseFragment<FragmentPetModifyBinding>() {

    private val myPageDetailViewModel by activityViewModels<MyPageDetailViewModel>()

    private val petDetailItem by lazy {
        myPageDetailViewModel.clickPetDetailItem
    }

    private var imageUrl = ""
    private var file: File? = null
    private lateinit var fileUri: Uri

    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPetModifyBinding {
        return FragmentPetModifyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgress()
        initView()
        buttonClick()
        editTextWatch()
        observeSetPetNameState()
        observeWeightState()
        observeRegisterButtonState()
        observePetNameResponse()
        observePostImageResponse()
        observeModifyPet()
    }

    private fun initProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initView() {
        with(binding) {
            petDetailItem?.let {
                myPageDetailViewModel.setPetNameState(EditTextValidateState.VALID_STATE)
                myPageDetailViewModel.setWeightState(EditTextState.VALID_STATE)

                if (it.profileImageUrl.isNotBlank()) {
                    petCircleImage.load(it.profileImageUrl)
                }

                petNameEdittext.setText(it.petName)

                petWeightEdittext.setText(it.weight.toString())

                if (it.isNeutered) {
                    neutralRadio.isChecked = true
                } else {
                    notNeutralRadio.isChecked = true
                }

                petRegisterNumEdittext.setText(it.registerNumber)
            }

        }
    }

    private fun buttonClick() {
        with(binding) {
            createPetPhotoButton.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop()
                    .compress(2048)
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }

            petNameCheckButton.setOnClickListener {
                val petName = petNameEdittext.text.toString()
                if (petName.isBlank()) {
                    myPageDetailViewModel.setPetNameState(EditTextValidateState.INVALID_STATE)
                } else {
                    checkValidPetName(petName)
                }
            }

            petSubmitButton.setOnClickListener {
                if (file != null) {
                    file?.let {
                        val requestFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                        val multipartBody = MultipartBody.Part.createFormData("image", it.name, requestFile)
                        myPageDetailViewModel.postImage(S3_PATH, multipartBody)
                    }
                } else {
                    postModifyPet()
                }
            }
        }
    }

    private fun checkValidPetName(petName: String) {
        myPageDetailViewModel.postCheckPetName(familyId, jwt, petName)
    }

    private fun editTextWatch() = with(binding) {
        petNameEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                myPageDetailViewModel.setPetNameState(EditTextValidateState.DEFAULT_STATE)
            }
        })

        petWeightEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val weight = petWeightEdittext.text.toString()

                if (weight.isNotBlank()) {
                    myPageDetailViewModel.setWeightState(EditTextState.VALID_STATE)
                } else {
                    myPageDetailViewModel.setWeightState(EditTextState.INVALID_STATE)
                }
            }
        })

    }

    private fun observeSetPetNameState() {
        myPageDetailViewModel.petNameState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                EditTextValidateState.DEFAULT_STATE -> {
                    setDefaultPetName()
                }
                EditTextValidateState.VALID_STATE -> {
                    setValidPetName()
                }
                EditTextValidateState.INVALID_STATE -> {
                    setInValidPetName(getString(R.string.invalid_petname_text))
                }
                EditTextValidateState.USED_STATE -> {
                    setInValidPetName(getString(R.string.already_used_petname_text))
                }
            }
        }
    }

    private fun setDefaultPetName() = with(binding) {
        petNameValidate.visibility = View.INVISIBLE
        petNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.grey_blue_edittext)
    }

    private fun setValidPetName() = with(binding) {
        petNameValidate.text = getString(R.string.valid_petname_text)
        petNameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
        petNameValidate.visibility = View.VISIBLE
        petNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
    }

    private fun setInValidPetName(text: String) = with(binding) {
        petNameValidate.text = text
        petNameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
        petNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
        petNameValidate.visibility = View.VISIBLE
    }

    private fun observeWeightState() {
        myPageDetailViewModel.weightState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                EditTextState.DEFAULT_STATE -> {
                    setDefaultWeightView()
                }
                EditTextState.VALID_STATE -> {
                    setValidWeightView()
                }
                EditTextState.INVALID_STATE -> {
                    setInValidWeightView()
                }
            }
        }
    }

    private fun setDefaultWeightView() {
        binding.petWeightEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.grey_blue_edittext)
    }

    private fun setValidWeightView() {
        binding.petWeightEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
    }

    private fun setInValidWeightView() {
        binding.petWeightEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
    }

    private fun observeRegisterButtonState() {
        myPageDetailViewModel.registerButtonState.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                setTrueSubmitButton()
            } else {
                setFalseSubmitButton()
            }
        }
    }

    private fun setTrueSubmitButton() = with(binding) {
        petSubmitButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
        petSubmitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        petSubmitButton.isClickable = true
    }

    private fun setFalseSubmitButton() = with(binding) {
        petSubmitButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
        petSubmitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        petSubmitButton.isClickable = false
    }

    private fun observePetNameResponse() = with(binding) {
        myPageDetailViewModel.petNameResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        myPageDetailViewModel.setPetNameState(EditTextValidateState.VALID_STATE)
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        if (petDetailItem?.petName == petNameEdittext.text.toString()) {
                            myPageDetailViewModel.setPetNameState(EditTextValidateState.VALID_STATE)
                        } else {
                            myPageDetailViewModel.setPetNameState(EditTextValidateState.USED_STATE)
                        }
                    }
                }
            }
        }
    }

    private fun observePostImageResponse() {
        myPageDetailViewModel.postImageResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> Unit

                is Resource.Success -> {
                    imageUrl = response.data?.imageUrl ?: ""
                    postModifyPet()
                }
                is Resource.Error -> Toast.makeText(requireContext(), "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun postModifyPet() {
        val modifyPetRequest = ModifyPetRequest(
            binding.petNameEdittext.text.toString(),
            petDetailItem?.birthDate ?: "",
            binding.petWeightEdittext.text.toString().toFloat(),
            binding.neutralRadio.isChecked,
            binding.petRegisterNumEdittext.text.toString(),
            imageUrl
        )
        myPageDetailViewModel.modifyPet(familyId, petDetailItem?.petId ?: -2, jwt, modifyPetRequest)
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    fileUri = data?.data!!
                    file = File(fileUri.path ?: "")
                    binding.petCircleImage.load(file)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "사진 등록 취소", Toast.LENGTH_SHORT).show()
                }

            }
        }

    private fun observeModifyPet() = with(binding) {
        myPageDetailViewModel.modifyPetResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        root.findNavController().navigate(R.id.action_petModifyFragment_to_myPageDetailFragment)
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
            }
        }
    }

    companion object {
        private const val S3_PATH = "PET"
    }

}