package org.retriever.dailypet.model.mypage

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyProfile(
    val nickName : String,
    val profileImageUrl : String
)
