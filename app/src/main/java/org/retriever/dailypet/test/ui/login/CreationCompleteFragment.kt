package org.retriever.dailypet.test.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.retriever.dailypet.MainActivity
import org.retriever.dailypet.databinding.FragmentCreationCompleteBinding
import org.retriever.dailypet.test.ui.base.BaseFragment
import org.retriever.dailypet.test.util.hideProgressCircular

class CreationCompleteFragment : BaseFragment<FragmentCreationCompleteBinding>(){
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreationCompleteBinding {
        return FragmentCreationCompleteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        buttonClick()
    }

    private fun initProgressCircular(){
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding){

        btnAdd.setOnClickListener{

            val nextIntent = Intent(requireContext(), CreatePetActivity::class.java)
            startActivity(nextIntent)
        }

        btnStart.setOnClickListener{
            val nextIntent = Intent(requireContext(), MainActivity::class.java)
            startActivity(nextIntent)
        }

    }
}