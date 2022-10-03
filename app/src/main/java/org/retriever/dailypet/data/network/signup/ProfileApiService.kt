package org.retriever.dailypet.data.network.signup

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.model.signup.profile.RegisterProfileResponse
import retrofit2.Response
import retrofit2.http.*

interface ProfileApiService {

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/v1/validation/nickname")
    suspend fun postCheckProfileNickname(@Body nickName: String): Response<ResponseBody>

    @Multipart
    @POST("api/v1/auth/sign-up")
    suspend fun postProfile(
        @Part("dto") registerProfile: RegisterProfile,
        @Part image: MultipartBody.Part?,
    ): Response<RegisterProfileResponse>

}