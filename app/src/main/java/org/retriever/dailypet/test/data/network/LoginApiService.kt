package org.retriever.dailypet.test.data.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.models.General
import org.retriever.dailypet.test.model.login.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LoginApiService {

    @Headers("accept: Application/json", "Content-type: Application/json")
    @POST("api/v1/auth/login")
    suspend fun postIsMember(@Body member: Member): Response<LoginResponse>

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/v1/validation/nickname")
    suspend fun postCheckProfileNickname(@Body nickName: String): Response<ResponseBody>

    @Multipart
    @POST("api/v1/auth/sign-up")
    suspend fun postProfile(
        @Part("dto") registerProfile: RegisterProfile,
        @Part image: MultipartBody.Part?,
    ): Response<RegisterProfileResponse>

    @POST("api/v1/validation/family-name")
    suspend fun postCheckFamilyName(
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body familyName: String,
    ): Response<ResponseBody>

    @POST("api/v1/validation/family")
    suspend fun postFamily(
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body familyInfo: FamilyInfo,
    ): Response<ResponseBody>

}