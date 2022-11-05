package org.retriever.dailypet.ui.signup.group

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentSelectFamilyTypeBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class SelectGroupTypeFragment : BaseFragment<FragmentSelectFamilyTypeBinding>() {

    private val groupViewModel by activityViewModels<GroupViewModel>()

    private lateinit var onBackCallBack: OnBackPressedCallback

    private val jwt = GlobalApplication.prefs.jwt ?: ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSelectFamilyTypeBinding {
        return FragmentSelectFamilyTypeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCallBack()
        initProgress()
        buttonClick()
        observeAloneButtonState()
        observeGroupButtonState()
        observeChooseButtonState()
        observeMakeAlone()
    }

    private fun initCallBack() {
        onBackCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackCallBack)
    }

    private fun initProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding) {

        selectAloneButton.setOnClickListener {
            groupViewModel.setAloneButtonState()
        }

        selectGroupButton.setOnClickListener {
            groupViewModel.setGroupButtonState()
        }

        chooseCompleteButton.setOnClickListener {
            if (selectAloneButton.isSelected) {
                groupViewModel.makeAlone(jwt)
            } else {
                root.findNavController().navigate(R.id.action_selectFamilyTypeFragment_to_familyEntranceFragment)
            }
        }

    }

    private fun observeAloneButtonState() {
        groupViewModel.aloneButtonState.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                setAloneButtonValidView()
            } else {
                setAloneButtonInvalidView()
            }
        }
    }

    private fun setAloneButtonValidView() = with(binding) {
        selectAloneButton.isSelected = true
        aloneImageCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main_blue))
    }

    private fun setAloneButtonInvalidView() = with(binding) {
        selectAloneButton.isSelected = false
        aloneImageCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
    }

    private fun observeGroupButtonState() {
        groupViewModel.groupButtonState.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                setGroupButtonValidView()
            } else {
                setGroupButtonInvalidView()
            }
        }
    }

    private fun setGroupButtonValidView() = with(binding) {
        selectGroupButton.isSelected = true
        groupImageCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main_blue))
    }

    private fun setGroupButtonInvalidView() = with(binding) {
        selectGroupButton.isSelected = false
        groupImageCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
    }

    private fun observeChooseButtonState() {
        groupViewModel.chooseButtonState.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                setChooseButtonValidView()
            }
        }
    }

    private fun setChooseButtonValidView() = with(binding) {
        chooseCompleteButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
        chooseCompleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        chooseCompleteButton.isClickable = true
    }

    private fun observeMakeAlone() = with(binding) {
        groupViewModel.registerGroupResponse.observe(viewLifecycleOwner) { event ->
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

    override fun onDestroyView() {
        super.onDestroyView()

        onBackCallBack.remove()
    }

}