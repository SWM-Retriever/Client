package org.retriever.dailypet.model.signup.family

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyFamilyResponse(
    val familyId: Int,
    val familyName: String,
    val invitationCode: String,
    val groupType: String,
)