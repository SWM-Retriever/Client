package org.retriever.dailypet.models

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import okhttp3.Interceptor
import okhttp3.Response
import org.retriever.dailypet.R

class App : Application() {
    companion object{
        lateinit var instance : App
        lateinit var prefs : Prefs
    }

    override fun onCreate() {
        instance = this
        prefs = Prefs(applicationContext)
        KakaoSdk.init(this, resources.getString(R.string.kakao_native_app_key))
        NaverIdLoginSDK.initialize(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), "반려하루")
        super.onCreate()
    }

    fun context(): Context = applicationContext
}

class Prefs(context: Context){
    private val prefName = "jwt"
    private val prefs = context.getSharedPreferences(prefName, MODE_PRIVATE)
    var token: String?
        get() = prefs.getString("token", null)
        set(value){
            prefs.edit().putString("token",value).apply()
        }
    fun init(){
        prefs.edit().putString("token",null).apply()
    }
}

class AuthInterceptor: Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization",App.prefs.token?: " ")
            .build()
        return chain.proceed(request)
    }
}