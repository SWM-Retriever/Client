package org.retriever.dailypet.data.repository.login

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.login.LoginApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.login.*
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginApiService: LoginApiService) : BaseRepo() {

    suspend fun postIsMember(member: Member): Resource<LoginResponse> =
        safeApiCall { loginApiService.postIsMember(member) }

    suspend fun getProgressStatus(jwt : String) : Resource<ProgressStatusResponse> =
        safeApiCall { loginApiService.getProgressStatus(jwt) }

    suspend fun postCheckProfileNickname(nickname: String): Resource<ResponseBody> =
        safeApiCall { loginApiService.postCheckProfileNickname(nickname) }

    suspend fun postProfile(registerProfile: RegisterProfile, image: MultipartBody.Part?): Resource<RegisterProfileResponse> =
        safeApiCall { loginApiService.postProfile(registerProfile, image) }

    suspend fun postCheckFamilyName(jwt: String, familyName: String): Resource<ResponseBody> =
        safeApiCall { loginApiService.postCheckFamilyName(jwt, familyName) }

    suspend fun postFamily(jwt: String, familyInfo: FamilyInfo): Resource<FamilyResponse> =
        safeApiCall { loginApiService.postFamily(jwt, familyInfo) }

    suspend fun postCheckPetName(familyId: Int, jwt: String, petName: String): Resource<ResponseBody> =
        safeApiCall { loginApiService.postCheckPetName(familyId, jwt, petName) }

}