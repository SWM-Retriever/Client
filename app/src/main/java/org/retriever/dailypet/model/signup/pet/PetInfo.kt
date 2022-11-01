package org.retriever.dailypet.model.signup.pet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PetInfo(
    var petName : String,
    var petType : String,
    var gender : String,
    var birthDate : String,
    var petKindId : Int,
    var weight : Float,
    var isNeutered : Boolean,
    var registerNumber: String,
    var profileImageUrl: String,
)

