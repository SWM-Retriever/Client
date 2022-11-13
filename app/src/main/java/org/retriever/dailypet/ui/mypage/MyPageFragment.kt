package org.retriever.dailypet.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentMyPageBinding
import org.retriever.dailypet.ui.base.BaseFragment

class MyPageFragment : BaseFragment<FragmentMyPageBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPageBinding {
        return FragmentMyPageBinding.inflate(inflater, container, false)
    }
}