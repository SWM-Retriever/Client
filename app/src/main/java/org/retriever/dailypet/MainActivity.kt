package org.retriever.dailypet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.fragments.HomeFragment
import org.retriever.dailypet.fragments.MyPageFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.holder_fl_main, fragment)
        fragmentTransaction.commit()
    }

    private fun navigationItemSelect() {
        binding.bnMain.run {
            setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.action_home -> replaceFragment(HomeFragment())
                    R.id.action_myPage -> replaceFragment(MyPageFragment())
                }
                true
            }
            selectedItemId = R.id.action_home
        }
    }
}