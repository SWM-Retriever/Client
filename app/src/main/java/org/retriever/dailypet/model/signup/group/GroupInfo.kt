package org.retriever.dailypet.model.signup.group

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupInfo(
    val familyName: String,
    val familyRoleName: String,
)
