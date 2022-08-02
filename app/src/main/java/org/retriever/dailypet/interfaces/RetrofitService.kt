package org.retriever.dailypet.interfaces

import org.retriever.dailypet.models.GetTest
import org.retriever.dailypet.models.Message
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
    @POST("api/v1/login")
    fun postIsMember(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("nickname") nickname: String,
        @Field("email") email: String,
    ): Call<Message>
}