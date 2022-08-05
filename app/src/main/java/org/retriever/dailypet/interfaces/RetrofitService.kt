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
    ): Call<General>

    @FormUrlEncoded
    @POST("api/v1/api/v1/member")
    fun postProfile(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("nickname") nickname: String,
        @Field("email") email: String,
        @Field("imageURL") image: String,
    ): Call<General>

    @FormUrlEncoded
    @POST("api/v1/validation/familyName")
    fun postCheckFamilyName(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("familyName") familyName: String,
    ): Call<General>

    @FormUrlEncoded
    @POST("api/v1/validation/family-role-name")
    fun postCheckFamilyNickName(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("familyRoleName") nickname: String,
    ): Call<General>

    @FormUrlEncoded
    @POST("api/v1/api/v1/member")
    fun postFamily(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("familyName") familyName: String,
        @Field("familyRoleName") familyNickname: String,
        @Field("imageURL") image: String,
    ): Call<General>

    @FormUrlEncoded
    @POST("api/v1/family/invitation-code")
    fun postInviteCode(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("invitationCode") inviteCode: String,
    ): Call<FamilyInfo>
}