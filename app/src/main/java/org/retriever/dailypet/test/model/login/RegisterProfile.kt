package org.retriever.dailypet.test.model.login

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterProfile(
    @Json(name = "snsNickName")
    val nickname: String,
    val email: String,
    @Json(name = "providerType")
    val domain: String,
    val deviceToken: String,
    val isPushAgree: Boolean,
    val isProfileInformationAgree: Boolean,
)
