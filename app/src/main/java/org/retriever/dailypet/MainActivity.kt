package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.btn_login)
        val testButton = findViewById<Button>(R.id.btn_test)

        loginButton.setOnClickListener {
            val nextIntent = Intent(this, LoginActivity::class.java)
            startActivity(nextIntent)
        }
        testButton.setOnClickListener {
            val nextIntent = Intent(this, TestActivity::class.java)
            startActivity(nextIntent)
        }
    }
}