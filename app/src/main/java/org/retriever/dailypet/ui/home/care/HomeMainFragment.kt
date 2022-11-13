package org.retriever.dailypet.ui.home.care

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentHomeMainBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.Care
import org.retriever.dailypet.model.signup.pet.Pet
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.home.care.adapter.CareAdapter
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.ui.main.MainActivity
import org.retriever.dailypet.util.ArrayListAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular
import java.util.*
import kotlin.system.exitProcess

class HomeMainFragment : BaseFragment<FragmentHomeMainBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var pagerAdapter: CareAdapter
    private var petList: MutableList<Pet> = mutableListOf()
    private var petIdList: MutableList<Int> = mutableListOf()
    private var petNameList: MutableList<String> = mutableListOf()
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId
    private val groupType = GlobalApplication.prefs.groupType ?: ""
    private var curPetId = 0
    private var curPetName = ""
    private var curIdx = 0
    private lateinit var onBackCallBack: OnBackPressedCallback
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0
    private var isEmptyCare = true


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeMainBinding {
        return FragmentHomeMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCallBack()
        initProgressCircular()
        initContribution()
        initPetList()
        getPetList()
        initDeleteCare()
        initDaysView()
        initCareList()
        getContribution()
        buttonClick()
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initCallBack() {
        onBackCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val tempTime = System.currentTimeMillis();
                val intervalTime = tempTime - backPressedTime;
                if (intervalTime in 0..FINISH_INTERVAL_TIME) {
                    exitProcess(0)
                } else {
                    backPressedTime = tempTime;
                    Toast.makeText(requireContext(), "뒤로가기 버튼을 한번 더 누르면\n앱이 종료됩니다", Toast.LENGTH_SHORT).show();
                    return
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackCallBack)
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initPetList() = with(binding) {
        homeViewModel.getPetListResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        val petResponse = response.data?.petInfoDetailList
                        petList.clear()

                        petResponse?.forEach { pet ->
                            petList.add(Pet(pet.petId, pet.petName))
                            petIdList.add(pet.petId)
                            petNameList.add(pet.petName)
                        }
                        curPetId = petIdList[0]
                        getDays()
                        getCareList()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }

        }
    }

    private fun getPetList() {
        homeViewModel.getPetList(familyId, jwt)
    }

    private fun getDays() {
        homeViewModel.getDays(curPetId, jwt)
    }

    private fun getCareList() {
        Log.e("ABC", "Get CareList")
        homeViewModel.getCareList(curPetId, jwt)
    }

    private fun getContribution() {
        homeViewModel.getMyContribution(jwt)
    }

    private fun initDeleteCare() = with(binding) {
        homeViewModel.deletePetCareResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        getCareList()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
            }

    }

    private fun initContribution() = with(binding) {
        homeViewModel.getMyContributionResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        val contribution : Int = response.data?.contributionPercent?.toInt() ?: 0
                        contributionText.text = getString(R.string.home_contribution_text, contribution)

                        val content = contributionText.text.toString()
                        val spannableString = SpannableString(content)
                        val start = content.indexOf(contribution.toString())
                        val end = start + contribution.toString().length + 1
                        spannableString.setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_blue)),
                            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        contributionText.text = spannableString
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }

        }
    }

    private fun initDaysView() = with(binding) {
        homeViewModel.getDaysResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        val nickname = response.data?.userName ?: ""
                        curPetName = response.data?.petName ?: ""
                        val dDay = response.data?.calculatedDay ?: 0
                        petNameText.text = getString(R.string.home_pet_name_text, curPetName)
                        dDayText.text = getString(R.string.home_pet_day_text, nickname, curPetName, dDay)

                        val content = dDayText.text.toString()
                        val spannableString = SpannableString(content)
                        val start = content.indexOf(dDay.toString())
                        val end = start + dDay.toString().length + 1
                        spannableString.setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_pink)),
                            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        dDayText.text = spannableString
                        initView()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
            }

    }

    private fun initCareList() = with(binding) {
        homeViewModel.getCareListResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    val arrayListAdapter = ArrayListAdapter()
                    val careList = response.data?.caresInfoList ?: ArrayList()
                    setCareTabView(arrayListAdapter.careListFromJson(careList))
                    initView()
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun setCareTabView(careList: ArrayList<Care>) = with(binding) {
        if (careList.isEmpty()) {
            isEmptyCare = true
            emptyAddCareButton.visibility = View.VISIBLE
            emptyCommentText.visibility = View.VISIBLE
            careListTab.visibility = View.GONE
            viewpagerMain.visibility = View.GONE
            addCareButton.visibility = View.GONE
        } else {
            isEmptyCare = false
            emptyAddCareButton.visibility = View.GONE
            emptyCommentText.visibility = View.GONE
            careListTab.visibility = View.VISIBLE
            viewpagerMain.visibility = View.VISIBLE
            addCareButton.visibility = View.VISIBLE
        }
        viewPager = viewpagerMain
        tabLayout = careListTab
        pagerAdapter = CareAdapter(requireActivity())
        for (care in careList) {
            if (getWeek() in care.dayOfWeeks) {
                pagerAdapter.addFragment(CareFragment().newInstance(curPetId, care))
            }
        }

        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                curIdx = position
            }
        })
        if(curIdx >= careList.size){
            curIdx = 0
        }
        viewPager.setCurrentItem(curIdx, false)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = careList[position].careName
        }.attach()
    }

    private fun getWeek(): String? {
        val cal: Calendar = Calendar.getInstance()
        var strWeek: String? = null
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            1 -> strWeek = "SUN"
            2 -> strWeek = "MON"
            3 -> strWeek = "TUE"
            4 -> strWeek = "WED"
            5 -> strWeek = "THU"
            6 -> strWeek = "FRI"
            7 -> strWeek = "SAT"
        }
        return strWeek
    }

    private fun initView() = with(binding){
        alarmButton.visibility = View.INVISIBLE
        homeProfileImage.visibility = View.VISIBLE
        petNameText.visibility = View.VISIBLE
        dDayText.visibility = View.VISIBLE
        careTitleText.visibility = View.VISIBLE
        if(groupType == "FAMILY"){
            if(isEmptyCare){
                statisticsText.visibility = View.INVISIBLE
                contributionText.visibility = View.INVISIBLE
            }
            else{
                statisticsText.visibility = View.VISIBLE
                contributionText.visibility = View.VISIBLE
            }
        }
        else{
            contributionText.visibility = View.INVISIBLE
            statisticsText.visibility = View.INVISIBLE
        }
    }

    private fun buttonClick() = with(binding) {
        homeProfileImage.setOnClickListener {
            showPetList()
        }
        petNameText.setOnClickListener {
            showPetList()
        }
        emptyAddCareButton.setOnClickListener {
            addCare()
        }
        addCareButton.setOnClickListener {
            addCare()
        }
        careTitleText.setOnClickListener {
            getContribution()
            getCareList()
        }
        statisticsText.setOnClickListener {
            val action = HomeMainFragmentDirections.actionHomeMainFragmentToStatisticsFragment(curPetId, curPetName)
            root.findNavController().navigate(action)
        }
    }

    private fun showPetList() {
        val popup = PopupMenu(requireContext(), binding.petNameText)
        val menu = popup.menu
        petList.forEach { pet ->
            menu.add(pet.petName)
        }
        popup.menuInflater.inflate(R.menu.pet_list_menu, menu)
        popup.setOnMenuItemClickListener { item ->
            changePet(item.title as String)
            false
        }
        popup.show()
    }

    private fun addCare() {
        val action = HomeMainFragmentDirections.actionHomeMainFragmentToAddCareFragment(curPetId, curPetName)
        binding.root.findNavController().navigate(action)
    }

    private fun changePet(petName: String) {
        val idx = petNameList.indexOf(petName)
        curPetId = petIdList[idx]
        getDays()
        getCareList()
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackCallBack.remove()
    }

}