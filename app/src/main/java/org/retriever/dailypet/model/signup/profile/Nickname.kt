package org.retriever.dailypet.model.signup.profile

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Nickname(
    val nickName: String,
)
