package org.retriever.dailypet.test.ui.login

import android.Manifest.permission.*
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.SelectFamilyTypeActivity
import org.retriever.dailypet.databinding.FragmentCreateProfileBinding
import org.retriever.dailypet.test.model.Resource
import org.retriever.dailypet.test.model.login.FragmentCameraSheet
import org.retriever.dailypet.test.model.login.RegisterProfile
import org.retriever.dailypet.test.ui.base.BaseFragment
import org.retriever.dailypet.test.util.hideProgressCircular
import org.retriever.dailypet.test.util.showProgressCircular

class CreateProfileFragment : BaseFragment<FragmentCreateProfileBinding>() {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private var domain = ""
    private var option1 = false
    private var option2 = false
    private var bitmap: Bitmap? = null
    private var isValidNickname: Boolean = false

    private val galleryResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().contentResolver, it))
                } else {
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                }
                bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
                binding.imgCreateProfilePhoto.setImageBitmap(bitmap)
            }
        }
    }

    private val cameraResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            bitmap = result.data?.extras?.get("data") as Bitmap
            bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
            binding.imgCreateProfilePhoto.setImageBitmap(bitmap)
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreateProfileBinding {
        return FragmentCreateProfileBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initNickNameView()
        initProfileView()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initNickNameView() = with(binding) {
        loginViewModel.nickNameResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    textProfileNicknameValidate.text = resources.getString(R.string.valid_nickname_text)
                    textProfileNicknameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
                    textCreateProfileNickname.background = ContextCompat.getDrawable(requireContext(), R.drawable.success_edittext)
                    btnCreateProfileSubmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
                    btnCreateProfileSubmit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    isValidNickname = true
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)

                    when (response.code) {
                        CODE_INVALID_NICKNAME -> {
                            binding.textProfileNicknameValidate.text = resources.getString(R.string.already_used_nickname_text)
                            textProfileNicknameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                            textCreateProfileNickname.background = ContextCompat.getDrawable(requireContext(), R.drawable.fail_edittext)
                            btnCreateProfileSubmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
                            btnCreateProfileSubmit.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
                            isValidNickname = false
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
        loginViewModel.registerProfileResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    val jwt = response.data?.jwtToken
                    GlobalApplication.prefs.jwt = jwt

                    Toast.makeText(requireContext(), "프로필 등록에 성공하였습니다", Toast.LENGTH_SHORT).show()
                    val nextIntent = Intent(requireContext(), SelectFamilyTypeActivity::class.java)
                    startActivity(nextIntent) // 가족유형 선택 페이지로 이동
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "프로필 등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun buttonClick() = with(binding) {

        btnCreateProfileLoad.setOnClickListener {
            showBottomSheetDialog()
        }

        btnProfileNicknameCheck.setOnClickListener {
            val nickname = textCreateProfileNickname.text.toString()
            checkValidNickName(nickname)
        }

        btnCreateProfileSubmit.setOnClickListener {
            val nickname = textCreateProfileNickname.text.toString()
            val email = textRegisterProfileEmail.text.toString()
            checkValidNickName(nickname)

            if (isValidNickname) {
                val deviceToken = GlobalApplication.prefs.getString("deviceToken", "")
                if (deviceToken.isEmpty()) {
                    Log.e(TAG, "Device Token Error")
                } else {
                    postProfileInfo(nickname, email, deviceToken)
                }
            } else {
                Toast.makeText(requireContext(), "닉네임 중복검사를 진행해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        /* 이전버튼 */
        binding.imgbtnBack.setOnClickListener {
            //onBackPressed()
        }

    }

    private fun showBottomSheetDialog() {
        val cameraSheetFragment : FragmentCameraSheet = FragmentCameraSheet {
            when(it){
                0 -> takePicture()
                1 -> openGallery()
            }
        }
        cameraSheetFragment.show(childFragmentManager, cameraSheetFragment.tag)
    }

    private fun openGallery() {
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)

        if (readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(READ_EXTERNAL_STORAGE), REQUEST_GALLERY)
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/&")
            galleryResult.launch(intent)
        }
    }

    private fun takePicture() {
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), CAMERA)
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE)

        if (cameraPermission == PackageManager.PERMISSION_DENIED || writePermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA)
        } else {
            val intent = Intent(ACTION_IMAGE_CAPTURE)
            cameraResult.launch(intent)
        }
    }

    inner class BitmapRequestBody(private val bitmap: Bitmap) : RequestBody() {
        override fun contentType(): MediaType = "image/jpeg".toMediaType()
        override fun writeTo(sink: BufferedSink) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, sink.outputStream())
        }
    }

    private fun checkValidNickName(nickName: String) = with(binding) {
        if (nickName.isBlank()) {
            textProfileNicknameValidate.text = resources.getString(R.string.invalid_nickname_text)
            textProfileNicknameValidate.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
            textCreateProfileNickname.background = ContextCompat.getDrawable(requireContext(), R.drawable.fail_edittext)
            btnCreateProfileSubmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
            btnCreateProfileSubmit.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            isValidNickname = false
        } else {
            loginViewModel.postCheckProfileNickname(nickName)
        }
    }

    private fun postProfileInfo(nickname: String, email: String, deviceToken: String) {
        val registerProfile = RegisterProfile(nickname, email, domain, deviceToken, option1, option2)
        val bitmapRequestBody = bitmap!!.let { BitmapRequestBody(it) }
        val multiPartBody = MultipartBody.Part.createFormData("image", "image", bitmapRequestBody)

        loginViewModel.postProfile(registerProfile, multiPartBody)
    }

    companion object {
        private const val TAG = "CREATE PROFILE"
        private const val CODE_INVALID_NICKNAME = 409
        private const val CODE_FAIL = 500
        private const val REQUEST_GALLERY = 1
        private const val REQUEST_CAMERA = 2
    }

}