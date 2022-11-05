package org.retriever.dailypet.model.signup.group

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupResponse(
    val familyId : Int
)