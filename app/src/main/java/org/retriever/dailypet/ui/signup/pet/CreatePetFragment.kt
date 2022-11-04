package org.retriever.dailypet.ui.signup.pet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreatePetBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.ModifyPetRequest
import org.retriever.dailypet.model.signup.pet.PetInfo
import org.retriever.dailypet.model.signup.pet.PetResponse
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.bottomsheet.BreedBottomSheet
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreatePetFragment : BaseFragment<FragmentCreatePetBinding>() {

    private val petViewModel by activityViewModels<PetViewModel>()

    private var petInfo = PetInfo("", "", "", "", -1, 0f, false, "", "")

    private lateinit var onBackCallBack: OnBackPressedCallback

    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId
    private var datePicker: MaterialDatePicker<Long>? = null
    private var petKindId = -1
    private var dontKnow = false
    private val args: CreatePetFragmentArgs by navArgs()
    private var imageUrl = ""
    private var file: File? = null

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    file = File(fileUri.path ?: "")
                    binding.petCircleImage.load(file)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }

            }
        }

    private fun observePreSignedUrlResponse() {
        petViewModel.preSignedUrlResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    imageUrl = response.data?.originalUrl ?: ""
                    petInfo.profileImageUrl = imageUrl
                    file?.let {
                        val requestBody = it.path.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val multipartBody = MultipartBody.Part.createFormData("file", it.name, requestBody)
                        petViewModel.putImageUrl("image/jpeg", response.data?.preSignedUrl ?: "", multipartBody)
                    }
                }
                is Resource.Error -> {

                }
            }
        }
    }

    private fun observeImageUrlResponse() {
        petViewModel.putImageUrlResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    if (args.petDetailItem == null) {
                        postPetInfo(petInfo)
                    } else {
                        modifyPet(
                            args.petDetailItem?.petId ?: -1, ModifyPetRequest(
                                petInfo.petName, petInfo.birthDate, petInfo.weight, petInfo.isNeutered, petInfo.registerNumber, imageUrl
                            )
                        )
                    }
                }
                is Resource.Error -> {

                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreatePetBinding {
        return FragmentCreatePetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        petViewModel.setInitial()
        initCallBack()
        initProgressCircular()
        buttonClick()
        editTextWatch()
        initPetNameView()
        initPetView()
        initSubmitButton()
        initArgsView()
        observeModifyPet()
        observePreSignedUrlResponse()
        observeImageUrlResponse()
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

    private fun initArgsView() = with(binding) {
        args.petDetailItem?.let {

            createPetTitleText.text = getString(R.string.pet_modify_title)
            petSubmitButton.text = getString(R.string.modify_text)

            petNameEdittext.setText(it.petName)
            setValidPetName()

            if (it.petKind == "DOG") {
                setDogType()
            } else {
                setCatType()
            }
            petTypeDogButton.isClickable = false
            petTypeCatButton.isClickable = false

            if (it.gender == "MALE") {
                setMaleType()
            } else {
                setFemaleType()
            }
            petSexMaleButton.isClickable = false
            petSexFemaleButton.isClickable = false

            petBirthDatePicker.text = it.birthDate
            petViewModel.setBirth()

            petBreedBottomSheet.text = it.petKind
            petBreedBottomSheet.isClickable = false

            petWeightEdittext.setText(it.weight.toString())
            petViewModel.setWeight(true)

            petRegisterNumEdittext.setText(it.registerNumber)

            if (it.isNeutered) {
                neutralRadio.isChecked = true
            } else {
                notNeutralRadio.isChecked = true
            }
        }
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
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
                setInValidPetName(getString(R.string.invalid_petname_text))
            } else {
                checkValidPetName(petName)
            }
        }

        petTypeDogButton.setOnClickListener {
            setDogType()
        }

        petTypeCatButton.setOnClickListener {
            setCatType()
        }

        petSexMaleButton.setOnClickListener {
            setMaleType()
        }

        petSexFemaleButton.setOnClickListener {
            setFemaleType()
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
            dontKnow = !dontKnow
            dontKnowButton.isSelected = dontKnow

            if (dontKnow) {
                setTrueDontKnow()
            } else {
                setFalseDontKnow()
            }
        }

        petSubmitButton.setOnClickListener {
            petInfo.petName = petNameEdittext.text.toString()
            petInfo.petType = petViewModel.getPetType()
            petInfo.gender = petViewModel.getSexType()
            petInfo.birthDate = petBirthDatePicker.text.toString()
            petInfo.weight = petWeightEdittext.text.toString().toFloat()
            petInfo.isNeutered = neutralRadio.isChecked
            petInfo.registerNumber = petRegisterNumEdittext.text.toString()
            petInfo.petKindId = petKindId

            if (args.petDetailItem == null) {
                if (file != null) {
                    petViewModel.getPreSignedUrl(S3_PATH, file!!.name)
                } else {
                    postPetInfo(petInfo)
                }
            } else {
                if (file != null) {
                    petViewModel.getPreSignedUrl(S3_PATH, file!!.name)
                } else {
                    modifyPet(
                        args.petDetailItem?.petId ?: -1, ModifyPetRequest(
                            petInfo.petName, petInfo.birthDate, petInfo.weight, petInfo.isNeutered, petInfo.registerNumber, imageUrl
                        )
                    )
                }
            }

        }

    }

    private fun setInValidPetName(text: String) = with(binding) {
        petNameValidate.text = text
        petNameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
        petNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
        petNameValidate.visibility = View.VISIBLE
        petViewModel.setValidPetName(false)
    }

    private fun checkValidPetName(petName: String) {
        petViewModel.postCheckPetName(familyId, jwt, petName)
    }

    private fun setDogType() = with(binding) {
        petTypeDogButton.isSelected = true
        petTypeCatButton.isSelected = false
        petViewModel.setDogTrue()
    }

    private fun setCatType() = with(binding) {
        petTypeDogButton.isSelected = false
        petTypeCatButton.isSelected = true
        petViewModel.setCatTrue()
    }

    private fun setMaleType() = with(binding) {
        petSexMaleButton.isSelected = true
        petSexFemaleButton.isSelected = false
        petViewModel.setMaleTrue()
    }

    private fun setFemaleType() = with(binding) {
        petSexMaleButton.isSelected = false
        petSexFemaleButton.isSelected = true
        petViewModel.setFemaleTrue()
    }

    private fun showDatePicker() {
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.input_pet_birth_text))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker?.apply {
            show(this@CreatePetFragment.requireActivity().supportFragmentManager, datePicker.toString())
            addOnPositiveButtonClickListener {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                binding.petBirthDatePicker.text = date
                petViewModel.setBirth()
            }
            addOnNegativeButtonClickListener {
                dismiss()
            }
        }
    }

    private fun showBreedBottomSheetDialog() {
        val breedSheetFragment = BreedBottomSheet { breed ->
            binding.petBreedBottomSheet.text = breed.petKindName
            petKindId = breed.petKindId
        }

        val bundle = Bundle()
        val petType = petViewModel.getPetType()
        bundle.putString("petType", petType)

        breedSheetFragment.arguments = bundle
        breedSheetFragment.show(childFragmentManager, breedSheetFragment.tag)
    }

    private fun setTrueDontKnow() = with(binding) {
        petBreedBottomSheet.text = getString(R.string.dont_know_text)
        petBreedBottomSheet.isClickable = false
    }

    private fun setFalseDontKnow() = with(binding) {
        petBreedBottomSheet.text = getString(R.string.pet_breed_hint)
        petBreedBottomSheet.isClickable = true
    }

    private fun initPetNameView() = with(binding) {
        petViewModel.petNameResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        setValidPetName()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        setInValidPetName(getString(R.string.already_used_petname_text))
                    }
                }
            }
        }
    }

    private fun setValidPetName() = with(binding) {
        petNameValidate.text = getString(R.string.valid_petname_text)
        petNameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
        petNameValidate.visibility = View.VISIBLE
        petNameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
        petViewModel.setValidPetName(true)
    }

    private fun editTextWatch() {
        binding.petWeightEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                petViewModel.setWeight(s.toString().isNotBlank())
            }

        })
    }

    private fun initPetView() = with(binding) {
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

                        if (args.isAdd) {
                            root.findNavController().popBackStack()
                        } else {
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

    private fun initSubmitButton() {
        petViewModel.submit.observe(viewLifecycleOwner) {
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

    private fun postPetInfo(petInfo: PetInfo) {
        petViewModel.postPet(familyId, jwt, petInfo)
    }

    private fun modifyPet(
        petId: Int,
        modifyPetRequest: ModifyPetRequest
    ) {
        petViewModel.modifyPet(familyId, petId, jwt, modifyPetRequest)
    }

    private fun observeModifyPet() = with(binding) {
        petViewModel.modifyPetResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        root.findNavController().popBackStack()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
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