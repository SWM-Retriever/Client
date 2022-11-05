package org.retriever.dailypet.model.signup.group

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyGroupResponse(
    val familyId: Int,
    val familyName: String,
    val invitationCode: String,
    val groupType: String,
)