package org.retriever.dailypet.model.mypage

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupResponse(
    val familyId: Int,
    val familyMemberCount: Int,
    val familyMemberList: List<GroupMember>,
    val familyName: String
)