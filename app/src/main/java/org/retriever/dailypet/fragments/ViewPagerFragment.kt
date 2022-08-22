package org.retriever.dailypet.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.interfaces.CareAdapter

class ViewPagerFragment : Fragment(){
    private val TAG = "VIEW_PAGER_FRAGMENT"
    private lateinit var binding: ActivityMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = CareAdapter(requireParentFragment())
        // 3개의 Fragment Add
        pagerAdapter.addFragment(CareFragment().newInstance("1"))
        pagerAdapter.addFragment(CareFragment().newInstance("2"))
        pagerAdapter.addFragment(CareFragment().newInstance("3"))
        // Adapter
        binding.viewpagerMain.adapter = pagerAdapter

        binding.viewpagerMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position+1}")
            }
        })

        // TabLayout attach
        TabLayoutMediator(binding.tabCareList, binding.viewpagerMain) { tab, position ->
            tab.text = "Tab ${position+1}"
        }.attach()
    }
}