package org.retriever.dailypet.model.signup.pet

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PetResponse(
    var familyId : Int,
    var familyName : String,
    var familyRoleName : String,
    var petList : List<Pet>,
    var invitationCode : String,
) : Parcelable

