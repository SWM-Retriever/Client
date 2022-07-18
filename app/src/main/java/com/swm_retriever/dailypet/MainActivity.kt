package com.swm_retriever.dailypet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val crashButton = findViewById<Button>(R.id.btn_crash)
        crashButton.setOnClickListener{
            throw RuntimeException("Test Crash") // Force a crash
        }

//        addContentView(crashButton, ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT))
    }



}