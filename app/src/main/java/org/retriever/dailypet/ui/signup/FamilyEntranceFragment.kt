package org.retriever.dailypet.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentFamilyEntranceBinding
import org.retriever.dailypet.ui.base.BaseFragment

class FamilyEntranceFragment : BaseFragment<FragmentFamilyEntranceBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFamilyEntranceBinding {
        return FragmentFamilyEntranceBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClick()
    }

    private fun buttonClick() = with(binding) {

        groupCreateButton.setOnClickListener {
            val action = FamilyEntranceFragmentDirections.actionFamilyEntranceFragmentToCreateFamilyFragment(false)
            root.findNavController().navigate(action)
        }

        inviteCodeText.setOnClickListener {
            root.findNavController().navigate(R.id.action_familyEntranceFragment_to_findGroupFragment)
        }

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }

    }

}