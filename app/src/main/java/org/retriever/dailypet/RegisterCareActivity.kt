package org.retriever.dailypet

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import org.retriever.dailypet.databinding.ActivityRegisterCareBinding

class RegisterCareActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterCareBinding
    private var SUBMIT = false
    private val TAG = "REGISTER CARE ACTIVITY"
    private var food = false
    private var walk = false
    private var wash = false
    private var play = false
    private var teeth = false
    private var snack = false
    private var mon = false
    private var tue = false
    private var wed = false
    private var thu = false
    private var fri = false
    private var sat = false
    private var sun = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterCareBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /* Text Listener */
        binding.edittextNumber.addTextChangedListener(object : TextWatcher {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun afterTextChanged(p0: Editable?) {
                submitCheck()
                if(binding.edittextNumber.text.isNotBlank()){
                    binding.edittextNumber.background = applicationContext.getDrawable(R.drawable.whiteblue_click_button)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        init()
    }

    private fun init() = with(binding){
        btnCareRegisterSubmit.setOnClickListener{
            Toast.makeText(applicationContext, "챙겨주기 항목이 등록되었습니다", Toast.LENGTH_SHORT).show()
            val nextIntent = Intent(applicationContext, MainActivity::class.java)
            var flag = intent.getIntExtra("flag", 0)
            nextIntent.putExtra("flag", flag)
            Log.e("CNT", flag.toString())
            startActivity(nextIntent)
        }

        /* Checkbox Listener */
        var listener = CompoundButton.OnCheckedChangeListener{
                buttonView, isChecked ->
            if(isChecked){
                when(buttonView.id){
                    R.id.check_everyday -> setDay(true)
                    R.id.check_sun -> {sun = true}
                    R.id.check_mon -> {mon = true}
                    R.id.check_tue -> {tue = true}
                    R.id.check_wed -> {wed = true}
                    R.id.check_thu -> {thu = true}
                    R.id.check_fri -> {fri = true}
                    R.id.check_sat -> {sat = true}
                }
            } else{
                when(buttonView.id){
                    R.id.check_everyday -> setDay(false)
                    R.id.check_sun -> {sun = false}
                    R.id.check_mon -> {mon = false}
                    R.id.check_tue -> {tue = false}
                    R.id.check_wed -> {wed = false}
                    R.id.check_thu -> {thu = false}
                    R.id.check_fri -> {fri = false}
                    R.id.check_sat -> {sat = false}
                }
            }
            submitCheck()
        }
        checkEveryday.setOnCheckedChangeListener(listener)
        checkMon.setOnCheckedChangeListener(listener)
        checkTue.setOnCheckedChangeListener(listener)
        checkWed.setOnCheckedChangeListener(listener)
        checkThu.setOnCheckedChangeListener(listener)
        checkFri.setOnCheckedChangeListener(listener)
        checkSat.setOnCheckedChangeListener(listener)
        checkSun.setOnCheckedChangeListener(listener)

        /* Pet Type */
        btnFood.setOnClickListener{
            select(btnFood)
            food = true
            submitCheck()
        }
        btnPlay.setOnClickListener{
            select(btnPlay)
            play = true
            submitCheck()
        }
        btnSnack.setOnClickListener{
            select(btnSnack)
            snack = true
            submitCheck()
        }
        btnTeeth.setOnClickListener{
            select(btnTeeth)
            teeth = true
            submitCheck()
        }
        btnWalk.setOnClickListener{
            select(btnWalk)
            walk = true
            submitCheck()
        }
        btnWash.setOnClickListener{
            select(btnWash)
            wash = true

        }



        /* Previous button */
        btnX.setOnClickListener{
            onBackPressed()
        }
    }

    private fun setDay(check : Boolean){
        mon = check
        tue = check
        wed = check
        thu = check
        fri = check
        sat = check
        sun = check
        binding.checkMon.isChecked = check
        binding.checkTue.isChecked = check
        binding.checkWed.isChecked = check
        binding.checkThu.isChecked = check
        binding.checkFri.isChecked = check
        binding.checkSat.isChecked = check
        binding.checkSun.isChecked = check
    }

    private fun select(selectedBtn: AppCompatButton) = with(binding){
        btnFood.isSelected = false
        btnPlay.isSelected = false
        btnSnack.isSelected = false
        btnTeeth.isSelected = false
        btnWalk.isSelected = false
        btnWash.isSelected = false
        food= false
        play = false
        snack = false
        teeth = false
        walk = false
        wash = false
        selectedBtn.isSelected = true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun submitCheck(){
        val day = mon || tue || wed || thu || fri || sat || sun
        val care = food || walk || wash || play || teeth || snack
        val number = binding.edittextNumber.text.isNotBlank()
        SUBMIT = day && care && number
        if(SUBMIT){
            binding.btnCareRegisterSubmit.background = applicationContext.getDrawable(R.drawable.blue_button)
            binding.btnCareRegisterSubmit.setTextColor(applicationContext.getColor(R.color.white))
        } else{
            binding.btnCareRegisterSubmit.background = applicationContext.getDrawable(R.drawable.grey_button)
            binding.btnCareRegisterSubmit.setTextColor(applicationContext.getColor(R.color.grey))
        }
    }
}