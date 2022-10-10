package org.retriever.dailypet.ui.main.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentDiaryRegisterBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.model.diary.DiaryPost
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class DiaryRegisterFragment : BaseFragment<FragmentDiaryRegisterBinding>() {

    private val diaryViewModel by activityViewModels<DiaryViewModel>()

    private val familyId = GlobalApplication.prefs.familyId
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    private val args: DiaryRegisterFragmentArgs by navArgs()
    private lateinit var diaryItem: DiaryItem

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryRegisterBinding {
        return FragmentDiaryRegisterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getItemFromArgs()
        initView()
        initCircularProgress()
        completeButtonClick()
        observePostResponse()
        observeUpdateResponse()
    }

    private fun getItemFromArgs() {
        diaryItem = args.diaryItem
    }

    private fun initView() {
        if (!diaryItem.diaryText.isNullOrBlank()) {
            binding.contentEdittext.setText(diaryItem.diaryText)
            binding.diaryRegisterTitle.text = getString(R.string.diary_modify_title)
        }
    }

    private fun initCircularProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun completeButtonClick() = with(binding) {
        completeButton.setOnClickListener {
            if (contentEdittext.text.isNotBlank()) {
                callApi()
            } else {
                Toast.makeText(requireContext(), "일기를 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callApi() {
        val text = binding.contentEdittext.text.toString()
        val image = ""
        val diaryPost = DiaryPost(text, image)

        if (diaryItem.diaryText.isNullOrBlank()) {
            postDiary(diaryPost)
        } else {
            val diaryId = diaryItem.diaryId ?: -1
            updateDiary(diaryId, diaryPost)
        }
    }

    private fun postDiary(diaryPost: DiaryPost) {
        diaryViewModel.postDiary(familyId, jwt, diaryPost)
    }

    private fun updateDiary(diaryId: Int, diaryPost: DiaryPost) {
        diaryViewModel.updateDiary(familyId, diaryId, jwt, diaryPost)
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

}