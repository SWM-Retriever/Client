package org.retriever.dailypet.models

import com.google.gson.annotations.SerializedName

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

data class Message(
    @SerializedName("error")
    val error: String = "",
    @SerializedName("message")
    val message: String = "",
)

data class UserAccount(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
)