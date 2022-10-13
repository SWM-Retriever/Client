package org.retriever.dailypet.model.main

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CareList(
    val petId : Int,
    val caresInfoList: List<Care>
): Parcelable

