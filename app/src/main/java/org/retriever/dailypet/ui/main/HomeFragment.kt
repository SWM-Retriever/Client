package org.retriever.dailypet.ui.main

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.*
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentHomeBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.Care
import org.retriever.dailypet.model.signup.pet.Pet
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.ArrayListAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular
import java.util.*

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

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

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressCircular()
        initPetList()
        initGroupType()
        getPetList()
        initDeleteCare()
        initDaysView()
        initCareList()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initPetList() = with(binding) {
        homeViewModel.getPetListResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
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
    }

    private fun initGroupType() = with(binding) {
        // TODO 1인가구 뷰 변경 로직 추가
        if (groupType == "FAMILY") {
            statisticsButton.visibility = View.VISIBLE
            contributionText.visibility = View.VISIBLE
        } else {
            statisticsButton.visibility = View.INVISIBLE
            contributionText.visibility = View.INVISIBLE
        }
    }

    private fun getPetList() {
        homeViewModel.getPetList(familyId, jwt)
    }

    private fun getDays() {
        homeViewModel.getDays(curPetId, jwt)
    }

    private fun getCareList() {
        homeViewModel.getCareList(curPetId, jwt)
    }

    private fun initDeleteCare() = with(binding) {
        homeViewModel.deletePetCareResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
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
    }

    private fun initDaysView() = with(binding) {
        homeViewModel.getDaysResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
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

                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
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
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun setCareTabView(careList: ArrayList<Care>) = with(binding) {
        if (careList.isEmpty()) {
            emptyAddCareButton.visibility = View.VISIBLE
            emptyCommentText.visibility = View.VISIBLE
            careListTab.visibility = View.GONE
            viewpagerMain.visibility = View.GONE
            addCareButton.visibility = View.GONE
        } else {
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
            }
        })
        // TODO 새로 고침 오류
        //pagerAdapter.notifyDataSetChanged()

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
        refreshButton.setOnClickListener {
            getCareList()
        }
        statisticsText.setOnClickListener {
            root.findNavController().navigate(R.id.action_homeFragment_to_statisticsFragment)
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
        val action = HomeFragmentDirections.actionHomeFragmentToAddCareFragment(curPetId, curPetName)
        binding.root.findNavController().navigate(action)
    }

    private fun changePet(petName: String) {
        val idx = petNameList.indexOf(petName)
        curPetId = petIdList[idx]
        getDays()
        initDaysView()
        getCareList()
    }

}