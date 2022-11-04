package org.retriever.dailypet.model.signup.group

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EnterGroupResponse(
    val familyId: Int,
    val familyName: String,
    val nickName: String,
    val invitationCode: String,
    val groupType : String,
    val profileImageUrl: String,
)