package org.retriever.dailypet.model.signup.pet

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PetResponse(
    var familyId: Int,
    val familyName: String? = "",
    val nickName: String,
    val invitationCode: String?,
    val groupType: String,
    val profileImageUrl: String,
) : Parcelable

