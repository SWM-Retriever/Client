package org.retriever.dailypet.model.mypage

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PetDetailItem(
    val petId: Int,
    val petName: String,
    val profileImageUrl: String,
    val birthDate: String,
    val weight: Float,
    val registerNumber: String,
    val isNeutered: Boolean,
    val gender: String,
    val petType: String,
    val petKind: String,
) : Parcelable
