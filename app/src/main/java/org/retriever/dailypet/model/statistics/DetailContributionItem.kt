package org.retriever.dailypet.model.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailContributionItem(
    val viewType: String,
    val careName: String,
    val careCountList: List<DetailCareItem>?
)
