package org.retriever.dailypet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.retriever.dailypet.LoginActivity
import org.retriever.dailypet.R
import org.retriever.dailypet.RegisterCareActivity
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.databinding.FragmentCareBinding
import org.retriever.dailypet.databinding.FragmentHomeBinding
import org.retriever.dailypet.interfaces.CareAdapter

class CareFragment : Fragment(), View.OnClickListener{
    private val TAG = "CARE_FRAGMENT"
    private lateinit var binding: FragmentCareBinding
    var name = ""

    fun newInstance(str : String) : CareFragment{
        val fragment = CareFragment()
        fragment.name = str
        return fragment
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()

//        val pagerAdapter = CareAdapter()
//        // 3개의 Fragment Add
//        pagerAdapter.addFragment(FirstFragment())
//        pagerAdapter.addFragment(SecondFragment())
//        pagerAdapter.addFragment(ThirdFragment())
//        // Adapter
//        binding.viewpagerMain.adapter = pagerAdapter
//
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                Log.e("ViewPagerFragment", "Page ${position+1}")
//            }
//        })
//
//        // TabLayout attach
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = "Tab ${position+1}"
//        }.attach()
    }

    private fun setOnClickListener() {
        binding.button.setOnClickListener(this)
        binding.button.text = name
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button -> {
                activity?.let{
                    Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}