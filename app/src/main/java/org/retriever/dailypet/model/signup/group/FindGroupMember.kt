package org.retriever.dailypet.model.signup.group

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FindGroupMember (
    val nickName: String,
    val memberId: Int,
    val profileImageUrl: String
)