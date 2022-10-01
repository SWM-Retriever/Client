package org.retriever.dailypet.test.model.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val snsNickName: String,
    val email: String,
    val jwtToken: String,
)
