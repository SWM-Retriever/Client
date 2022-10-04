package org.retriever.dailypet.model.signup.profile

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterProfileResponse(
    val jwtToken: String,
)