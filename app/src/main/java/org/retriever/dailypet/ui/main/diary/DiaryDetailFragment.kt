package org.retriever.dailypet.ui.main.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentDiaryDetailBinding
import org.retriever.dailypet.ui.base.BaseFragment

class DiaryDetailFragment : BaseFragment<FragmentDiaryDetailBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryDetailBinding {
        return FragmentDiaryDetailBinding.inflate(inflater, container, false)
    }

}