package org.retriever.dailypet.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
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

        btnNext.setOnClickListener {
            if (check1.isChecked && check2.isChecked) {
                val args: TermOfServiceFragmentArgs by navArgs()
                val registerProfile = args.registerProfile
                registerProfile.isProfileInformationAgree = check3.isChecked
                registerProfile.isPushAgree = check4.isChecked

                val action = TermOfServiceFragmentDirections.actionTermOfServiceFragmentToCreateProfileFragment(registerProfile)
                root.findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "필수 약관을 모두 동의해주세요", Toast.LENGTH_SHORT).show()
            }

        }

        checkAll.setOnClickListener { onCheckChanged(checkAll) }
        check1.setOnClickListener { onCheckChanged(check1) }
        check2.setOnClickListener { onCheckChanged(check2) }
        check3.setOnClickListener { onCheckChanged(check3) }
        check4.setOnClickListener { onCheckChanged(check4) }

        //TODO 약관보기 나중에 해야함

        imgbtnBack.setOnClickListener {
            root.findNavController().popBackStack()
        }
    }

    private fun onCheckChanged(checkBox: CheckBox) = with(binding) {
        when (checkBox.id) {
            checkAll.id -> {
                if (checkAll.isChecked) {
                    check1.isChecked = true
                    check2.isChecked = true
                    check3.isChecked = true
                    check4.isChecked = true
                } else {
                    check1.isChecked = false
                    check2.isChecked = false
                    check3.isChecked = false
                    check4.isChecked = false
                }
            }
            else -> {
                checkAll.isChecked = (check1.isChecked
                        && check2.isChecked
                        && check3.isChecked
                        && check4.isChecked)
            }
        }
        if (check1.isChecked && check2.isChecked) {
            btnNext.setBackgroundColor(resources.getColor(R.color.main_blue))
            btnNext.setTextColor(Color.WHITE)
        } else {
            btnNext.setBackgroundColor(resources.getColor(R.color.light_grey))
            btnNext.setTextColor(Color.BLACK)
        }
    }

}