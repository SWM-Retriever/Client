package org.retriever.dailypet.ui.signup.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentGroupEntranceBinding
import org.retriever.dailypet.ui.base.BaseFragment

class GroupEntranceFragment : BaseFragment<FragmentGroupEntranceBinding>() {
    private var progressList: ArrayList<String> = arrayListOf("프로필","그룹","반려동물")

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentGroupEntranceBinding {
        return FragmentGroupEntranceBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpProgressbar.setStateDescriptionData(progressList)
        buttonClick()
    }

    private fun buttonClick() = with(binding) {

        groupCreateButton.setOnClickListener {
            val action = GroupEntranceFragmentDirections.actionFamilyEntranceFragmentToCreateFamilyFragment(false)
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