package org.retriever.dailypet.model.login

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(

    @Json(name = "snsNickName")
    val nickname: String,
    val email: String,
    @Json(name = "providerType")
    val domain: String,

)