package org.retriever.dailypet.model.signup.family

import android.provider.ContactsContract
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EnterFamilyResponse(
    val familyId: Int,
    val familyName: String,
    val nickName: String,
    val invitationCode: String,
    val groupType : String,
    val profileImageUrl: String,
)