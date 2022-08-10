package org.retriever.dailypet

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.CheckBox
import android.widget.Toast
import org.retriever.dailypet.databinding.ActivityCreationCompleteBinding
import org.retriever.dailypet.databinding.ActivityTermOfServiceBinding

class TermOfService : AppCompatActivity() {
    private lateinit var binding : ActivityTermOfServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermOfServiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()

    }
    private fun init(){
        /* 다음 버튼 */
        binding.btnNext.setOnClickListener{
            if(binding.check1.isChecked && binding.check2.isChecked){
                val nextIntent = Intent(this, CreateProfileActivity::class.java)
                val name = intent.getStringExtra("userName")
                val email = intent.getStringExtra("userEmail")
                nextIntent.putExtra("userName",name)
                nextIntent.putExtra("userEmail",email)
                startActivity(nextIntent)
            } else{
                Toast.makeText(this,"필수 약관을 모두 동의해주세요",Toast.LENGTH_SHORT).show()
            }

        }
        /* 체크박스 */
        binding.checkAll.setOnClickListener{onCheckChanged(binding.checkAll)}
        binding.check1.setOnClickListener{onCheckChanged(binding.check1)}
        binding.check2.setOnClickListener{onCheckChanged(binding.check2)}
        binding.check3.setOnClickListener{onCheckChanged(binding.check3)}
        binding.check4.setOnClickListener{onCheckChanged(binding.check4)}
        /* 약관보기 */
        binding.text1.setOnClickListener{}

        /* 이전버튼 */
        binding.imgbtnBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun onCheckChanged(checkBox: CheckBox) = with(binding){
        when(checkBox.id){
            checkAll.id ->{
                if(checkAll.isChecked){
                    check1.isChecked = true
                    check2.isChecked = true
                    check3.isChecked = true
                    check4.isChecked = true
                } else{
                    check1.isChecked = false
                    check2.isChecked = false
                    check3.isChecked = false
                    check4.isChecked = false
                }
            }
            else ->{
                checkAll.isChecked = (check1.isChecked
                                && check2.isChecked
                                && check3.isChecked
                                && check4.isChecked)
            }
        }
        if(check1.isChecked && check2.isChecked){
            btnNext.setBackgroundColor(resources.getColor(R.color.main_blue))
            btnNext.setTextColor(Color.WHITE)
        } else{
            btnNext.setBackgroundColor(resources.getColor(R.color.light_grey))
            btnNext.setTextColor(Color.BLACK)
        }
    }
}