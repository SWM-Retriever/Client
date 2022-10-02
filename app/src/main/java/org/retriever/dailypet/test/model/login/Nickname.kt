package org.retriever.dailypet.test.model.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Nickname(
    val nickName: String,
)
