package org.retriever.dailypet.models

import com.google.gson.annotations.SerializedName

data class Test(
    @SerializedName("message")
    val testMessage: String = ""
)

data class Token(
    @SerializedName("accessToken")
    val accessToken: String
)

data class UserAccount(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
)