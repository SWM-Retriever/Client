package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.kakao.sdk.auth.AuthApiClient

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val loginButton = findViewById<Button>(R.id.btn_login)
        val testButton = findViewById<Button>(R.id.btn_test)

        loginButton.setOnClickListener {
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
}