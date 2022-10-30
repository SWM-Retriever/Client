package org.retriever.dailypet.model.statistics

data class DetailStaticsItem(
    val viewType: String,
    val careName: String,
    val careList: List<CareItem>,
)