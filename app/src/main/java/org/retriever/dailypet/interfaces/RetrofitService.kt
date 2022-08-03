package org.retriever.dailypet.interfaces

import org.retriever.dailypet.models.*
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
    @POST("api/v1/login")
    fun postIsMember(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("nickname") nickname: String,
        @Field("email") email: String,
    ): Call<Message>

    @FormUrlEncoded
    @POST("api/v1/validation/nickname")
    fun postCheckNickname(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("nickname") nickname: String,
    ): Call<NicknameCheck>
}