package org.retriever.dailypet.ui.main

import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.ui.base.BaseActivity

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>({ActivityMainBinding.inflate(it)}) {
    private val TAG = "MAIN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalApplication.prefs.apply {
            Log.d(TAG, this.nickname.toString())
            Log.d(TAG, this.jwt.toString())
            Log.d(TAG, this.familyId.toString())
            Log.d(TAG, this.groupName.toString())
            Log.d(TAG, this.invitationCode.toString())
            Log.d(TAG, this.groupType.toString())
            Log.d(TAG, this.profileImageUrl.toString())
        }
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        NavigationUI.setupWithNavController(binding.bottomNavigation, findNavController(R.id.nav_host))
    }

}