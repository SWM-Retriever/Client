package org.retriever.dailypet.model.signup.pet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PetList(
    val petInfoDetailList : List<PetListInfo>
)

@JsonClass(generateAdapter = true)
data class PetListInfo(
    val petId : Int,
    val petName : String,
    val profileImageUrl: String,
    val birthDate : String,
    val weight : Float,
    val registerNumber: String,
    val isNeutered : Boolean,
    val gender : String,
    val petKind : String,
)
