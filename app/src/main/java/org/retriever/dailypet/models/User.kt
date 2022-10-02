package org.retriever.dailypet.models

import com.google.gson.annotations.SerializedName
import org.retriever.dailypet.test.model.login.RegisterProfile

data class GetTest(
    @SerializedName("message")
    val testMessage: String = ""
)

data class PostTest(
    @SerializedName("userInfo")
    val userInfo: String = "",
    @SerializedName("accessToken")
    val accessToken: String = "",
    @SerializedName("refreshToken")
    val refreshToken: String = "",
)





data class Temp(
    @SerializedName("dto")
    val registerProfile: RegisterProfile,
    @SerializedName("image")
    val str: String,
)

data class Message(
    val abc : String,
)




data class JWT(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("jwtToken")
    val status: String
)

data class General(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

data class FamilyInfo(
    @SerializedName("familyName")
    val familyName: String,
    @SerializedName("familyCount")
    val familyCount: Int,
    @SerializedName("profileImage")
    val familyProfile: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
)