package org.retriever.dailypet.model.mypage

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupMember(
    val familyRoleName: String,
    val memberId: Int,
    val profileImageUrl: String
)