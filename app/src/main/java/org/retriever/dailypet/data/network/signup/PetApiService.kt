package org.retriever.dailypet.data.network.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.model.signup.pet.*
import retrofit2.Response
import retrofit2.http.*

interface PetApiService {

    @POST("api/v1/validation/families/{familyId}/pet-name")
    suspend fun postCheckPetName(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body petName: String
    ): Response<ResponseBody>

    @GET("api/v1/pet/{petType}/kinds")
    suspend fun getPetBreedList(
        @Path("petType") petType: String,
        @Header("X-AUTH-TOKEN") jwt: String
    ): Response<BreedResponse>

    @POST("api/v1/families/{familyId}/pet")
    suspend fun postPet(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body petInfo: PetInfo,
    ): Response<PetResponse>
    // TODO Multipart 추가
//    @Multipart
//    @POST("api/v1/families/{familyId}/pet")
//    suspend fun postPet(
//        @Path("familyId") familyId: Int,
//        @Header("X-AUTH-TOKEN") jwt : String,
//        @Part("dto") petInfo: PetInfo,
//        @Part image: MultipartBody.Part?,
//    ): Response<PetResponse>

    @PATCH("api/v1/families/{familyId}/pets/{petId}")
    suspend fun modifyPet(
        @Path("familyId") familyId: Int,
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body modifyPetRequest: ModifyPetRequest,
    ): Response<ModifyPetResponse>

}