package org.retriever.dailypet.model.main

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CareInfo(
    val careName : String,
    val dayOfWeeks : MutableList<String>,
    val totalCountPerDay : Int,
)

@JsonClass(generateAdapter = true)
data class CareModifyInfo(
    val dayOfWeeks : MutableList<String>,
    val totalCountPerDay : Int,
)