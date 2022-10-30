package org.retriever.dailypet.model.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailContributionResponse(
    val graphList: List<DetailContributionItem>
)
