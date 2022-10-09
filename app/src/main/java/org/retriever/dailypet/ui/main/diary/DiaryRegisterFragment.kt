package org.retriever.dailypet.ui.main.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentDiaryRegisterBinding
import org.retriever.dailypet.ui.base.BaseFragment

class DiaryRegisterFragment : BaseFragment<FragmentDiaryRegisterBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryRegisterBinding {
        return FragmentDiaryRegisterBinding.inflate(inflater, container, false)
    }

}