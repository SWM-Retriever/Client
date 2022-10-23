package org.retriever.dailypet.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentSelectFamilyTypeBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.signup.viewmodel.FamilyViewModel
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class SelectFamilyTypeFragment : BaseFragment<FragmentSelectFamilyTypeBinding>() {

    private val familyViewModel by activityViewModels<FamilyViewModel>()

    private lateinit var onBackCallBack: OnBackPressedCallback

    private val jwt = GlobalApplication.prefs.jwt ?: ""

    private var alone = false
    private var group = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSelectFamilyTypeBinding {
        return FragmentSelectFamilyTypeBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initCallBack()
    }

    private fun initCallBack() {
        onBackCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgress()
        buttonClick()
        observeMakeAlone()
    }

    private fun initProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding) {

        selectAloneButton.setOnClickListener {
            alone = true
            group = false
            selectAloneButton.isSelected = true
            selectGroupButton.isSelected = false
            aloneImageCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main_blue))
            groupImageCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            chooseCompleteButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            chooseCompleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            chooseCompleteButton.isClickable = true
        }

        selectGroupButton.setOnClickListener {
            alone = false
            group = true
            selectAloneButton.isSelected = false
            selectGroupButton.isSelected = true
            aloneImageCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            groupImageCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main_blue))
            chooseCompleteButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            chooseCompleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            chooseCompleteButton.isClickable = true
        }

        chooseCompleteButton.setOnClickListener {
            if (alone) {
                familyViewModel.makeAlone(jwt)
            } else if (group) {
                root.findNavController().navigate(R.id.action_selectFamilyTypeFragment_to_familyEntranceFragment)
            } else {
                Toast.makeText(requireContext(), "양육 유형을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun observeMakeAlone() = with(binding) {
        familyViewModel.registerFamilyResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        GlobalApplication.prefs.familyId = response.data?.familyId ?: -1
                        root.findNavController().navigate(R.id.action_selectFamilyTypeFragment_to_createPetFragment)
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        onBackCallBack.remove()
    }

}