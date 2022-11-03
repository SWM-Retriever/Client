package org.retriever.dailypet.ui.signup.firstcourse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentTermOfServiceBinding
import org.retriever.dailypet.ui.base.BaseFragment

class TermOfServiceFragment : BaseFragment<FragmentTermOfServiceBinding>() {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTermOfServiceBinding {
        return FragmentTermOfServiceBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkButtonClick()
        observeAllCheckState()
        observeFirstCheckState()
        observeSecondCheckState()
        observeThirdCheckState()
        observeNextButtonState()
        buttonClick()
    }

    private fun checkButtonClick() = with(binding) {
        checkAll.setOnClickListener {
            profileViewModel.setAllCheckState(checkAll.isChecked)
        }

        firstCheck.setOnClickListener {
            profileViewModel.setFirstCheckState(firstCheck.isChecked)
        }

        secondCheck.setOnClickListener {
            profileViewModel.setSecondCheckState(secondCheck.isChecked)
        }

        thirdCheck.setOnClickListener {
            profileViewModel.setThirdCheckState(thirdCheck.isChecked)
        }
    }

    private fun observeAllCheckState() {
        profileViewModel.allCheckState.asLiveData().observe(viewLifecycleOwner) {
            binding.checkAll.isChecked = it
        }
    }

    private fun observeFirstCheckState() {
        profileViewModel.firstCheckState.asLiveData().observe(viewLifecycleOwner) {
            binding.firstCheck.isChecked = it
        }
    }

    private fun observeSecondCheckState() {
        profileViewModel.secondCheckState.asLiveData().observe(viewLifecycleOwner) {
            binding.secondCheck.isChecked = it
        }
    }

    private fun observeThirdCheckState() {
        profileViewModel.thirdCheckState.asLiveData().observe(viewLifecycleOwner) {
            binding.thirdCheck.isChecked = it
        }
    }

    private fun observeNextButtonState() {
        profileViewModel.nextButtonState.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                setNextButtonValid()
            } else {
                setNextButtonInValid()
            }
        }
    }

    private fun buttonClick() = with(binding) {

        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }

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

        nextButton.setOnClickListener {
            moveCreateProfileFragment()
        }

    }

    private fun moveCreateProfileFragment() = with(binding) {
        val args: TermOfServiceFragmentArgs by navArgs()
        val registerProfile = args.registerProfile

        registerProfile.isProfileInformationAgree = thirdCheck.isChecked
        registerProfile.isPushAgree = thirdCheck.isChecked

        val action = TermOfServiceFragmentDirections.actionTermOfServiceFragmentToCreateProfileFragment(registerProfile)
        root.findNavController().navigate(action)
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

    companion object {
        private const val TERMS_URL = "https://showy-king-303.notion.site/df847ac24e894e4a837717776a7dd4b7"
        private const val PRIVACY_URL = "https://showy-king-303.notion.site/c3dd318460424ae5ae0d13ebef8cdc48"
        private const val MARKETING_URL = "https://showy-king-303.notion.site/25ae8e794dc44c6a801adcfb8850ea0f"
    }

}