package org.retriever.dailypet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import org.retriever.dailypet.databinding.ActivityFindFamilyBinding
import org.retriever.dailypet.interfaces.RetrofitService
import org.retriever.dailypet.models.FamilyInfo
import org.retriever.dailypet.models.General
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FindFamilyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFindFamilyBinding
    private lateinit var retrofit : Retrofit
    private lateinit var retrofitService : RetrofitService
    private var isValidCode : Boolean = false
    private val TAG = "FIND FAMILY ACTIVITY"
    private val BASE_URL = "https://dailypet.p.rapidapi.com/"
    private val KEY = "455e42b91cmshc6a9672a01080d5p13c40ajsn2e2c01284a4c"
    private val HOST = "dailypet.p.rapidapi.com"
    private val CODE_NICKNAME = 200
    private val CODE_FAIL = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindFamilyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /* API Init */
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(RetrofitService::class.java)

        /* Check Code Validation */
        binding.btnSearchInviteCode.setOnClickListener{
            Log.d(TAG, "Button InviteCode Check")
            val inviteCode = binding.editTextInviteCode.text.toString()
            if(inviteCode == ""){
                binding.textInviteCodeValidate.text = "올바른 코드를 입력해주세요"
                binding.textInviteCodeValidate.setTextColor(Color.RED)
                isValidCode = false
                setVisibility()
            }
            else checkValidCode(inviteCode)
        }

        /* Register Family Member Page*/
        binding.btnSelectFamily.setOnClickListener{
            val nextIntent = Intent(this, EnterFamilyActivity::class.java)
            startActivity(nextIntent) // 가족 구성원 등록 페이지로 이동
        }
    }

    private fun checkValidCode(inviteCode : String){
        val call = retrofitService.postInviteCode(KEY, HOST, inviteCode)
        call.enqueue(object : Callback<FamilyInfo> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<FamilyInfo>, response: Response<FamilyInfo>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == CODE_NICKNAME){ // 유효한 코드
                        binding.textInviteCodeValidate.text = ""
                        binding.textInviteCodeValidate.setTextColor(Color.BLUE)
                        isValidCode = true
                        val familCount = response.body()?.familyCount
                        binding.textFamilyName.text = response.body()?.familyName + " ($familCount 명)"
                        setVisibility()
                    }
                }
                else{
                    if(response.code() == CODE_FAIL){ // 유효하지 않은 코드
                        setVisibility()
                        binding.textInviteCodeValidate.text = "유효하지 않은 코드입니다"
                        binding.textInviteCodeValidate.setTextColor(Color.RED)
                        isValidCode = false
                    }
                }
            }
            override fun onFailure(call: Call<FamilyInfo>, t: Throwable) {
                Log.e(TAG, "연결 실패")
            }
        })
    }

    private fun setVisibility(){
        if(isValidCode){
            binding.imgFamilyPhoto.visibility = View.VISIBLE
            binding.btnSelectFamily.visibility = Button.VISIBLE
            binding.textInvisibleComment.visibility = View.VISIBLE
        } else{
            binding.imgFamilyPhoto.visibility = View.INVISIBLE
            binding.textFamilyName.visibility = View.INVISIBLE
            binding.btnSelectFamily.visibility = Button.INVISIBLE
            binding.textInvisibleComment.visibility = View.INVISIBLE
        }
    }
}