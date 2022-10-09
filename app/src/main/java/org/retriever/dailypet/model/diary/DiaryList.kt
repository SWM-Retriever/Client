package org.retriever.dailypet.model.diary

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryList(
    val diaryList : List<Diary>
)
