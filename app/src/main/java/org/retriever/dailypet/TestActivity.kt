package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class TestActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        firebaseAnalytics = Firebase.analytics

        val crashButton = findViewById<Button>(R.id.btn_crashTest)
        val log1Button = findViewById<Button>(R.id.btn_logTest1)
        val log2Button = findViewById<Button>(R.id.btn_logTest2)
        val backButton = findViewById<Button>(R.id.btn_backToMain)

        crashButton.setOnClickListener{
            throw RuntimeException("Test Crash") // Force a crash
        }
        log1Button.setOnClickListener {
            var bundle = Bundle().apply {
                this.putString("level1", "Button")
            }
            firebaseAnalytics.logEvent("button1", bundle)
        }
        log2Button.setOnClickListener {
            var bundle = Bundle().apply {
                this.putString("level2", "button2")
            }
            firebaseAnalytics.logEvent("Button2", bundle)
        }
        backButton.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
    }
}