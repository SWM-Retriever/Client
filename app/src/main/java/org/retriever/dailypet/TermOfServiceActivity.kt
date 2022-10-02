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

class TermOfServiceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTermOfServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermOfServiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }
    private fun init() = with(binding){
        /* 다음 버튼 */
        btnNext.setOnClickListener{
            if(check1.isChecked && check2.isChecked){
             /* //  val nextIntent = Intent(applicationContext, CreateProfileActivity::class.java)
                val name = intent.getStringExtra("userName")
                val email = intent.getStringExtra("userEmail")
                val domain = intent.getStringExtra("domain")
                nextIntent.putExtra("userName",name)
                nextIntent.putExtra("userEmail",email)
                nextIntent.putExtra("domain", domain)
                nextIntent.putExtra("option1", check3.isChecked)
                nextIntent.putExtra("option2", check4.isChecked)
                startActivity(nextIntent)*/
            } else{
                Toast.makeText(applicationContext,"필수 약관을 모두 동의해주세요",Toast.LENGTH_SHORT).show()
            }

        }
        /* 체크박스 */
        checkAll.setOnClickListener{onCheckChanged(checkAll)}
        check1.setOnClickListener{onCheckChanged(check1)}
        check2.setOnClickListener{onCheckChanged(check2)}
        check3.setOnClickListener{onCheckChanged(check3)}
        check4.setOnClickListener{onCheckChanged(check4)}
        /* 약관보기 */
        text1.setOnClickListener{}

        /* 이전버튼 */
        imgbtnBack.setOnClickListener{
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