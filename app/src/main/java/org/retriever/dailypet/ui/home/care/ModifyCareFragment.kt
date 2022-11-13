package org.retriever.dailypet.ui.home.care

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentModifyCareBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.CareModifyInfo
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class ModifyCareFragment : BaseFragment<FragmentModifyCareBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private var petId = -1
    private var careId = -1
    private var careName = ""
    private var dayList: MutableList<Boolean> = mutableListOf(false, false, false, false, false, false, false)
    private val dayName: List<String> = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    private var SUBMIT = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentModifyCareBinding {
        return FragmentModifyCareBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressCircular()
        initPatchCare()
        initInfo()
        buttonClick()
        initCheckbox()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initPatchCare() = with(binding) {
        homeViewModel.patchPetCareResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    Toast.makeText(requireContext(), "케어가 수정되었습니다", Toast.LENGTH_SHORT).show()
                    root.findNavController().popBackStack()
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    Toast.makeText(requireContext(), "케어 수정에 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initInfo() = with(binding) {
        val args: ModifyCareFragmentArgs by navArgs()
        petId = args.petId
        careId = args.careId
        careName = args.careName
        val title = addCareTitleText.text
        addCareTitleText.text = "$careName $title"
        careCountPicker.minValue = 1
        careCountPicker.maxValue = 10
        careCountPicker.value = 5
    }

    private fun buttonClick() = with(binding) {
        addCareSubmitButton.setOnClickListener {
            if (SUBMIT) {
                patchCare()
            }
        }
        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }
    }

    private fun patchCare() {
        val list: MutableList<String> = mutableListOf()
        for (i in 0 until 7) {
            if (dayList[i]) {
                list.add(dayName[i])
            }
        }
        val careModifyInfo = CareModifyInfo(
            dayOfWeeks = list,
            totalCountPerDay = binding.careCountPicker.value
        )
        homeViewModel.patchPetCare(petId, careId, jwt, careModifyInfo)
    }


    private fun initCheckbox() = with(binding) {
        val listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                when (buttonView.id) {
                    R.id.check_everyday -> allCheck(true)
                    R.id.check_sun -> dayList[0] = true
                    R.id.check_mon -> dayList[1] = true
                    R.id.check_tue -> dayList[2] = true
                    R.id.check_wed -> dayList[3] = true
                    R.id.check_thu -> dayList[4] = true
                    R.id.check_fri -> dayList[5] = true
                    R.id.check_sat -> dayList[6] = true
                }
            } else {
                when (buttonView.id) {
                    R.id.check_everyday -> allCheck(false)
                    R.id.check_sun -> dayList[0] = false
                    R.id.check_mon -> dayList[1] = false
                    R.id.check_tue -> dayList[2] = false
                    R.id.check_wed -> dayList[3] = false
                    R.id.check_thu -> dayList[4] = false
                    R.id.check_fri -> dayList[5] = false
                    R.id.check_sat -> dayList[6] = false
                }
            }
            submitCheck()
        }
        checkEveryday.setOnCheckedChangeListener(listener)
        checkMon.setOnCheckedChangeListener(listener)
        checkTue.setOnCheckedChangeListener(listener)
        checkWed.setOnCheckedChangeListener(listener)
        checkThu.setOnCheckedChangeListener(listener)
        checkFri.setOnCheckedChangeListener(listener)
        checkSat.setOnCheckedChangeListener(listener)
        checkSun.setOnCheckedChangeListener(listener)
    }

    private fun allCheck(check: Boolean) = with(binding) {
        for (i in 0 until dayList.size) {
            dayList[i] = false
        }
        checkMon.isChecked = check
        checkTue.isChecked = check
        checkWed.isChecked = check
        checkThu.isChecked = check
        checkFri.isChecked = check
        checkSat.isChecked = check
        checkSun.isChecked = check
    }

    private fun submitCheck() = with(binding) {
        val day = dayList.contains(true)

        SUBMIT = day
        if (SUBMIT) {
            addCareSubmitButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            addCareSubmitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            addCareSubmitButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
            addCareSubmitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        }
    }
}