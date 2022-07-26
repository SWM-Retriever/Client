package org.retriever.dailypet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = Firebase.analytics

        val crashButton = findViewById<Button>(R.id.btn_crash)
        val log1Button = findViewById<Button>(R.id.btn_1)
        val log2Button = findViewById<Button>(R.id.btn_2)

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


//        addContentView(crashButton, ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT))
    }



}