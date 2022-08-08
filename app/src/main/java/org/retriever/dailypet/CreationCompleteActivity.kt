package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.retriever.dailypet.databinding.ActivityCreationCompleteBinding

class CreationCompleteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreationCompleteBinding
    private val TAG = "CREATION COMPLETE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreationCompleteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()

    }


    private fun init(){
        /* 반려동물 추가 등록 버튼 */
        binding.btnAdd.setOnClickListener{
            val nextIntent = Intent(this, CreatePetActivity::class.java)
            startActivity(nextIntent)
        }
        /* 케어 시작하기 버튼 */
        binding.btnStart.setOnClickListener{
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
    }
}