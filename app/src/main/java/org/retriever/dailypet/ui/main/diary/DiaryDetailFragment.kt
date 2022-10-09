package org.retriever.dailypet.ui.main.diary

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentDiaryDetailBinding
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.ui.base.BaseFragment

class DiaryDetailFragment : BaseFragment<FragmentDiaryDetailBinding>() {

    private val args: DiaryDetailFragmentArgs by navArgs()

    private lateinit var item: DiaryItem

    private lateinit var popUpWindow: ListPopupWindow
    private val popUpWindowItems = listOf(MODIFY, DELETE)

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryDetailBinding {
        return FragmentDiaryDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initItem()
        initView()
        initPopUp()
        moreButtonClick()
    }

    private fun initItem() {
        item = args.diaryItem
    }

    private fun initView() = with(binding) {
        writerNickNameText.text = item.authorNickName
        diaryContentText.text = item.diaryText
    }

    private fun initPopUp() {
        popUpWindow = ListPopupWindow(requireContext(), null, com.airbnb.lottie.R.attr.listPopupWindowStyle)
        popUpWindow.anchorView = binding.moreButton

        val adapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, popUpWindowItems)
        popUpWindow.setAdapter(adapter)

        popUpWindow.setOnItemClickListener { _, _, _, id ->
            if(id == 0L){
                
            }else{

            }
            popUpWindow.dismiss()
        }

    }

    private fun moreButtonClick() {
        binding.moreButton.setOnClickListener {
            popUpWindow.show()
        }
    }

    companion object {
        private const val MODIFY = "수정하기"
        private const val DELETE = "삭제하기"
    }

}