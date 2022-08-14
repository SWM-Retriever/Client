package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.retriever.dailypet.databinding.ActivityFamilyEntranceBinding
import org.retriever.dailypet.databinding.ActivitySelectFamilyTypeBinding

class FamilyEntranceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFamilyEntranceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyEntranceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() = with(binding){
        /* 가족 생성 버튼 */
        btnCreate.setOnClickListener{
            val nextIntent = Intent(applicationContext, CreateFamilyActivity::class.java)
            startActivity(nextIntent)
        }
        /* 초대코드 페이지 버튼 */
        textInviteCode.setOnClickListener{
            val nextIntent = Intent(applicationContext, FindFamilyActivity::class.java)
            startActivity(nextIntent)
        }
        /* 이전버튼 */
        imgbtnBack.setOnClickListener{
            onBackPressed()
        }
    }
}