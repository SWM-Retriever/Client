package org.retriever.dailypet.ui.main

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCareBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.Care
import org.retriever.dailypet.model.main.CheckList
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.ArrayListAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular
import java.util.*

class CareFragment : BaseFragment<FragmentCareBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private val eDay: List<String> = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    private val kDay: List<String> = listOf("일", "월", "화", "수", "목", "금", "토")
    private var name = ""
    private var period: ArrayList<String> = arrayListOf()
    private var logList: ArrayList<CheckList> = arrayListOf()
    private var logNameList: ArrayList<String> = arrayListOf()
    private var memberNameList: ArrayList<String> = arrayListOf()
    private var colorList: ArrayList<Int> = arrayListOf()
    private var totalCnt = 0
    private var curCnt = 0
    private var weekdays = ""
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId
    private var petId = -1
    private var careId = -1

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCareBinding {
        return FragmentCareBinding.inflate(inflater, container, false)
    }

    fun newInstance(petId: Int, newCare: Care): CareFragment {
        val fragment = CareFragment()
        val arrayListAdapter = ArrayListAdapter()
        val args = Bundle()

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
        initGroupInfo()
        getGroupInfo()
        initCareInfo()
        initCareCheck()
        initCareCancel()
        initView()
        buttonClick()
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun getGroupInfo() {
        homeViewModel.getGroupInfo(familyId, jwt)
    }

    private fun initGroupInfo() = with(binding) {
        homeViewModel.getGroupInfoResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    val memberResponse = response.data?.familyMemberList
                    memberNameList.clear()
                    memberResponse?.forEach { member ->
                        memberNameList.add(member.familyRoleName)
                    }
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun initCareInfo() {
        petId = arguments?.getInt("petId") ?: -1
        careId = arguments?.getInt("careId") ?: -1
        name = arguments?.getString("name") ?: ""
        period = (((arguments?.getStringArrayList("period") ?: "") as ArrayList<String>))
        logList = (arguments?.getStringArrayList("log") ?: "") as ArrayList<CheckList>
        totalCnt = arguments?.getInt("totalCnt") ?: 0
        curCnt = arguments?.getInt("curCnt") ?: 0
        weekdays = ""
        initWeekdays()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initCareCheck() = with(binding) {
        homeViewModel.postCareCheckResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    increaseProgress()
                    //updateCareInfo(response.data)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun initCareCancel() = with(binding) {
        homeViewModel.postCareCancelResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    decreaseProgress()
                    //updateCareInfo(response.data)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun updateCareInfo(care: Care?) {
        name = care?.careName.toString()
        period = care?.dayOfWeeks as ArrayList<String>
        logList = care.checkList as ArrayList<CheckList>
        totalCnt = care.totalCareCount
        curCnt = care.currentCount
        weekdays = ""
        initWeekdays()
        initView()
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

    private fun initView() = with(binding) {
        careTitleText.text = name
        careCountText.text = getString(R.string.care_count, curCnt, totalCnt)
        periodTitleText.text = weekdays
        val content = periodTitleText.text.toString()
        val spannableString = SpannableString(content)
        val curWeek = getCurWeek()
        val start = content.indexOf(curWeek.toString())
        val end = start + curWeek.toString().length + 1
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_pink)),
            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        periodTitleText.text = spannableString

        progressbar.maxStateNumber = totalCnt
        if(curCnt == 0){
            progressbar.setAllStatesCompleted(false)
            progressbar.currentStateNumber = 1
            progressbar.foregroundColor = ContextCompat.getColor(requireContext(), R.color.grey)
        }
        else if(curCnt == totalCnt){
            progressbar.setAllStatesCompleted(true)
            progressbar.foregroundColor = ContextCompat.getColor(requireContext(), R.color.main_pink)
        }
        else{
            progressbar.setAllStatesCompleted(false)
            progressbar.currentStateNumber = curCnt
            progressbar.foregroundColor = ContextCompat.getColor(requireContext(), R.color.main_pink)
        }

        makeLogName()
        progressbar.setStateDescriptionData(logNameList)
    }

    private fun makeLogName(){
        for (log in logList) {
            var name = log.familyRoleName
            if(name.length == 4){
                name = name.substring(0, 2) +"\n"+ name.substring(2, 4)
            }
            logNameList.add(name)
        }
        val num = totalCnt - curCnt
        for(i in 0 until num) {
            logNameList.add("")
        }
    }

    private fun buttonClick() = with(binding) {

        checkButton.setOnClickListener {
            if (curCnt != totalCnt) {
                postCareCheck()
            } else {
                Toast.makeText(requireContext(), "케어 최대 횟수입니다", Toast.LENGTH_SHORT).show()
            }

        }
        cancelText.setOnClickListener {
            if (curCnt != 0) {
                postCareCancel()
            } else {
                Toast.makeText(requireContext(), "케어 최소 횟수입니다", Toast.LENGTH_SHORT).show()
            }
        }
        careMoreButton.setOnClickListener {
            showPopup()
        }
    }

    private fun postCareCheck() {
        homeViewModel.postCareCheck(petId, careId, jwt)
    }

    private fun postCareCancel() {
        homeViewModel.postCareCancel(petId, careId, jwt)
    }

    private fun increaseProgress() = with(binding) {
        curCnt++
        if (curCnt > totalCnt) curCnt = totalCnt
        careCountText.text = getString(R.string.care_count, curCnt, totalCnt)
        progressbar.currentStateNumber = curCnt
    }

    private fun decreaseProgress() = with(binding) {
        curCnt--
        if (curCnt < 0) curCnt = 0
        careCountText.text = getString(R.string.care_count, curCnt, totalCnt)
        progressbar.currentStateNumber = curCnt
    }

    private fun showPopup() {
        val popup = PopupMenu(requireContext(), binding.careMoreButton)
        val menu = popup.menu

        menu.add("삭제하기")
        menu.add("수정하기")

        popup.menuInflater.inflate(R.menu.pet_list_menu, menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "삭제하기" -> deleteCare()
                "수정하기" -> modifyCare()
            }
            false
        }
        popup.show()
    }

    private fun deleteCare() {
        homeViewModel.deletePetCare(careId, careId, jwt)
    }

    private fun modifyCare() {
        val action = HomeFragmentDirections.actionHomeFragmentToModifyCareFragment(petId, name, careId)
        binding.root.findNavController().navigate(action)
    }

    private fun getCurWeek(): String? {
        val cal: Calendar = Calendar.getInstance()
        var strWeek: String? = null
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            1 -> strWeek = "일"
            2 -> strWeek = "월"
            3 -> strWeek = "화"
            4 -> strWeek = "수"
            5 -> strWeek = "목"
            6 -> strWeek = "금"
            7 -> strWeek = "토"
        }
        return strWeek
    }

}