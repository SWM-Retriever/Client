package org.retriever.dailypet.ui.diary

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentDiaryDetailBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class DiaryDetailFragment : BaseFragment<FragmentDiaryDetailBinding>() {

    private val diaryViewModel by activityViewModels<DiaryViewModel>()

    private val familyId = GlobalApplication.prefs.familyId
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    private val args: DiaryDetailFragmentArgs by navArgs()

    private lateinit var item: DiaryItem

    private lateinit var popUpWindow: ListPopupWindow
    private val popUpWindowItems = listOf(MODIFY, DELETE)

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryDetailBinding {
        return FragmentDiaryDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initItem()
        initView()
        initPopUp()
        backButtonClick()
        moreButtonClick()
        observeResponse()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initItem() {
        item = args.diaryItem
    }

    private fun initView() = with(binding) {
        diaryContentText.movementMethod = ScrollingMovementMethod()
        writerNickNameText.text = item.authorNickName
        diaryContentText.text = item.diaryText
        if (item.authorImageUrl?.isNotEmpty() == true) {
            writerCircleImage.load(item.authorImageUrl)
        }
        if (!item.diaryImageUrl.isNullOrEmpty()) {
            diaryImage.load(item.diaryImageUrl)
            diaryImage.visibility = View.VISIBLE
        } else {
            diaryImage.visibility = View.GONE
        }
    }

    private fun initPopUp() {
        popUpWindow = ListPopupWindow(requireContext(), null, com.airbnb.lottie.R.attr.listPopupWindowStyle)
        popUpWindow.anchorView = binding.moreButton

        val adapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, popUpWindowItems)
        popUpWindow.setAdapter(adapter)

        popUpWindow.setOnItemClickListener { _, _, _, id ->
            if (id == 0L) {
                moveDiaryRegisterFragment()
            } else {
                deleteDiary()
            }
            popUpWindow.dismiss()
        }

    }

    private fun backButtonClick() {
        binding.backButton.setOnClickListener {
            binding.root.findNavController().popBackStack()
        }
    }

    private fun moreButtonClick() {
        binding.moreButton.setOnClickListener {
            popUpWindow.show()
        }
    }

    private fun moveDiaryRegisterFragment() {
        val action = DiaryDetailFragmentDirections.actionDiaryDetailFragmentToDiaryRegisterFragment(item)
        binding.root.findNavController().navigate(action)
    }

    private fun deleteDiary() {
        diaryViewModel.deleteDiary(familyId, item.diaryId ?: -1, jwt)
    }

    private fun observeResponse() = with(binding) {
        diaryViewModel.diaryDeleteResponse.observe(viewLifecycleOwner) { event ->
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
                        Toast.makeText(requireContext(), "삭제에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val MODIFY = "수정하기"
        private const val DELETE = "삭제하기"
    }

}