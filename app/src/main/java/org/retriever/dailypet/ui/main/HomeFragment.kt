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
import org.retriever.dailypet.ui.main.viewmodel.HomeViewModel
import org.retriever.dailypet.util.ArrayListAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

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
    private var redraw = true

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        redraw = true
        initProgressCircular()
        initPetList()
        initGroupType()
        getPetList()
        initCareCheck()
        initCareCancel()
        initDaysView()
        initCareList()
        buttonClick()
    }

    private fun initCareCheck() = with(binding) {
        homeViewModel.postCareCheckResponse.observe(viewLifecycleOwner) { event ->
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

    private fun initCareCancel() = with(binding) {
        homeViewModel.postCareCancelResponse.observe(viewLifecycleOwner) { event ->
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
        homeViewModel.getCareListResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        val arrayListAdapter = ArrayListAdapter()
                        val careList = response.data?.caresInfoList ?: ArrayList()
                        //if(redraw){
                        initCareTabView(arrayListAdapter.careListFromJson(careList))
                        redraw = false
                        //}
                        refreshCareTab(arrayListAdapter.careListFromJson(careList))
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
            }
        }
    }

    private fun initCareTabView(careList: ArrayList<Care>) = with(binding) {
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
            pagerAdapter.addFragment(CareFragment().newInstance(jwt, curPetId, care))
        }

//        tabLayout.setScrollPosition(2, 0f, true)
//        viewPager.currentItem = 2

        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = careList[position].careName
        }.attach()
    }

    private fun refreshCareTab(careList: ArrayList<Care>) = with(binding) {
        for (i in 0 until careList.size) {
            pagerAdapter.refreshFragment(i, CareFragment().newInstance(jwt, curPetId, careList[i]))
        }
        pagerAdapter.notifyDataSetChanged()
    }


    private fun buttonClick() = with(binding) {

        changePetButton.setOnClickListener {
            showPetList()
        }
        emptyAddCareButton.setOnClickListener {
            addCare()
        }
        addCareButton.setOnClickListener {
            addCare()
        }

    }

    private fun showPetList() {
        val popup = PopupMenu(requireContext(), binding.changePetButton)
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
        redraw = true
        getDays()
        initDaysView()
        getCareList()
    }

}