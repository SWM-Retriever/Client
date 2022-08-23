package org.retriever.dailypet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.retriever.dailypet.LoginActivity
import org.retriever.dailypet.R
import org.retriever.dailypet.RegisterCareActivity
import org.retriever.dailypet.databinding.FragmentHomeBinding
import org.retriever.dailypet.interfaces.CareAdapter

class HomeFragment : Fragment(), View.OnClickListener{
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    fun newInstance() : HomeFragment{
        return HomeFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewPager = binding.viewpagerMain
        tabLayout = binding.tabCareList
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()

        val pagerAdapter = CareAdapter(requireActivity())
        // Fragment Add
        pagerAdapter.addFragment(CareFragment().newInstance("1"))
        pagerAdapter.addFragment(CareFragment().newInstance("2"))
        pagerAdapter.addFragment(CareFragment().newInstance("3"))

        // Adapter 연결
        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(TAG, "Page ${position+1}")
            }
        })

        // TabLayout attach
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "Tab ${position+1}"
        }.attach()
    }

    private fun setOnClickListener() {
        binding.btnRegisterCare.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_register_care -> {
                activity?.let{
                    val intent = Intent(context, RegisterCareActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}