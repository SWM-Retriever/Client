package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.retriever.dailypet.databinding.ActivityRegisterCareBinding

class RegisterCareActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterCareBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterCareBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() = with(binding){
        btnCareRegisterSubmit.setOnClickListener{
            Toast.makeText(applicationContext, "챙겨주기 항목이 등록되었습니다", Toast.LENGTH_SHORT).show()
            val nextIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(nextIntent)
        }

        btnX.setOnClickListener{
            onBackPressed()
        }
    }
}