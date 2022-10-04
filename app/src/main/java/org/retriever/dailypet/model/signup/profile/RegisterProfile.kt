package org.retriever.dailypet.model.signup.profile

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RegisterProfile(
    @Json(name = "snsNickName")
    var nickname: String,
    val email: String,
    @Json(name = "providerType")
    val domain: String,
    val deviceToken: String,
    var isPushAgree: Boolean = false,
    var isProfileInformationAgree: Boolean = false,
) : Parcelable
