package org.retriever.dailypet.interfaces

import org.retriever.dailypet.models.Test
import org.retriever.dailypet.models.UserAccount
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("test")
    fun getTest(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
    ): Call<Test>

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