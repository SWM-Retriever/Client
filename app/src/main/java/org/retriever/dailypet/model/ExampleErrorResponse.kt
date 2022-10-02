package org.retriever.dailypet.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExampleErrorResponse(
    val errorCode: String,
    val message: String
)