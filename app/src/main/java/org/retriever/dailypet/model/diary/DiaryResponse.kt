package org.retriever.dailypet.model.diary

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryResponse(
    val diaryList : List<DiaryItem>
)
