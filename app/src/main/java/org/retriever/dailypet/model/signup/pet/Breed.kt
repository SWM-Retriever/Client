package org.retriever.dailypet.model.signup.pet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Breed(
    val petKindId: Int,
    val petKindName: String,
)
