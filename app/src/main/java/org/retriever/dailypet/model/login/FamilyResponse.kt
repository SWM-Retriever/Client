package org.retriever.dailypet.model.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FamilyResponse(
    val familyId : Int
)