package org.retriever.dailypet.ui.main.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.databinding.FragmentDiaryRegisterBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryPost
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class DiaryRegisterFragment : BaseFragment<FragmentDiaryRegisterBinding>() {

    private val diaryViewModel by activityViewModels<DiaryViewModel>()

    private val familyId = GlobalApplication.prefs.familyId
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryRegisterBinding {
        return FragmentDiaryRegisterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCircularProgress()
        completeButtonClick()
        observeResponse()
    }

    private fun initCircularProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun completeButtonClick() = with(binding) {
        completeButton.setOnClickListener {
            if (contentEdittext.text.isNotBlank()) {
                val text = contentEdittext.text.toString()
                val image = ""
                postDiary(DiaryPost(text, image))
            } else {
                Toast.makeText(requireContext(), "일기를 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun postDiary(diaryPost: DiaryPost) {
        diaryViewModel.postDiary(familyId, jwt, diaryPost)
    }

    private fun observeResponse() = with(binding) {
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

}