package org.retriever.dailypet.ui.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
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
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.bottomsheet.FragmentCameraSheet
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular
import java.text.SimpleDateFormat
import java.util.*

class CreatePetFragment : BaseFragment<FragmentCreatePetBinding>() {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val jwt = GlobalApplication.prefs.jwt ?: ""

    private var bitmap: Bitmap? = null

    private var datePicker : MaterialDatePicker<Long>? = null

    private var isValidPetName = false
    private var dog = false
    private var cat = false
    private var male = false
    private var female = false
    private var dontKnow = false
    private var submit = false

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
                binding.imgPhoto.setImageBitmap(bitmap)
            }
        }
    }

    private val cameraResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            bitmap = result.data?.extras?.get("data") as Bitmap
            bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
            binding.imgPhoto.setImageBitmap(bitmap)
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreatePetBinding {
        return FragmentCreatePetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        buttonClick()
        initPetNameView()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding) {

        btnLoadCamera.setOnClickListener {
            showBottomSheetDialog()
        }

        btnValidCheck.setOnClickListener {
            val petName = edittextPetName.text.toString()
            if (petName.isBlank()) {
                textPetNameValidate.text = getString(R.string.invalid_petname_text)
                edittextPetName.background = ContextCompat.getDrawable(requireContext(), R.drawable.fail_edittext)
                isValidPetName = false
                submitCheck()
            } else {
                checkValidPetName(petName)
            }
        }

        btnPetTypeDog.setOnClickListener {
            btnPetTypeDog.isSelected = true
            btnPetTypeCat.isSelected = false
            dog = true
            cat = false
            submitCheck()
        }

        btnPetTypeCat.setOnClickListener {
            btnPetTypeDog.isSelected = false
            btnPetTypeCat.isSelected = true
            dog = false
            cat = true
            submitCheck()
        }

        btnPetSexMale.setOnClickListener {
            btnPetSexMale.isSelected = true
            btnPetSexFemale.isSelected = false
            male = true
            female = false
            submitCheck()
        }

        btnPetSexFemale.setOnClickListener {
            btnPetSexFemale.isSelected = true
            btnPetSexMale.isSelected = false
            male = false
            female = true
            submitCheck()
        }

        editTextBirth.setOnClickListener {
            showDatePicker()
        }

        btnDontknow.setOnClickListener {
            dontKnow != dontKnow
            btnDontknow.isSelected = dontKnow
            submitCheck()
        }

        btnPetSubmit.setOnClickListener {
            if (submit) {
                val name = edittextPetName.text.toString()
                val type = if (dog) "Dog" else "Cat"
                val sex = if (male) "Male" else "Female"
                val birth = editTextBirth.text.toString()
                val breed = editTextBreed.text.toString()
                val weight = edittextWeight.text.toString().toFloat()
                val neutral = radioNeutral.isChecked
                val registerNum = edittextRegisterNum.text.toString()
                postPetInfo(name, type, sex, birth, breed, weight, neutral, registerNum)
            } else {
                Toast.makeText(requireContext(), "필수항목을 모두 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        imgbtnBack.setOnClickListener {
            root.findNavController().popBackStack()
        }

    }

    private fun showBottomSheetDialog() {
        val cameraSheetFragment = FragmentCameraSheet {
            when (it) {
                0 -> takePicture()
                1 -> openGallery()
            }
        }
        cameraSheetFragment.show(childFragmentManager, cameraSheetFragment.tag)
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

    private fun checkValidPetName(petName: String) {
        val familyId = GlobalApplication.prefs.familyId
        loginViewModel.postCheckPetName(familyId, jwt, petName)
    }

    private fun showDatePicker(){
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.input_pet_birth_text))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker?.apply {
            show(this@CreatePetFragment.requireActivity().supportFragmentManager, datePicker.toString())
            addOnPositiveButtonClickListener {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                binding.editTextBirth.text = date
            }
            addOnNegativeButtonClickListener {
                dismiss()
            }
        }
    }

    private fun initPetNameView() = with(binding) {
        loginViewModel.petNameResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    textPetNameValidate.text = getString(R.string.valid_petname_text)
                    textPetNameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
                    edittextPetName.background = ContextCompat.getDrawable(requireContext(), R.drawable.success_edittext)
                    isValidPetName = true
                    submitCheck()
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    textPetNameValidate.text = getString(R.string.invalid_petname_text)
                    textPetNameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                    edittextPetName.background = ContextCompat.getDrawable(requireContext(), R.drawable.fail_edittext)
                    isValidPetName = false
                    submitCheck()
                }
            }
        }
    }

    private fun submitCheck() = with(binding) {
        val name = isValidPetName
        val type = dog || cat
        val sex = male || female
        val birth = editTextBirth.text.isNotBlank()
        val weight = edittextWeight.text.isNotBlank()
        submit = name && type && sex && birth && weight

        if (submit) {
            btnPetSubmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            btnPetSubmit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            btnPetSubmit.isClickable = true
        } else {
            btnPetSubmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
            btnPetSubmit.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            btnPetSubmit.isClickable = false
        }
    }

    private fun postPetInfo(
        name: String, type: String, sex: String, birth: String, breed: String,
        weight: Float, neutral: Boolean, registerNumber: String
    ) {
        var bitmapMultipartBody: MultipartBody.Part? = null
        bitmap?.let {
            val bitmapRequestBody = BitmapRequestBody(it)
            bitmapMultipartBody = MultipartBody.Part.createFormData("image", "petImage", bitmapRequestBody)
        }

        //TODO POST 등록하기 해야함
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

    companion object {
        private const val REQUEST_GALLERY = 1
        private const val REQUEST_CAMERA = 2
    }

}