package org.retriever.dailypet.test.model.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FamilyInfo(
    val familyName: String,
    val familyRoleName: String,
)
