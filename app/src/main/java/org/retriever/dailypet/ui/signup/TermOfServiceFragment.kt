package org.retriever.dailypet.ui.signup

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
                moveCreateProfileFragment()
            } else {
                Toast.makeText(requireContext(), "필수 약관을 모두 동의해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        checkAll.setOnClickListener { onCheckChanged(checkAll) }
        firstCheck.setOnClickListener { onCheckChanged(firstCheck) }
        secondCheck.setOnClickListener { onCheckChanged(secondCheck) }
        thirdCheck.setOnClickListener { onCheckChanged(thirdCheck) }

        firstSeeText.setOnClickListener {
            val action = TermOfServiceFragmentDirections.actionTermOfServiceFragmentToWebViewActivity2(TERMS_URL)
            root.findNavController().navigate(action)
        }
        secondSeeText.setOnClickListener {
            val action = TermOfServiceFragmentDirections.actionTermOfServiceFragmentToWebViewActivity2(PRIVACY_URL)
            root.findNavController().navigate(action)
        }
        thirdSeeText.setOnClickListener {
            val action = TermOfServiceFragmentDirections.actionTermOfServiceFragmentToWebViewActivity2(MARKETING_URL)
            root.findNavController().navigate(action)
        }

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }
    }

    private fun moveCreateProfileFragment() = with(binding) {
        val args: TermOfServiceFragmentArgs by navArgs()
        val registerProfile = args.registerProfile
        // TODO 백엔드보고 프로필정보 추가수집 지우기
        registerProfile.isProfileInformationAgree = thirdCheck.isChecked
        registerProfile.isPushAgree = thirdCheck.isChecked

        val action = TermOfServiceFragmentDirections.actionTermOfServiceFragmentToCreateProfileFragment(registerProfile)
        root.findNavController().navigate(action)
    }

    private fun onCheckChanged(checkBox: CheckBox) = with(binding) {
        when (checkBox.id) {
            checkAll.id -> {
                if (checkAll.isChecked) {
                    setAllCheckedTrue()
                } else {
                    setAllCheckedFalse()
                }
            }
            else -> {
                checkAll.isChecked = (firstCheck.isChecked
                                && secondCheck.isChecked
                                && thirdCheck.isChecked)
            }
        }
        if (firstCheck.isChecked && secondCheck.isChecked) {
            setNextButtonValid()
        } else {
            setNextButtonInValid()
        }
    }

    private fun setAllCheckedTrue() = with(binding) {
        firstCheck.isChecked = true
        secondCheck.isChecked = true
        thirdCheck.isChecked = true
    }

    private fun setAllCheckedFalse() = with(binding) {
        firstCheck.isChecked = false
        secondCheck.isChecked = false
        thirdCheck.isChecked = false
    }

    private fun setNextButtonValid() = with(binding) {
        nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
        nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        nextButton.isClickable = true
    }

    private fun setNextButtonInValid() = with(binding) {
        nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
        nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_light_grey))
        nextButton.isClickable = false
    }

    companion object{
        private const val TERMS_URL = "https://showy-king-303.notion.site/df847ac24e894e4a837717776a7dd4b7"
        private const val PRIVACY_URL = "https://showy-king-303.notion.site/c3dd318460424ae5ae0d13ebef8cdc48"
        private const val MARKETING_URL = "https://showy-king-303.notion.site/25ae8e794dc44c6a801adcfb8850ea0f"
    }

}