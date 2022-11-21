package org.retriever.dailypet.ui.signup.pet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreatePetBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.PetResponse
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.ui.signup.EditTextState
import org.retriever.dailypet.ui.signup.EditTextValidateState
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreatePetFragment : BaseFragment<FragmentCreatePetBinding>() {

    private val petViewModel by viewModels<PetViewModel>()
    private lateinit var onBackCallBack: OnBackPressedCallback
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId
    private var datePicker: MaterialDatePicker<Long>? = null
    private var imageUrl = ""
    private var file: File? = null
    private lateinit var fileUri: Uri
    private var progressList: ArrayList<String> = arrayListOf("프로필", "그룹", "반려동물")

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreatePetBinding {
        return FragmentCreatePetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPetInfo()
        initCallBack()
        initProgressCircular()
        buttonClick()
        editTextWatch()
        observeSetPetNameState()
        observePetTypeState()
        observePetSexState()
        observeBirthState()
        observeBreedState()
        observeNotKnowState()
        observeWeightState()
        observeRegisterButtonState()
        observePetNameResponse()
        observePetResponse()
        observePostImageResponse()
    }

    private fun initPetInfo() = with(binding) {
        petNameEdittext.setText(petViewModel.petInfo.petName)
        petBirthDatePicker.text = petViewModel.petInfo.birthDate

        petBreedBottomSheet.text = petViewModel.petBreed
        val weight = petViewModel.petInfo.weight.toString()
        petWeightEdittext.setText(if (weight == "-1.0") "" else weight)
        petRegisterNumEdittext.setText(petViewModel.petInfo.registerNumber)
    }

    private fun initCallBack() {
        onBackCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackCallBack)
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
        binding.signUpProgressbar.setStateDescriptionData(progressList)
    }

    private fun buttonClick() = with(binding) {

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
                petViewModel.setPetNameState(EditTextValidateState.INVALID_STATE)
            } else {
                checkValidPetName(petName)
            }
        }

        petTypeDogButton.setOnClickListener {
            petViewModel.setPetTypeState(EditTextState.INVALID_STATE)
        }

        petTypeCatButton.setOnClickListener {
            petViewModel.setPetTypeState(EditTextState.VALID_STATE)
        }

        petSexMaleButton.setOnClickListener {
            petViewModel.setPetSexState(EditTextState.INVALID_STATE)
        }

        petSexFemaleButton.setOnClickListener {
            petViewModel.setPetSexState(EditTextState.VALID_STATE)
        }

        petBirthDatePicker.setOnClickListener {
            showDatePicker()
        }

        petBreedBottomSheet.setOnClickListener {
            if (petViewModel.getPetTypeSelected()) {
                showBreedBottomSheetDialog()
            } else {
                Toast.makeText(requireContext(), "반려동물 종류를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dontKnowButton.setOnClickListener {
            dontKnowButton.isSelected = !dontKnowButton.isSelected
            petViewModel.setNotKnowState(dontKnowButton.isSelected)
        }

        notNeutralRadio.setOnClickListener {
            petViewModel.petInfo.isNeutered = false
        }

        neutralRadio.setOnClickListener {
            petViewModel.petInfo.isNeutered = true
        }

        petSubmitButton.setOnClickListener {
            if (file != null) {
                file?.let {
                    val requestFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                    val multipartBody = MultipartBody.Part.createFormData("image", it.name, requestFile)
                    petViewModel.postImage(S3_PATH, multipartBody)
                }
            } else {
                postPetInfo()
            }
        }
    }

    private fun checkValidPetName(petName: String) {
        petViewModel.postCheckPetName(familyId, jwt, petName)
    }

    private fun showDatePicker() {
        val constraints: CalendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now()).build()

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.input_pet_birth_text))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraints)
            .build()


        datePicker?.apply {

            show(this@CreatePetFragment.requireActivity().supportFragmentManager, datePicker.toString())
            addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val getDateStringFromDatePicker = sdf.format(it)
                val currentDateString = sdf.format(Date())

                binding.petBirthDatePicker.text = getDateStringFromDatePicker
                petViewModel.petInfo.birthDate = getDateStringFromDatePicker

                val getDateFromDatePicker: Date = sdf.parse(getDateStringFromDatePicker) ?: Date()
                val currentDate: Date = sdf.parse(currentDateString) ?: Date()

                if (getDateFromDatePicker <= currentDate) {
                    petViewModel.setBirthState(EditTextState.VALID_STATE)
                } else {
                    petViewModel.setBirthState(EditTextState.INVALID_STATE)
                }
            }
            addOnNegativeButtonClickListener {
                dismiss()
            }
        }
    }

    private fun showBreedBottomSheetDialog() {
        val breedSheetFragment = BreedBottomSheet { breed ->
            binding.petBreedBottomSheet.text = breed.petKindName
            petViewModel.petBreed = breed.petKindName
            petViewModel.petInfo.petKindId = breed.petKindId
            petViewModel.setBreedState(EditTextState.VALID_STATE)
        }

        val bundle = Bundle()
        val petType = petViewModel.getPetType()
        bundle.putString("petType", petType)

        breedSheetFragment.arguments = bundle
        breedSheetFragment.show(childFragmentManager, breedSheetFragment.tag)
    }

    private fun postPetInfo() {
        petViewModel.postPet(familyId, jwt)
    }

    private fun editTextWatch() = with(binding) {
        petNameEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                val petName = petNameEdittext.text.toString()
                petViewModel.petInfo.petName = petName
                petViewModel.setPetNameState(EditTextValidateState.DEFAULT_STATE)
            }
        })

        petWeightEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val weight = petWeightEdittext.text.toString()
                petViewModel.petInfo.weight = weight.toFloat()

                if (weight.isNotBlank()) {
                    petViewModel.setWeightState(EditTextState.VALID_STATE)
                } else {
                    petViewModel.setWeightState(EditTextState.INVALID_STATE)
                }
            }
        })

        petRegisterNumEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                petViewModel.petInfo.registerNumber = petRegisterNumEdittext.text.toString()
            }

        })
    }

    private fun observeSetPetNameState() {
        petViewModel.petNameState.asLiveData().observe(viewLifecycleOwner) {
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

    private fun observePetTypeState() {
        petViewModel.petTypeState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                EditTextState.DEFAULT_STATE -> {
                    setDefaultType()
                }
                EditTextState.VALID_STATE -> {
                    setCatType()
                }
                EditTextState.INVALID_STATE -> {
                    setDogType()
                }
            }
        }
    }

    private fun setDefaultType() = with(binding) {
        petTypeDogButton.isSelected = false
        petTypeCatButton.isSelected = false
    }

    private fun setDogType() = with(binding) {
        petTypeDogButton.isSelected = true
        petTypeCatButton.isSelected = false
    }

    private fun setCatType() = with(binding) {
        petTypeDogButton.isSelected = false
        petTypeCatButton.isSelected = true
    }

    private fun observePetSexState() {
        petViewModel.petSexState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                EditTextState.DEFAULT_STATE -> {
                    setDefaultSex()
                }
                EditTextState.VALID_STATE -> {
                    setFemaleType()
                }
                EditTextState.INVALID_STATE -> {
                    setMaleType()
                }
            }
        }
    }

    private fun setDefaultSex() = with(binding) {
        petSexMaleButton.isSelected = false
        petSexFemaleButton.isSelected = false
    }

    private fun setMaleType() = with(binding) {
        petSexMaleButton.isSelected = true
        petSexFemaleButton.isSelected = false
    }

    private fun setFemaleType() = with(binding) {
        petSexMaleButton.isSelected = false
        petSexFemaleButton.isSelected = true
    }

    private fun observeBirthState() {
        petViewModel.birthState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                EditTextState.DEFAULT_STATE -> {
                    setDefaultTextView(binding.petBirthDatePicker)
                }
                EditTextState.VALID_STATE -> {
                    setValidTextView(binding.petBirthDatePicker)
                }
                EditTextState.INVALID_STATE -> {
                    setInValidTextView(binding.petBirthDatePicker)
                }
            }
        }
    }

    private fun observeBreedState() {
        petViewModel.breedState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                EditTextState.DEFAULT_STATE -> {
                    setDefaultTextView(binding.petBreedBottomSheet)
                }
                EditTextState.VALID_STATE -> {
                    setValidTextView(binding.petBreedBottomSheet)
                }
                EditTextState.INVALID_STATE -> {
                    setInValidTextView(binding.petBreedBottomSheet)
                }
            }
        }
    }

    private fun setDefaultTextView(textView: TextView) {
        textView.setViewBackgroundWithoutResettingPadding(R.drawable.whitegrey_click_button)
    }

    private fun setValidTextView(textView: TextView) {
        textView.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
    }

    private fun setInValidTextView(textView: TextView) {
        textView.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
    }

    private fun observeNotKnowState() {
        petViewModel.notKnowState.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                setTrueNotKnow()
            } else {
                setFalseNotKnow()
            }
        }
    }

    private fun setTrueNotKnow() = with(binding) {
        dontKnowButton.isSelected = true
        petBreedBottomSheet.text = getString(R.string.dont_know_text)
        petBreedBottomSheet.isClickable = false
    }

    private fun setFalseNotKnow() = with(binding) {
        dontKnowButton.isSelected = false
        petBreedBottomSheet.text = getString(R.string.pet_breed_hint)
        petBreedBottomSheet.isClickable = true
    }

    private fun observeWeightState() {
        petViewModel.weightState.asLiveData().observe(viewLifecycleOwner) {
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
        petViewModel.registerButtonState.asLiveData().observe(viewLifecycleOwner) {
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
        petViewModel.petNameResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        petViewModel.setPetNameState(EditTextValidateState.VALID_STATE)
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        petViewModel.setPetNameState(EditTextValidateState.USED_STATE)
                    }
                }
            }
        }
    }

    private fun observePetResponse() = with(binding) {
        petViewModel.petResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)

                        val petResponse = response.data?.let {
                            PetResponse(
                                familyId = it.familyId,
                                familyName = it.familyName,
                                nickName = it.nickName,
                                invitationCode = it.invitationCode,
                                groupType = it.groupType,
                                profileImageUrl = it.profileImageUrl,
                            )
                        }

                        val isAdd = arguments?.get("isAdd") ?: false

                        if(isAdd as Boolean){
                            root.findNavController().popBackStack()
                        }else{
                            val action = CreatePetFragmentDirections.actionCreatePetFragmentToCreationCompleteFragment(petResponse!!)
                            root.findNavController().navigate(action)
                        }

                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        Toast.makeText(requireContext(), "반려동물 등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observePostImageResponse() {
        petViewModel.postImageResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> Unit

                is Resource.Success -> {
                    imageUrl = response.data?.imageUrl ?: ""
                    petViewModel.petInfo.profileImageUrl = imageUrl
                    postPetInfo()
                }
                is Resource.Error -> Toast.makeText(requireContext(), "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT)
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

    override fun onDestroyView() {
        super.onDestroyView()
        datePicker = null
        onBackCallBack.remove()
    }

    companion object {
        private const val S3_PATH = "PET"
    }

}