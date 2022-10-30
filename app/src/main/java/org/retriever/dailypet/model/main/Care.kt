package org.retriever.dailypet.model.main

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Care(
    val careId: Int,
    val careName: String,
    val totalCareCount: Int,
    val currentCount: Int,
    val checkList: List<CheckList>,
    val dayOfWeeks: List<String>,
):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CheckList(
    val memberId: Int,
    val familyRoleName: String? = "",
):Parcelable