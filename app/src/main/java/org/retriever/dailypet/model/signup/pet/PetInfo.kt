package org.retriever.dailypet.model.signup.pet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PetInfo(
    val petName : String,
    val petType : String,
    val gender : String,
    val birthDate : String,
    val petKindId : Int,
    val weight : Float,
    val isNeutered : Boolean,
    val registerNumber: String,
    val profileImageUrl: String,
)

