package org.retriever.dailypet.model.diary

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class DiaryItem(
    val viewType: String,
    val date: String,
    val diaryId: Int,
    val authorImageUrl: String,
    val authorNickName: String,
    val diaryImageUrlList: List<String>,
    val diaryText: String,
)
