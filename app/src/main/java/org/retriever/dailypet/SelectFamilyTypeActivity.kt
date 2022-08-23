package org.retriever.dailypet

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.retriever.dailypet.databinding.ActivityLoginBinding
import org.retriever.dailypet.databinding.ActivitySelectFamilyTypeBinding

class SelectFamilyTypeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectFamilyTypeBinding
    private var backKeyPressedTime : Long = 0
    private var alone = false
    private var group = false

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectFamilyTypeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /* 1인 가구 */
        binding.btnSelectAlone.setOnClickListener{
            alone = true
            group = false
            binding.btnSelectAlone.isSelected = true
            binding.btnSelectGroup.isSelected = false
            binding.imgAlone.setColorFilter(resources.getColor(R.color.main_blue))
            binding.imgGroup.setColorFilter(resources.getColor(R.color.grey))
            binding.btnSelectType.background = this.getDrawable(R.drawable.blue_button)
            binding.btnSelectType.setTextColor(this.getColor(R.color.white))
        }
        /* 가족 */
        binding.btnSelectGroup.setOnClickListener{
            alone = false
            group = true
            binding.btnSelectAlone.isSelected = false
            binding.btnSelectGroup.isSelected = true
            binding.imgAlone.setColorFilter(resources.getColor(R.color.grey))
            binding.imgGroup.setColorFilter(resources.getColor(R.color.main_blue))
            binding.btnSelectType.background = this.getDrawable(R.drawable.blue_button)
            binding.btnSelectType.setTextColor(this.getColor(R.color.white))
        }
        /* 선택 */
        binding.btnSelectType.setOnClickListener{
            if(alone){
                // 가족 생성 처리

                val nextIntent = Intent(this, CreatePetActivity::class.java)
                startActivity(nextIntent)
            }
            else if(group){
                val nextIntent = Intent(this, FamilyEntranceActivity::class.java)
                startActivity(nextIntent)
            }
            else{
                Toast.makeText(this, "양육 유형을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
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