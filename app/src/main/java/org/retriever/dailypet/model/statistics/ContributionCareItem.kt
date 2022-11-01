package org.retriever.dailypet.model.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContributionCareItem(
    val careName: String,
    val totalCareCount: Int,
    val myCareCount: Int,
)
