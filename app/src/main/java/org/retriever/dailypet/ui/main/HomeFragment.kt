package org.retriever.dailypet.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.RegisterCareActivity
import org.retriever.dailypet.databinding.FragmentHomeBinding
import org.retriever.dailypet.interfaces.CareAdapter
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.models.Care
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.main.viewmodel.HomeViewModel
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val petIdList = GlobalApplication.prefs.getPetIdList()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        getDays()
        initDaysView()
        initCareTabView()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun getDays(){
        homeViewModel.getDays(petIdList[0], jwt)
    }

    private fun initDaysView() = with(binding) {
        homeViewModel.getDaysResponse.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    val nickname = response.data?.userName ?: ""
                    val petName = response.data?.petName ?: ""
                    val dDay = response.data?.calculatedDay ?: 0
                    dDayText.text = getString(R.string.home_pet_day_text, nickname, petName, dDay)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }

    }

    private fun initCareTabView() = with(binding) {
        viewPager = binding.viewpagerMain
        tabLayout = binding.tabCareList

        // Load Data
        val careList = ArrayList<Care>()
        careList.add(Care("식사", "월 수 금", 3, 1, "나"))
        careList.add(Care("산책", "월 화 수 목 금", 2, 0, ""))
        careList.add(Care("간식", "월 화 수 목 금 토 일", 5, 4, "나 아빠 엄마 동생"))
        careList.add(Care("놀이", "화 목 토", 10, 6, "나 아빠 나 아빠 나 아빠"))
        careList.add(Care("목욕", "일", 1, 1, "엄마"))
        careList.add(Care("병원", "토", 1, 0, "나 아빠 엄마 동생"))
        careList.add(Care("양치", "월 화 수 목 금 토 일", 2, 1, "엄마 동생"))

        if (careList.isEmpty()) {
            binding.btnRegisterCare.visibility = View.VISIBLE
            binding.textEmptyComment.visibility = View.VISIBLE
            binding.tabCareList.visibility = View.GONE
            binding.viewpagerMain.visibility = View.GONE
            binding.btnAddCare.visibility = View.GONE
        } else {
            binding.btnRegisterCare.visibility = View.GONE
            binding.textEmptyComment.visibility = View.GONE
            binding.tabCareList.visibility = View.VISIBLE
            binding.viewpagerMain.visibility = View.VISIBLE
            binding.btnAddCare.visibility = View.VISIBLE
        }

        val pagerAdapter = CareAdapter(requireActivity())
        // Fragment Add
        for (care in careList) {
            pagerAdapter.addFragment(CareFragment().newInstance(care))
        }

        // Adapter 연결
        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        // TabLayout attach
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = careList[position].name
        }.attach()
    }

    private fun buttonClick() = with(binding) {
        btnRegisterCare.setOnClickListener {
            val intent = Intent(requireContext(), RegisterCareActivity::class.java)
            startActivity(intent)
        }
        btnAddCare.setOnClickListener {
            val intent = Intent(requireContext(), RegisterCareActivity::class.java)
            startActivity(intent)
        }
    }

}