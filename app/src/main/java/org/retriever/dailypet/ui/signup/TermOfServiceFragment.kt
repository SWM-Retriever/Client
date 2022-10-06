package org.retriever.dailypet.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentTermOfServiceBinding
import org.retriever.dailypet.ui.base.BaseFragment

class TermOfServiceFragment : BaseFragment<FragmentTermOfServiceBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTermOfServiceBinding {
        return FragmentTermOfServiceBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClick()
    }

    private fun buttonClick() = with(binding) {

        nextButton.setOnClickListener {
            if (firstCheck.isChecked && secondCheck.isChecked) {
                val args: TermOfServiceFragmentArgs by navArgs()
                val registerProfile = args.registerProfile
                registerProfile.isProfileInformationAgree = thirdCheck.isChecked
                registerProfile.isPushAgree = fourthCheck.isChecked

                val action = TermOfServiceFragmentDirections.actionTermOfServiceFragmentToCreateProfileFragment(registerProfile)
                root.findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "필수 약관을 모두 동의해주세요", Toast.LENGTH_SHORT).show()
            }

        }

        checkAll.setOnClickListener { onCheckChanged(checkAll) }
        firstCheck.setOnClickListener { onCheckChanged(firstCheck) }
        secondCheck.setOnClickListener { onCheckChanged(secondCheck) }
        thirdCheck.setOnClickListener { onCheckChanged(thirdCheck) }
        fourthCheck.setOnClickListener { onCheckChanged(fourthCheck) }

        //TODO 약관보기 나중에 해야함

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }
    }

    private fun onCheckChanged(checkBox: CheckBox) = with(binding) {
        when (checkBox.id) {
            checkAll.id -> {
                if (checkAll.isChecked) {
                    firstCheck.isChecked = true
                    secondCheck.isChecked = true
                    thirdCheck.isChecked = true
                    fourthCheck.isChecked = true
                } else {
                    firstCheck.isChecked = false
                    secondCheck.isChecked = false
                    thirdCheck.isChecked = false
                    fourthCheck.isChecked = false
                }
            }
            else -> {
                checkAll.isChecked = (firstCheck.isChecked
                        && secondCheck.isChecked
                        && thirdCheck.isChecked
                        && fourthCheck.isChecked)
            }
        }
        if (firstCheck.isChecked && secondCheck.isChecked) {
            nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
            nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_light_grey))
        }
    }

}