package org.retriever.dailypet.model.signup.pet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyPetResponse(
    val petId : Int,
    val petName : String,
    val profileImageUrl : String,
    val birthDate : String,
    val weight : Int,
    val registerNumber : String,
    val isNeutered : Boolean,
    val gender : String,
    val petKind : String,
    val petType : String,
)
