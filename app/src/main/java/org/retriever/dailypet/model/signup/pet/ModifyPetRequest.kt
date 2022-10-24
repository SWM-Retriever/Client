package org.retriever.dailypet.model.signup.pet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyPetRequest(
    val petName : String,
    val birthDate : String,
    val weight : Float,
    val isNeutered : Boolean,
    val registerNumber : String,
    val profileImageUrl : String,
)