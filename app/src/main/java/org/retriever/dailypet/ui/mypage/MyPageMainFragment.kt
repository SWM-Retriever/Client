package org.retriever.dailypet.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentMyPageMainBinding
import org.retriever.dailypet.ui.base.BaseFragment

class MyPageMainFragment : BaseFragment<FragmentMyPageMainBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPageMainBinding {
        return FragmentMyPageMainBinding.inflate(inflater, container, false)
    }

}