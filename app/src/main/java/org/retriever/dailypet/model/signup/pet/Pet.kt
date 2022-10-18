package org.retriever.dailypet.model.signup.pet

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Pet(
    val petId : Int,
    val petName : String,
    val petImage : String = "",
):Parcelable