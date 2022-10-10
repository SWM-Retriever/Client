package org.retriever.dailypet.model.signup.family

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FindGroupResponse(
    val familyId: Int,
    val familyName: String,
    val familyMemberCount: Int,
)