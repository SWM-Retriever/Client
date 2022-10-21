package org.retriever.dailypet.ui.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreatePetBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.PetInfo
import org.retriever.dailypet.model.signup.pet.PetResponse
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.bottomsheet.BreedBottomSheet
import org.retriever.dailypet.ui.bottomsheet.CameraBottomSheet
import org.retriever.dailypet.ui.signup.viewmodel.PetViewModel
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular
import java.text.SimpleDateFormat
import java.util.*

class CreatePetFragment : BaseFragment<FragmentCreatePetBinding>() {

    private val petViewModel by activityViewModels<PetViewModel>()

    private lateinit var onBackCallBack: OnBackPressedCallback

    private val jwt = GlobalApplication.prefs.jwt ?: ""

    private var bitmap: Bitmap? = null

    private var datePicker: MaterialDatePicker<Long>? = null

    private var petKindId = -1

    private var dontKnow = false

    private val args: CreatePetFragmentArgs by navArgs()

    private val galleryResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().contentResolver, it))
                } else {
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                }
                bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
                binding.petCircleImage.setImageBitmap(bitmap)
            }
        }
    }

    private val cameraResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            bitmap = result.data?.extras?.get("data") as Bitmap
            bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
            binding.petCircleImage.setImageBitmap(bitmap)
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreatePetBinding {
        return FragmentCreatePetBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initCallBack()
    }

    private fun initCallBack() {
        onBackCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        petViewModel.setInitial()
        initProgressCircular()
        buttonClick()
        editTextWatch()
        initPetNameView()
        initPetView()
        initSubmitButton()
        initArgsView()
    }

    private fun initArgsView() = with(binding) {
        args.petDetailItem?.let {
            //TODO 이미지 설정

            createPetTitleText.text = getString(R.string.pet_modify_title)
            petSubmitButton.text = getString(R.string.modify_text)

            petNameEdittext.setText(it.petName)
            setValidPetName()

            if (it.gender == "MALE") {
                setMaleType()
            } else {
                setFemaleType()
            }

            petBirthDatePicker.text = it.birthDate
            petViewModel.setBirth()

            petBreedBottomSheet.text = it.petKind

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
            showCameraBottomSheetDialog()
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
            val name = petNameEdittext.text.toString()
            val type = petViewModel.getPetType()
            val sex = petViewModel.getSexType()
            val birth = petBirthDatePicker.text.toString()
            val weight = petWeightEdittext.text.toString().toFloat()
            val neutral = neutralRadio.isChecked
            val registerNum = petRegisterNumEdittext.text.toString()
            postPetInfo(name, type, sex, birth, weight, neutral, registerNum)
        }

    }

    private fun showCameraBottomSheetDialog() {
        val cameraSheetFragment = CameraBottomSheet {
            when (it) {
                0 -> takePicture()
                1 -> openGallery()
            }
        }
        cameraSheetFragment.show(childFragmentManager, cameraSheetFragment.tag)
    }

    private fun takePicture() {
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (cameraPermission == PackageManager.PERMISSION_DENIED || writePermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CAMERA
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraResult.launch(intent)
        }
    }

    private fun openGallery() {
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)

        if (readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_GALLERY
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/&")
            galleryResult.launch(intent)
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
        val familyId = GlobalApplication.prefs.familyId
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
                                petList = it.petList
                            )
                        }

                        if (args.petDetailItem == null && args.isAdd) {
                            root.findNavController().popBackStack()
                        } else if (args.petDetailItem != null && !args.isAdd) {
                            // TODO 수정 API로 변경
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

    private fun postPetInfo(
        name: String,
        type: String,
        sex: String,
        birth: String,
        weight: Float,
        neutral: Boolean,
        registerNumber: String,
    ) {
        val familyId = GlobalApplication.prefs.familyId
        val petInfo = PetInfo(name, type, sex, birth, petKindId, weight, neutral, registerNumber, "")
        val bitmapRequestBody = bitmap!!.let { BitmapRequestBody(it) }
        val multiPartBody = MultipartBody.Part.createFormData("image", "image", bitmapRequestBody)
        petViewModel.postPet(familyId, jwt, petInfo)
    }

    inner class BitmapRequestBody(private val bitmap: Bitmap) : RequestBody() {
        override fun contentType(): MediaType = "image/jpeg".toMediaType()
        override fun writeTo(sink: BufferedSink) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, sink.outputStream())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        datePicker = null
    }

    override fun onDestroy() {
        super.onDestroy()

        onBackCallBack.remove()
    }

    companion object {
        private const val REQUEST_GALLERY = 1
        private const val REQUEST_CAMERA = 2
        private const val MALE = "MALE"
        private const val FEMALE = "FEMALE"
    }

}