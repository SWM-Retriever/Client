package org.retriever.dailypet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import org.retriever.dailypet.databinding.ActivityFindGroupBinding
import org.retriever.dailypet.interfaces.RetrofitService
import org.retriever.dailypet.models.FamilyInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FindGroupActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFindGroupBinding
    private lateinit var retrofit : Retrofit
    private lateinit var retrofitService : RetrofitService
    private var isValidCode : Boolean = false
    private val TAG = "FIND FAMILY ACTIVITY"
    private val BASE_URL = "https://dailypet.p.rapidapi.com/"
    private val KEY = "455e42b91cmshc6a9672a01080d5p13c40ajsn2e2c01284a4c"
    private val HOST = "dailypet.p.rapidapi.com"
    private val CODE_NICKNAME = 200
    private val CODE_FAIL = 400
    private var groupName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindGroupBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        /* API Init */
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(RetrofitService::class.java)
        init()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun init() = with(binding){
        /* Check Code Validation */
        btnSearchInviteCode.setOnClickListener{
            Log.d(TAG, "Button InviteCode Check")
            val inviteCode = editTextCode.text.toString()
            Log.e(TAG, inviteCode)
            if(inviteCode.isBlank()){
                textInviteCodeValidate.text = "올바른 코드를 입력해주세요"
                textInviteCodeValidate.setTextColor(applicationContext.getColor(R.color.fail_red))
                editTextCode.background = applicationContext.getDrawable(R.drawable.fail_edittext)
                isValidCode = false
                setVisibility()
            }
            else checkValidCode(inviteCode)
        }

        /* Register Family Member Page*/
        btnSelectFamily.setOnClickListener{
            if(isValidCode){
                val nextIntent = Intent(applicationContext, EnterFamilyActivity::class.java)
                nextIntent.putExtra("FamilyName", groupName)
                startActivity(nextIntent) // 가족 구성원 등록 페이지로 이동
            }
        }
    }

    private fun checkValidCode(inviteCode : String){
        val call = retrofitService.postInviteCode(KEY, HOST, inviteCode)
        call.enqueue(object : Callback<FamilyInfo> {
            @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
            override fun onResponse(call: Call<FamilyInfo>, response: Response<FamilyInfo>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == CODE_NICKNAME){ // 유효한 코드
                        binding.textInviteCodeValidate.text = ""
                        binding.editTextCode.background = applicationContext.getDrawable(R.drawable.success_edittext)
                        isValidCode = true
                        val memberCnt = response.body()?.familyCount
                        groupName = response.body()?.familyName
                        binding.textFamilyName.text = response.body()?.familyName + " ($memberCnt 명)"
                        setVisibility()
                    }
                }
                else{
                    if(response.code() == CODE_FAIL){ // 유효하지 않은 코드
                        setVisibility()
                        binding.textInviteCodeValidate.setTextColor(applicationContext.getColor(R.color.fail_red))
                        binding.editTextCode.background = applicationContext.getDrawable(R.drawable.fail_edittext)
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
            binding.textFamilyName.visibility = View.VISIBLE
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