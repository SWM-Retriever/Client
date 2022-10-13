package org.retriever.dailypet.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.databinding.FragmentAddCareBinding
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.main.viewmodel.HomeViewModel
import org.retriever.dailypet.util.hideProgressCircular

class AddCareFragment : BaseFragment<FragmentAddCareBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddCareBinding {
        return FragmentAddCareBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding){
        btnFood.setOnClickListener{

        }
        btnWalk.setOnClickListener{

        }
    }


}