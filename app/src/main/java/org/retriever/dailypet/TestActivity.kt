package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.retriever.dailypet.interfaces.RetrofitService
import org.retriever.dailypet.models.GetTest
import org.retriever.dailypet.models.PostTest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var retrofit : Retrofit
    private lateinit var retrofit2 : Retrofit
    private lateinit var retrofitService : RetrofitService
    private lateinit var retrofitService2 : RetrofitService
    private var KEY = "455e42b91cmshc6a9672a01080d5p13c40ajsn2e2c01284a4c"
    private var HOST = "test11639.p.rapidapi.com"
    private var BASE_URL = "https://test11639.p.rapidapi.com/"
    private var POST_URL = "https://test11639.p.rapidapi.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        firebaseAnalytics = Firebase.analytics

        /* API Init */
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(RetrofitService::class.java)
        retrofit2 = Retrofit.Builder()
            .baseUrl(POST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService2 = retrofit2.create(RetrofitService::class.java)

        val crashButton = findViewById<Button>(R.id.btn_crashTest)
        val log1Button = findViewById<Button>(R.id.btn_logTest1)
        val log2Button = findViewById<Button>(R.id.btn_logTest2)
        val backButton = findViewById<Button>(R.id.btn_backToMain)
        val getButton = findViewById<Button>(R.id.btn_apiGetTest)
        val postButton = findViewById<Button>(R.id.btn_apiPostTest)

        crashButton.setOnClickListener{
            throw RuntimeException("Test Crash") // Force a crash
        }
        log1Button.setOnClickListener {
            val bundle = Bundle().apply {
                this.putString("level1", "Button")
            }
            firebaseAnalytics.logEvent("button1", bundle)
        }
        log2Button.setOnClickListener {
            val bundle = Bundle().apply {
                this.putString("level2", "button2")
            }
            firebaseAnalytics.logEvent("Button2", bundle)
        }
        getButton.setOnClickListener {
            get()
        }
        postButton.setOnClickListener {
            post()
        }

        backButton.setOnClickListener {
            val nextIntent = Intent(this, StartActivity::class.java)
            startActivity(nextIntent)
        }
    }

    private fun get(){
        val callGetTest = retrofitService.getTest(KEY, HOST)
        callGetTest.enqueue(object : Callback<GetTest> {
            override fun onResponse(call: Call<GetTest>, response: Response<GetTest>) {
                if(response.isSuccessful) {
                    val result: String = response.body().toString()
                    Log.d("Test", result)
                    Toast.makeText(applicationContext, result,Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(applicationContext, "GET Response 실패",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<GetTest>, t: Throwable) {
                Toast.makeText(applicationContext, "GET 실패",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun post(){
        val callPostTest = retrofitService2.postTest(KEY, HOST, "ashpurple")
        callPostTest.enqueue(object : Callback<PostTest> {
            override fun onResponse(call: Call<PostTest>, response: Response<PostTest>) {
                if(response.isSuccessful) {
                    val result: String = response.body().toString()
                    Log.d("Test", result)
                    Toast.makeText(applicationContext, result,Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(applicationContext, "POST Response 실패",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PostTest>, t: Throwable) {
                Toast.makeText(applicationContext, "POST 실패",Toast.LENGTH_SHORT).show()
            }
        })
    }
}