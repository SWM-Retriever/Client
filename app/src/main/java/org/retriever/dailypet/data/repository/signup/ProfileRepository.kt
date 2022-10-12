package org.retriever.dailypet.data.repository.signup

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.signup.ProfileApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.model.signup.profile.RegisterProfileResponse
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val profileApiService: ProfileApiService) : BaseRepo() {

    suspend fun postCheckProfileNickname(nickname: String): Resource<ResponseBody> =
        safeApiCall { profileApiService.postCheckProfileNickname(nickname) }

    suspend fun postProfile(registerProfile: RegisterProfile): Resource<RegisterProfileResponse> =
        safeApiCall { profileApiService.postProfile(registerProfile) }

}