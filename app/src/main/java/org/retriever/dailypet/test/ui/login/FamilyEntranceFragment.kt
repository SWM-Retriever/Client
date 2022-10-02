package org.retriever.dailypet.test.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import org.retriever.dailypet.CreateFamilyActivity
import org.retriever.dailypet.FindGroupActivity
import org.retriever.dailypet.databinding.FragmentFamilyEntranceBinding
import org.retriever.dailypet.test.ui.base.BaseFragment

class FamilyEntranceFragment : BaseFragment<FragmentFamilyEntranceBinding>(){

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFamilyEntranceBinding {
        return FragmentFamilyEntranceBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClick()
    }

    private fun buttonClick() = with(binding){

        btnCreate.setOnClickListener{
            val nextIntent = Intent(requireContext(), CreateFamilyActivity::class.java)
            startActivity(nextIntent)
        }

        textInviteCode.setOnClickListener{
            val nextIntent = Intent(requireContext(), FindGroupActivity::class.java)
            startActivity(nextIntent)
        }

        imgbtnBack.setOnClickListener{
            root.findNavController().popBackStack()
        }

    }

}