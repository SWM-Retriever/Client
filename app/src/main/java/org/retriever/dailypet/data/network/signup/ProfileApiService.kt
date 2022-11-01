package org.retriever.dailypet.data.network.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.model.signup.profile.RegisterProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ProfileApiService {

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/v1/validation/nickname")
    suspend fun postCheckProfileNickname(@Body nickName: String): Response<ResponseBody>

    @POST("api/v1/auth/sign-up")
    suspend fun postProfile(
        @Body registerProfile: RegisterProfile,
    ): Response<RegisterProfileResponse>

    // TODO Mulipart로 URL 받아오기
//    @Multipart
//    @POST("api/v1/auth/sign-up")
//    suspend fun postProfile(
//        @Part("dto") registerProfile: RegisterProfile,
//        @Part image: MultipartBody.Part?,
//    ): Response<RegisterProfileResponse>

}