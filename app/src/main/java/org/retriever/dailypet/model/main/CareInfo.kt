package org.retriever.dailypet.model.main

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CareInfo(
    val careName : String,
    val dayOfWeeks : MutableList<String>,
    val totalCountPerDay : Int,
)
