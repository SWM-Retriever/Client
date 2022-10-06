package org.retriever.dailypet

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.JsonArray
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp
import org.json.JSONArray

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

    var deviceToken: String?
        get() = prefs.getString("deviceToken", null)
        set(value) {
            prefs.edit().putString("deviceToken", value).apply()
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

    var petIdList: String?
        get() = jsonToList(JSONArray(prefs.getString("petIdList", null))).toString()

        set(value) {
            prefs.edit().putString("petIdList", value).apply()
        }

    fun initDeviceToken() {
        prefs.edit().putString("deviceToken", null).apply()
    }

    fun initJwt() {
        prefs.edit().putString("jwt", null).apply()
    }

    fun initFamilyId() {
        prefs.edit().putInt("familyId", -1).apply()
    }

    fun initPetIdList() {
        prefs.edit().putString("petIdList", null).apply()
    }

    fun getPetIdList(): MutableList<Int> {
        return jsonToList(JSONArray(prefs.getString("petIdList", null)))
    }

    private fun jsonToList(jsonArray: JSONArray): MutableList<Int> {
        val list = mutableListOf<Int>()
        for(i in 0 until jsonArray.length()){
            list.add(jsonArray.optInt(i))
        }
        return list
    }

}
