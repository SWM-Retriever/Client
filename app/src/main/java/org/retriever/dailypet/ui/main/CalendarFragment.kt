package org.retriever.dailypet.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import org.retriever.dailypet.databinding.FragmentCalendarBinding
import org.retriever.dailypet.ui.base.BaseFragment

class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCalendarBinding {
        return FragmentCalendarBinding.inflate(inflater, container, false)
    }


}