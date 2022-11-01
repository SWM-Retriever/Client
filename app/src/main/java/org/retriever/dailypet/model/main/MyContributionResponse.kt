package org.retriever.dailypet.model.main

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyContributionResponse(
    val contributionPercent : Float,
)
