package org.retriever.dailypet.model.image

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UrlResponse(
    val preSignedUrl: String,
)
