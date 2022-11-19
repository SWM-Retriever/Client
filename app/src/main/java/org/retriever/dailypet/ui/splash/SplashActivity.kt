package org.retriever.dailypet.ui.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLoginState
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.databinding.ActivitySplashBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseActivity
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.ui.login.LoginViewModel
import org.retriever.dailypet.ui.main.MainActivity
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>({ ActivitySplashBinding.inflate(it) }) {
    private val loginViewModel by viewModels<LoginViewModel>()
    private val REQUEST_CODE_UPDATE = 1
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private lateinit var appUpdateManager : AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageLogo = findViewById<ImageView>(R.id.img_logo)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fadein)
        imageLogo.startAnimation(fadeIn)

        initProgress()
        checkUpdate()
        startApp()
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    requestUpdate(appUpdateInfo)
                }
            }
    }

    private fun checkUpdate(){
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.let {
            it.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    requestUpdate(appUpdateInfo)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != Activity.RESULT_OK) {
                checkUpdate()
            }
            else if (requestCode == Activity.RESULT_OK){
                val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo){
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            IMMEDIATE,
            this,
            REQUEST_CODE_UPDATE
        )
    }

    private fun initProgress() = with(binding) {
        val loginIntent = Intent(applicationContext, LoginActivity::class.java)
        val mainIntent = Intent(applicationContext, MainActivity::class.java)

        loginViewModel.progressStatusResponse.observe(this@SplashActivity) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        when (response.data?.status) {
                            "PROFILE" -> startActivity(loginIntent)
                            "GROUP" -> startActivity(loginIntent)
                            else -> startActivity(mainIntent)
                        }
                    }
                    is Resource.Error -> {
                        GlobalApplication.prefs.apply {
                            jwt = this.jwt ?: ""
                            this.initNickname()
                            this.initJwt()
                            this.initFamilyId()
                            this.initGroupName()
                            this.initInvitationCode()
                            this.initGroupType()
                            this.initProfileImageUrl()
                        }
                        startActivity(loginIntent)
                    }
                }
            }
        }
    }

    private fun startApp(){
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        if (error is KakaoSdkError && error.isInvalidTokenError()) {
                            startActivity(intent)
                            finish()
                        }
                        else {
                            //기타 에러
                        }
                    }
                    else {
                        loginViewModel.getProgressStatus(jwt)
                        finish()
                    }
                }
            }
            else if (NidOAuthLoginState.OK.equals(NaverIdLoginSDK.getState())){
                loginViewModel.getProgressStatus(jwt)
                finish()
            }
            else {
                startActivity(intent)
                finish()
            }
        }, DURATION)
    }

    companion object {
        private const val DURATION : Long = 1500
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}