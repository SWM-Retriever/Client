package org.retriever.dailypet.test.data.repository.login

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.test.data.network.LoginApiService
import org.retriever.dailypet.test.data.repository.BaseRepo
import org.retriever.dailypet.test.model.Resource
import org.retriever.dailypet.test.model.login.*
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginApiService: LoginApiService) : BaseRepo() {

    suspend fun postIsMember(member: Member): Resource<LoginResponse> = safeApiCall { loginApiService.postIsMember(member) }

    suspend fun postCheckProfileNickname(nickname: String): Resource<ResponseBody> =
        safeApiCall { loginApiService.postCheckProfileNickname(nickname) }

    suspend fun postProfile(registerProfile: RegisterProfile, image: MultipartBody.Part?): Resource<RegisterProfileResponse> =
        safeApiCall { loginApiService.postProfile(registerProfile, image) }

}