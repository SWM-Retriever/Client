package org.retriever.dailypet.test.model.login

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
