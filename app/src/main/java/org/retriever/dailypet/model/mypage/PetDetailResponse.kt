package org.retriever.dailypet.model.mypage

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PetDetailResponse(
    val petInfoDetailList: List<PetDetailItem>
)
