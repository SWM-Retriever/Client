package org.retriever.dailypet.ui.main.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentDiaryBinding
import org.retriever.dailypet.ui.base.BaseFragment

class DiaryFragment : BaseFragment<FragmentDiaryBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryBinding {
        return FragmentDiaryBinding.inflate(inflater, container, false)
    }

}