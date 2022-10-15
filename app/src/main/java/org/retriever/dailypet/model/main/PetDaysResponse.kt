package org.retriever.dailypet.model.main

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PetDaysResponse(
    val userName : String,
    val petName : String,
    val calculatedDay : Int,
)
