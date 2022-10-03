package org.retriever.dailypet

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

@HiltAndroidApp
class GlobalApplication : Application() {
    companion object {
        lateinit var instance: GlobalApplication
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        instance = this
        prefs = Prefs(applicationContext)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        NaverIdLoginSDK.initialize(this, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, "반려하루")
        super.onCreate()
    }

    fun context(): Context = applicationContext
}

class Prefs(context: Context) {
    private val prefName = "dailyPet"
    private val prefs = context.getSharedPreferences(prefName, MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    var jwt: String?
        get() = prefs.getString("jwt", null)
        set(value) {
            prefs.edit().putString("jwt", value).apply()
        }
    var familyId: Int
        get() = prefs.getInt("familyId", -1)
        set(value) {
            prefs.edit().putInt("familyId", value).apply()
        }

    fun jwtInit() {
        prefs.edit().putString("jwt", null).apply()
    }

    fun familyIdInit() {
        prefs.edit().putInt("familyId", -1).apply()
    }
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", GlobalApplication.prefs.jwt ?: " ")
            .build()
        return chain.proceed(request)
    }
}

class NullOnEmptyConverterFactory : Converter.Factory() { // empty response 처리
    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        val delegate = retrofit!!.nextResponseBodyConverter<Any>(this, type!!, annotations!!)
        return Converter<ResponseBody, Any> {
            if (it.contentLength() == 0L) return@Converter EmptyResponse()
            delegate.convert(it)
        }
    }

    class EmptyResponse {

    }

}