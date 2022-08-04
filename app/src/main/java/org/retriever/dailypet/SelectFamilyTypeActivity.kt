package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.retriever.dailypet.databinding.ActivityLoginBinding
import org.retriever.dailypet.databinding.ActivitySelectFamilyTypeBinding

class SelectFamilyTypeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectFamilyTypeBinding
    private var backKeyPressedTime : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectFamilyTypeBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        /* 1인 가구 */
        binding.btnSelectAlone.setOnClickListener{
            // 가족 생성 처리
        }
        /* 가족 */
        binding.btnSelectFamily.setOnClickListener{
            val nextIntent = Intent(this, FamilyEntranceActivity::class.java)
            startActivity(nextIntent)
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2500){
            backKeyPressedTime = System.currentTimeMillis()
            return
        } else{
            finishAffinity()
        }
    }
}