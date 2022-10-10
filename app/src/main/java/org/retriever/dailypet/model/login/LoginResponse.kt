package org.retriever.dailypet.model.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val nickName: String,
    val email: String,
    val jwtToken: String,
    val familyId : Int,
    val familyName : String,
    val invitationCode : String,
    val petIdList : List<Int>,
)
