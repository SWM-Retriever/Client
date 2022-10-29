package org.retriever.dailypet.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentAddCareBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.CareInfo
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular

class AddCareFragment : BaseFragment<FragmentAddCareBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private var petId = -1
    private var petName = ""
    private var dayList: MutableList<Boolean> = mutableListOf(false, false, false, false, false, false, false)
    private var careList: MutableList<Boolean> = mutableListOf(false, false, false, false, false)
    private val dayName: List<String> = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    private var careName = ""
    private var SUBMIT = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddCareBinding {
        return FragmentAddCareBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressCircular()
        initPostCare()
        initInfo()
        buttonClick()
        initCheckbox()
        initEditText()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initPostCare() = with(binding) {
        homeViewModel.postCareResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        Toast.makeText(requireContext(), "케어가 등록되었습니다", Toast.LENGTH_SHORT).show()
                        root.findNavController().navigate(AddCareFragmentDirections.actionAddCareFragmentToHomeFragment())
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        Toast.makeText(requireContext(), "케어 등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initInfo() {
        val args: AddCareFragmentArgs by navArgs()
        petId = args.petId
        petName = args.petName
        val title = binding.addCareTitleText.text
        binding.addCareTitleText.text = "$petName $title"
    }

    private fun buttonClick() = with(binding) {
        btnFood.setOnClickListener {
            selectCare(btnFood, 1)
            submitCheck()
        }
        btnWalk.setOnClickListener {
            selectCare(btnWalk, 2)
            submitCheck()
        }
        btnPlay.setOnClickListener {
            selectCare(btnPlay, 3)
            submitCheck()
        }
        btnWash.setOnClickListener {
            selectCare(btnWash, 4)
            submitCheck()
        }
        btnInputCare.setOnClickListener {
            careInputEdittext.visibility = View.VISIBLE
            selectCare(btnInputCare, 0)

        }
        addCareSubmitButton.setOnClickListener {
            if (SUBMIT) {
                postCare()
            }
        }
        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }
    }

    private fun postCare() {
        val list: MutableList<String> = mutableListOf()
        for (i in 0 until 7) {
            if (dayList[i]) {
                list.add(dayName[i])
            }
        }
        val careInfo = CareInfo(
            careName = careName,
            dayOfWeeks = list,
            totalCountPerDay = binding.careCountEdittext.text.toString().toInt()
        )
        homeViewModel.postCare(petId, jwt, careInfo)
    }

    private fun selectCare(selectedBtn: AppCompatButton, idx : Int) = with(binding) {
        disableButton()
        if(selectedBtn != btnInputCare){
            careInputEdittext.visibility = View.GONE
        }
        careList[idx] = true
        selectedBtn.isSelected = true
        careName = selectedBtn.text.toString()
        submitCheck()
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

    private fun disableButton() = with(binding){
        for(i in 0 until careList.size){
            careList[i] = false
        }
        btnFood.isSelected = false
        btnWalk.isSelected = false
        btnPlay.isSelected = false
        btnWash.isSelected = false
        btnInputCare.isSelected = false
    }

    private fun allCheck(check: Boolean) = with(binding){
        for(i in 0 until dayList.size){
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

    private fun initEditText() = with(binding){
        careCountEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                submitCheck()
                if (careCountEdittext.text.isNotBlank()) {
                    careCountEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.whiteblue_click_button)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        careInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                submitCheck()
                if (careInputEdittext.text.isNotBlank()) {
                    careInputEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.whiteblue_click_button)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun submitCheck() = with(binding){
        val day = dayList.contains(true)
        var care = careList.contains(true)
        val number = careCountEdittext.text.isNotBlank()
        if(careList[0]){
            care = careInputEdittext.text.isNotBlank()
            careName = careInputEdittext.text.toString()
        }

        SUBMIT = day && care && number
        if (SUBMIT) {
            addCareSubmitButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            addCareSubmitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            addCareSubmitButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
            addCareSubmitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        }
    }
}