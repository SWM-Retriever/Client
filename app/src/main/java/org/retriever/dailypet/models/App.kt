package org.retriever.dailypet.models

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import okhttp3.Interceptor
import okhttp3.Response

class App : Application() {
    companion object{
        lateinit var prefs : Prefs
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
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