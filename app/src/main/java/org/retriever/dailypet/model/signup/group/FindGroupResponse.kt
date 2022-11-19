package org.retriever.dailypet.model.signup.group

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FindGroupResponse(
    val familyId: Int,
    val familyName: String,
    val familyMemberCount: Int,
    val familyMemberList: List<FindGroupMember>,
)