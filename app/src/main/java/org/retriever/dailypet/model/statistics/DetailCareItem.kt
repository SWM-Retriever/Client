package org.retriever.dailypet.model.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailCareItem(
    val familyRoleName: String,
    val careCount: Float,
)
