package org.retriever.dailypet.ui.main.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentDiaryMainBinding
import org.retriever.dailypet.ui.base.BaseFragment

class DiaryMainFragment : BaseFragment<FragmentDiaryMainBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryMainBinding {
        return FragmentDiaryMainBinding.inflate(inflater, container, false)
    }

}