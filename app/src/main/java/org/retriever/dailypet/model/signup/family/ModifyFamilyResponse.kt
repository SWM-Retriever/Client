package org.retriever.dailypet.model.signup.family

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyFamilyResponse(
    val familyId: Int,
    val familyName: Int,
    val invitationCode: Int,
    val groupType: String,
)