package org.retriever.dailypet.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.ui.base.BaseActivity

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>({ActivityMainBinding.inflate(it)}) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalApplication.prefs.apply {
            Log.d("ABC", this.nickname.toString())
            Log.d("ABC", this.jwt.toString())
            Log.d("ABC", this.familyId.toString())
            Log.d("ABC", this.groupName.toString())
            Log.d("ABC", this.invitationCode.toString())
            Log.d("ABC", this.groupType.toString())
            Log.d("ABC", this.profileImageUrl.toString())
            Log.d("ABC", this.petIdList.toString())
        }
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        NavigationUI.setupWithNavController(binding.bottomNavigation, findNavController(R.id.nav_host))
    }

}