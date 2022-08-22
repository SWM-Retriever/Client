package org.retriever.dailypet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.fragments.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    var fragment : CareFragment = CareFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tabItemSelected()
        navigationItemSelect()
    }

    private fun tabItemSelected() = with(binding){
        val tabLayout = tabCareList
        val tab: TabLayout.Tab = tabLayout.newTab()
        tab.text = "newTab"
        tabLayout.addTab(tab)

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            // 탭 버튼을 선택할 때 이벤트
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                when(val name = tab?.text) {
                    "식사" -> transaction.replace(R.id.tab_careList, fragment.newInstance(name as String))
                    "놀이" -> transaction.replace(R.id.tab_careList, fragment.newInstance(name as String))
                    "간식" -> transaction.replace(R.id.tab_careList, fragment.newInstance(name as String))
                    "병원" -> transaction.replace(R.id.tab_careList, fragment.newInstance(name as String))
                }
                transaction.commit()
            }

            // 다른 탭 버튼을 눌러 선택된 탭 버튼이 해제될 때 이벤트
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            // 선택된 탭 버튼을 다시 선택할 때 이벤
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_main, fragment)
        fragmentTransaction.commit()
    }

    private fun navigationItemSelect() {
        binding.bottomBar.run {
            setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.action_home -> replaceFragment(HomeFragment())
                    R.id.action_diary -> replaceFragment(DiaryFragment())
                    R.id.action_calendar -> replaceFragment(CalendarFragment())
                    R.id.action_myPage -> replaceFragment(MyPageFragment())
                }
                true
            }
            selectedItemId = R.id.action_home
        }
    }
}