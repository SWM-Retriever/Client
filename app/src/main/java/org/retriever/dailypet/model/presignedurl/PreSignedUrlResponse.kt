package org.retriever.dailypet.model.presignedurl

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PreSignedUrlResponse(
    @Json(name = "presignedUrl")
    val preSignedUrl: String,
    val originalUrl: String,
)
