package org.retriever.dailypet.interfaces

import okhttp3.MultipartBody
import org.retriever.dailypet.models.*
import org.retriever.dailypet.test.model.login.Member
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


//    @Headers("accept: application/json", "content-type: application/json")
//    @POST("api/v1/auth/sign-up")
//    fun postProfile(
//        @Body temp: Temp,
//    ): Call<JWT>



    @FormUrlEncoded
    @POST("api/v1/validation/family-role-name")
    fun postCheckFamilyNickName(
        @Field("familyRoleName") nickname: String,
    ): Call<General>

    @Multipart
    @POST("api/v1/api/v1/member")
    fun postFamily(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Part("familyName") familyName: String,
        @Part("familyRoleName") familyNickname: String,
        @Part image: MultipartBody.Part?,
    ): Call<General>

    @FormUrlEncoded
    @POST("api/v1/family/invitation-code")
    fun postInviteCode(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("invitationCode") inviteCode: String,
    ): Call<FamilyInfo>

    @FormUrlEncoded
    @POST("api/v1/family/api/v1/families/5")
    fun postEnterFamily(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("familyRoleName") nickname: String,
    ): Call<General>

    @FormUrlEncoded
    @POST("api/v1/validation/pet-name'")
    fun postCheckPetName(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Field("petName") familyName: String,
    ): Call<General>

    @Multipart
    @POST("api/v1/api/v1/member")
    fun postPet(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String,
        @Part("petName") petName: String,
        @Part("petType") petType: String,
        @Part("petSex") sex: String,
        @Part("petBirth") petBirth: String,
        @Part("petBreed") petBreed: String,
        @Part("petWeight") petWeight: Float,
        @Part("petNeutral") petNeutral: Boolean,
        @Part("petRegisterNum") petRegisterNum: String,
        @Part image: MultipartBody.Part?,
    ): Call<General>
}