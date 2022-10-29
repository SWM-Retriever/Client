package org.retriever.dailypet.model.main

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class GroupInfo(
    val familyId : Int,
    val familyName : String,
    val familyMemberCount: Int,
    val familyMemberList : List<Member>,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Member(
    val memberId : Int,
    val familyRoleName : String,
    val profileImageUrl: String,
) : Parcelable