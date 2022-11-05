package org.retriever.dailypet.model.signup.group

import com.squareup.moshi.JsonClass
import org.retriever.dailypet.model.mypage.GroupMember

@JsonClass(generateAdapter = true)
data class FindGroupResponse(
    val familyId: Int,
    val familyName: String,
    val familyMemberCount: Int,
    val familyMemberList: List<GroupMember>
)