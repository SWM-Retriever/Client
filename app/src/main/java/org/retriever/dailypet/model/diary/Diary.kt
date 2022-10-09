package org.retriever.dailypet.model.diary

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Diary(
    val viewType : String,
    val date : Date,
    val diaryId : Int,
    val writerImage : String,
    val writerNickName : String,
    val diaryImage : String,
    val diaryContent : String,
)
