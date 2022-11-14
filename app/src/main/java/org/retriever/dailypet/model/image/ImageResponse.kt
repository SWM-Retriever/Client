package org.retriever.dailypet.model.image

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageResponse(
    val imageUrl: String,
)
