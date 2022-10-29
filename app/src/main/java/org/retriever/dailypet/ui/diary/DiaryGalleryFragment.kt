package org.retriever.dailypet.ui.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentDiaryGalleryBinding
import org.retriever.dailypet.ui.base.BaseFragment

class DiaryGalleryFragment : BaseFragment<FragmentDiaryGalleryBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryGalleryBinding {
        return FragmentDiaryGalleryBinding.inflate(inflater, container, false)
    }

}