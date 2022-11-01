package org.retriever.dailypet.model.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContributionItem(
    val rank: Int,
    val familyRoleName: String,
    val contributionPercent: Float,
    val careInfoDetailList: List<ContributionCareItem>
)
