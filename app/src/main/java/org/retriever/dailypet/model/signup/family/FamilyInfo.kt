package org.retriever.dailypet.model.signup.family

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FamilyInfo(
    val familyName: String,
    val familyRoleName: String,
)