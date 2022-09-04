package org.retriever.dailypet

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.AuthApiClient
import org.retriever.dailypet.interfaces.MyFirebaseMessagingService
import org.retriever.dailypet.models.App

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        init()
        val loginButton = findViewById<Button>(R.id.btn_login)
        val testButton = findViewById<Button>(R.id.btn_test)
        val jwt = App.prefs.jwt
        loginButton.setOnClickListener {

            // jwt 인증 방식
//            if(jwt != null) {
//                val nextIntent = Intent(this, MainActivity::class.java)
//                startActivity(nextIntent)
//            }
//            else{
//                val nextIntent = Intent(this, LoginActivity::class.java)
//                startActivity(nextIntent)
//            }

            // sns 토큰 방식
            if(AuthApiClient.instance.hasToken()) {


                val nextIntent = Intent(this, MainActivity::class.java)
                startActivity(nextIntent)
            }
            else{
                val nextIntent = Intent(this, LoginActivity::class.java)
                startActivity(nextIntent)
            }
        }
        testButton.setOnClickListener {
            val nextIntent = Intent(this, TestActivity::class.java)
            startActivity(nextIntent)
        }
    }

    private fun init(){
        setFCMToken()
    }

    private fun setFCMToken(){
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            token = task.result
            Log.d(TAG, "FCM Token is ${token}")
            if(token != null) MyFirebaseMessagingService().onNewToken(token!!)
        })
    }
}