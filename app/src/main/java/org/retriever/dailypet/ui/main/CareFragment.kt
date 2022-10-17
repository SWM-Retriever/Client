package org.retriever.dailypet.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCareBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.Care
import org.retriever.dailypet.model.main.CheckList
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.main.viewmodel.HomeViewModel
import org.retriever.dailypet.util.ArrayListAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class CareFragment : BaseFragment<FragmentCareBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private var TOTAL = 1
    private var CUR = 0
    private val eDay: List<String> = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    private val kDay: List<String> = listOf("일", "월", "화", "수", "목", "금", "토")
    private var name = ""
    private var period : ArrayList<String> = arrayListOf()
    private var log : ArrayList<CheckList> = arrayListOf()
    private var totalCnt = 0
    private var curCnt = 0
    private var weekdays = ""
    private var jwt = ""
    private var petId = -1
    private var careId = -1

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCareBinding {
        return FragmentCareBinding.inflate(inflater, container, false)
    }

    fun newInstance(jwt: String, petId: Int, newCare: Care): CareFragment {
        val fragment = CareFragment()
        val arrayListAdapter = ArrayListAdapter()
        val args = Bundle()

        args.putString("jwt", jwt)
        args.putInt("petId", petId)
        args.putInt("careId", newCare.careId)
        args.putString("name", newCare.careName)
        args.putInt("totalCnt", newCare.totalCareCount)
        args.putInt("curCnt", newCare.currentCount)
        args.putStringArrayList("period", arrayListAdapter.stringListFromJson(newCare.dayOfWeeks))
        args.putParcelableArrayList("log", arrayListAdapter.checkListFromJson(newCare.checkList))
        fragment.arguments = args
        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initCareInfo()
        initCareCheck()
        initCareCancel()
        initWeekdays()
        initCare()
        buttonClick()
    }

    private fun initCareInfo(){
        jwt = arguments?.getString("jwt") ?: ""
        petId = arguments?.getInt("petId") ?: -1
        careId = arguments?.getInt("careId") ?: -1
        name = arguments?.getString("name") ?: ""
        period = ((arguments?.getStringArrayList("period") ?: "") as ArrayList<String>)
        log = (arguments?.getStringArrayList("log") ?: "") as ArrayList<CheckList>
        totalCnt = arguments?.getInt("totalCnt") ?: 0
        curCnt = arguments?.getInt("curCnt") ?: 0
        weekdays = ""
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initCareCheck() = with(binding){
        homeViewModel.postCareCheckResponse.observe(viewLifecycleOwner){ event->
            event.getContentIfNotHandled()?.let{ response ->
                when(response){
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        increaseProgress()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
            }
        }
    }

    private fun initCareCancel() = with(binding){
        homeViewModel.postCareCancelResponse.observe(viewLifecycleOwner){ event->
            event.getContentIfNotHandled()?.let{ response ->
                when(response){
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        decreaseProgress()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
            }
        }
    }

    private fun initWeekdays() {
        for (i in 0 until 7) {
            val day = eDay[i]
            for (j in 0 until period.size) {
                if (day == period[j]) {
                    weekdays += "${kDay[i]} "
                }
            }
        }
    }

    private fun initCare() = with(binding) {
        careTitleText.text = name
        careCountText.text = getString(R.string.care_count, curCnt, totalCnt)

        logText.text = "" // TODO 로그뷰 수정
        periodTitleText.text = weekdays
        val percent = curCnt.toDouble() / totalCnt.toDouble()
        progressbar.progress = (percent * 100).toInt()

        TOTAL = totalCnt
        CUR = curCnt
    }

    private fun buttonClick() = with(binding) {

        checkButton.setOnClickListener {
            if (CUR != TOTAL){
                postCareCheck()
            }
            else{
                Toast.makeText(requireContext(),"케어 최대 횟수입니다",Toast.LENGTH_SHORT).show()
            }

        }
        cancelText.setOnClickListener {
            if(CUR != 0){
                postCareCancel()
            }
            else{
                Toast.makeText(requireContext(),"케어 최소 횟수입니다",Toast.LENGTH_SHORT).show()
            }
        }
        careMoreButton.setOnClickListener {
            showPopup()
        }
    }

    private fun postCareCheck(){
        homeViewModel.postCareCheck(petId, careId, jwt)
    }

    private fun postCareCancel(){
        homeViewModel.postCareCancel(petId, careId, jwt)
    }

    private fun increaseProgress() = with(binding) {
        CUR++
        if (CUR > TOTAL) CUR = TOTAL
        val percent = CUR.toDouble() / TOTAL.toDouble()
        careCountText.text = getString(R.string.care_count, curCnt, totalCnt)
        progressbar.progress = (percent * 100).toInt()
    }

    private fun decreaseProgress() = with(binding) {
        CUR--
        if (CUR < 0) CUR = 0
        val percent = CUR.toDouble() / TOTAL.toDouble()
        careCountText.text = getString(R.string.care_count, curCnt, totalCnt)
        progressbar.progress = (percent * 100).toInt()
    }

    private fun showPopup() {
        val popup = PopupMenu(requireContext(), binding.careMoreButton)
        val menu = popup.menu

        menu.add("삭제하기")
        menu.add("수정하기")

        popup.menuInflater.inflate(R.menu.pet_list_menu, menu)
        popup.setOnMenuItemClickListener { item ->

            false
        }
        popup.show()
    }


}