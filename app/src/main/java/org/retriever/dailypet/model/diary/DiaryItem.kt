package org.retriever.dailypet.model.diary

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class DiaryItem(
    val viewType: String,
    val date: String,
    val diaryId: Int?,
    val authorImageUrl: String?,
    val authorNickName: String?,
    val diaryImageUrl: String?,
    val diaryText: String?,
) : Parcelable
