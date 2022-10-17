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
import org.retriever.dailypet.interfaces.CareAdapter
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.Care
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.main.viewmodel.HomeViewModel
import org.retriever.dailypet.util.ArrayListAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val groupType = GlobalApplication.prefs.groupType ?: ""
    private var curPetId = 0
    private var curPetName = ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        //initPetList()
        initGroupType()
        getDays()
        getCareList()
        initDaysView()
        initCareList()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    /*private fun initPetList() {
        petList.clear()
        val petNum = petIdList.size
        for (i in 0 until petNum) {
            petList.add(Pet(petIdList[i], petNameList[i]))
        }
    }*/

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

    private fun getDays() {
        homeViewModel.getDays(curPetId, jwt)
    }

    private fun getCareList() {
        homeViewModel.getCareList(curPetId, jwt)
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

                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun initCareList() = with(binding){
        homeViewModel.getCareListResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading ->{
                    showProgressCircular(progressCircular)
                }
                is Resource.Success ->{
                    hideProgressCircular(progressCircular)
                    val arrayListAdapter = ArrayListAdapter()
                    val careList = response.data?.caresInfoList ?: ArrayList()
                    initCareTabView(arrayListAdapter.careListFromJson(careList))
                }
                is Resource.Error ->{
                    hideProgressCircular(progressCircular)
                }
            }

        }
    }

    private fun initCareTabView(careList: ArrayList<Care>) = with(binding) {
        viewPager = binding.viewpagerMain
        tabLayout = binding.careListTab

        if (careList.isEmpty()) {
            binding.emptyAddCareButton.visibility = View.VISIBLE
            binding.emptyCommentText.visibility = View.VISIBLE
            binding.careListTab.visibility = View.GONE
            binding.viewpagerMain.visibility = View.GONE
            binding.addCareButton.visibility = View.GONE
        } else {
            binding.emptyAddCareButton.visibility = View.GONE
            binding.emptyCommentText.visibility = View.GONE
            binding.careListTab.visibility = View.VISIBLE
            binding.viewpagerMain.visibility = View.VISIBLE
            binding.addCareButton.visibility = View.VISIBLE
        }

        val pagerAdapter = CareAdapter(requireActivity())
        for (care in careList) {
            pagerAdapter.addFragment(CareFragment().newInstance(care))
        }

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

    private fun buttonClick() = with(binding) {

        changePetButton.setOnClickListener {
            //showPetList()
        }

        emptyAddCareButton.setOnClickListener {
            addCare()
        }
        addCareButton.setOnClickListener {
            addCare()
        }

    }

   /* private fun showPetList() {
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
    }*/

    private fun addCare() {
        val action = HomeFragmentDirections.actionHomeFragmentToAddCareFragment(curPetId, curPetName)
        binding.root.findNavController().navigate(action)
    }

    /*private fun changePet(petName: String) {
        val idx = petNameList.indexOf(petName)
        curPetId = petIdList[idx]
        getDays()
        initDaysView()
        getCareList()
    }*/

}