package org.retriever.dailypet.model.login

import com.squareup.moshi.JsonClass
import org.retriever.dailypet.model.signup.pet.Pet

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val nickName: String,
    val email: String,
    val jwtToken: String,
    val familyId: Int,
    val familyName: String,
    val invitationCode: String,
    val groupType: String,
    val profileImageUrl: String,
    val petList: List<Pet>,
)
