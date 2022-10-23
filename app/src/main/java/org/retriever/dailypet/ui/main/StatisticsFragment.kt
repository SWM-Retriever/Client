package org.retriever.dailypet.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentStatisticsBinding
import org.retriever.dailypet.ui.base.BaseFragment

class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentStatisticsBinding {
        return FragmentStatisticsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}