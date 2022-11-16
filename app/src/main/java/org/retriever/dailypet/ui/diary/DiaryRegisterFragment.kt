package org.retriever.dailypet.ui.diary

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentDiaryRegisterBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.model.diary.DiaryPost
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular
import java.io.File
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DiaryRegisterFragment : BaseFragment<FragmentDiaryRegisterBinding>() {

    private val diaryViewModel by viewModels<DiaryViewModel>()

    private val familyId = GlobalApplication.prefs.familyId
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val args: DiaryRegisterFragmentArgs by navArgs()
    private lateinit var diaryItem: DiaryItem
    private var file: File? = null
    private lateinit var fileUri: Uri
    private var imageUrl = ""
    private var mNow : Long = 0
    private lateinit var mDate : Date


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryRegisterBinding {
        return FragmentDiaryRegisterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getItemFromArgs()
        initView()
        initCircularProgress()
        buttonClick()
        observePostResponse()
        observeUpdateResponse()
        observePostImageResponse()
    }

    private fun getItemFromArgs() {
        diaryItem = args.diaryItem
    }

    private fun initView() = with(binding){
        diaryTimeText.text = getTime()
        if (!diaryItem.diaryText.isNullOrBlank()) {
            contentEdittext.setText(diaryItem.diaryText)
            diaryRegisterTitle.text = getString(R.string.diary_modify_title)
        }
    }

    private fun initCircularProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding){
        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }
        completeButton.setOnClickListener {
            if (contentEdittext.text.isNotBlank()) {
                callApi()
            } else {
                Toast.makeText(requireContext(), "일기내용을 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }
        photoLoadButton.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop()
                .compress(2048)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
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
                    binding.contentImage.load(file)
                    binding.contentImage.visibility = View.VISIBLE
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "사진 등록 취소", Toast.LENGTH_SHORT).show()
                }

            }
        }

    private fun callApi() {
        val text = binding.contentEdittext.text.toString()
        val image = ""
        val diaryPost = DiaryPost(text, image)


        if (file != null) {
            file?.let {
                val requestFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("image", it.name, requestFile)
                diaryViewModel.postImage(S3_PATH, multipartBody)
            }
        } else{
            if (diaryItem.diaryText.isNullOrBlank()) {
                postDiary(diaryPost)
            }
            else{
                val diaryId = diaryItem.diaryId ?: -1
                updateDiary(diaryId, diaryPost)
            }
        }

    }

    private fun postDiary(diaryPost: DiaryPost) {
        diaryViewModel.postDiary(familyId, jwt, diaryPost)
    }

    private fun updateDiary(diaryId: Int, diaryPost: DiaryPost) {
        diaryViewModel.updateDiary(familyId, diaryId, jwt, diaryPost)
    }

    private fun observePostImageResponse() = with(binding){
        diaryViewModel.postImageResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }

                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    imageUrl = response.data?.imageUrl ?: ""
                    val diaryPost = DiaryPost(contentEdittext.text.toString(), imageUrl)
                    if (diaryItem.diaryText.isNullOrBlank()) {
                        postDiary(diaryPost)
                    } else{
                        val diaryId = diaryItem.diaryId ?: -1
                        updateDiary(diaryId, diaryPost)
                    }
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    Toast.makeText(requireContext(), "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT)
                }
            }
        }
    }

    private fun observePostResponse() = with(binding) {
        diaryViewModel.diaryPostResponse.observe(viewLifecycleOwner) { event ->
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
                        Toast.makeText(requireContext(), "등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeUpdateResponse() = with(binding) {
        diaryViewModel.diaryUpdateResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        root.findNavController().navigate(R.id.action_diaryRegisterFragment_to_diaryMainFragment)
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        Toast.makeText(requireContext(), "수정에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getTime() : String{
        mNow = currentTimeMillis()
        mDate = Date(mNow)
        val mFormat = SimpleDateFormat("yyyy-MM-dd")
        return mFormat.format(mDate).toString()
    }

    companion object {
        private const val TAG = "CREATE DIARY"
        private const val S3_PATH = "DIARY"
    }

}