package org.retriever.dailypet.interfaces

import org.retriever.dailypet.models.GetTest
import org.retriever.dailypet.models.PostTest
import org.retriever.dailypet.models.UserAccount
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("test")
    fun getTest(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
    ): Call<GetTest>

    @FormUrlEncoded
    @POST("api/v1/login/kakao")
    fun postTest(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("authToken") value: String,
    ): Call<PostTest>

    @FormUrlEncoded
    @POST("posts/1")
    fun postAccessToken(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Field("source") source: String,
        @Field("target") target: String,
        @Field("text") text: String
    ): Call<UserAccount>
}