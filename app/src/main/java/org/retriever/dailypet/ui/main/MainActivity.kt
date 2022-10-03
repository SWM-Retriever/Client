package org.retriever.dailypet.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.fragments.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val TAG = "MAIN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        navigationItemSelect()
    }

    private fun replaceFragment(id : Int, fragment: Fragment) {
        val bundle = Bundle()
        val flag = intent.getIntExtra("flag", 0)
        bundle.putInt("flag",flag)
        fragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(id, fragment)
        fragmentTransaction.commit()
    }

    private fun navigationItemSelect() {
        binding.bottomBar.run {
            setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.action_home -> replaceFragment(R.id.frame_main, HomeFragment())
                    R.id.action_diary -> replaceFragment(R.id.frame_main, DiaryFragment())
                    R.id.action_calendar -> replaceFragment(R.id.frame_main, CalendarFragment())
                    R.id.action_myPage -> replaceFragment(R.id.frame_main, MyPageFragment())
                }
                true
            }
            selectedItemId = R.id.action_home
        }
    }
}