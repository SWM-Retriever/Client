package org.retriever.dailypet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.retriever.dailypet.databinding.ActivityEnterFamilyBinding
import org.retriever.dailypet.databinding.ActivityFindFamilyBinding
import org.retriever.dailypet.interfaces.RetrofitService
import org.retriever.dailypet.models.General
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EnterFamilyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEnterFamilyBinding
    private lateinit var retrofit : Retrofit
    private lateinit var retrofitService : RetrofitService
    private var isValidNickname : Boolean = false
    private val TAG = "ENTER FAMILY ACTIVITY"
    private val BASE_URL = "https://dailypet.p.rapidapi.com/"
    private val KEY = "455e42b91cmshc6a9672a01080d5p13c40ajsn2e2c01284a4c"
    private val HOST = "dailypet.p.rapidapi.com"
    private val CODE_NICKNAME = 200
    private val CODE_FAIL = 400
    private val CODE_FAMILY = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterFamilyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var sub = binding.textEnterFamilySub.text
        sub = intent.getStringExtra("FamilyName") + sub
        binding.textEnterFamilySub.text = sub

        /* API Init */
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(RetrofitService::class.java)

        /* Check Code Validation */
        binding.btnFamilyNicknameCheck.setOnClickListener{
            Log.d(TAG, "Button Nickname Check")
            val nickname = binding.editTextEnterFamilyNickname.text.toString()
            if(nickname == ""){
                binding.textFamilyNicknameValidate.text = "올바른 닉네임을 입력해주세요"
                binding.textFamilyNicknameValidate.setTextColor(Color.RED)
                isValidNickname = false
            }
            else checkValidNickname(nickname)
        }

        /* Enter Family Member*/
        binding.btnEnterFamily.setOnClickListener{
            if(isValidNickname){
                val nickname = binding.editTextEnterFamilyNickname.text.toString()
                postEnterFamily(nickname)
            } else{
                Toast.makeText(this,"닉네임 중복체크를 진행해주세요",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkValidNickname(familyNickname : String){
        val call = retrofitService.postCheckFamilyNickName(KEY, HOST, familyNickname)
        call.enqueue(object : Callback<General> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<General>, response: Response<General>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == CODE_NICKNAME){ // 유효한 닉네임
                        binding.textFamilyNicknameValidate.text = "사용가능한 가족 내 닉네임입니다"
                        binding.textFamilyNicknameValidate.setTextColor(Color.BLUE)
                        isValidNickname = true
                    }
                }
                else{
                    if(response.code() == CODE_FAIL){ // 유효하지 않은 닉네임
                        binding.textFamilyNicknameValidate.text = "중복된 가족 내 닉네임입니다\n다른 닉네임을 사용해주세요"
                        binding.textFamilyNicknameValidate.setTextColor(Color.RED)
                    }
                }
            }
            override fun onFailure(call: Call<General>, t: Throwable) {
                Log.e(TAG, "연결 실패")
            }
        })
    }

    /* 프로필 등록 */
    private fun postEnterFamily(nickname : String){
        val call = retrofitService.postEnterFamily(KEY, HOST, nickname)
        call.enqueue(object : Callback<General> {
            override fun onResponse(call: Call<General>, response: Response<General>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == CODE_FAMILY){ // 프로필 등록 성공
                        Toast.makeText(applicationContext, "가족 입장에 성공하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    if(response.code() == CODE_FAIL){ // 프로필 등록 실패
                        Toast.makeText(applicationContext, "가족 입장에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<General>, t: Throwable) {
                Log.e(TAG, "연결 실패")
            }
        })
    }
}