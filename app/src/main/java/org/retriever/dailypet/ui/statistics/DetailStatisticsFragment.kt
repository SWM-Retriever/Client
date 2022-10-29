package org.retriever.dailypet.ui.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentDetailStatisticsBinding
import org.retriever.dailypet.ui.base.BaseFragment

class DetailStatisticsFragment : BaseFragment<FragmentDetailStatisticsBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDetailStatisticsBinding {
        return FragmentDetailStatisticsBinding.inflate(inflater, container, false)
    }

}