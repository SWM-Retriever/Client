package org.retriever.dailypet.data.network.signup

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.model.signup.pet.BreedResponse
import org.retriever.dailypet.model.signup.pet.PetInfo
import org.retriever.dailypet.model.signup.pet.PetResponse
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.model.signup.profile.RegisterProfileResponse
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
        @Path("petType") petType : String,
        @Header("X-AUTH-TOKEN") jwt : String
    ) : Response<BreedResponse>

    @POST("api/v1/families/{familyId}/pet")
    suspend fun postPet(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt : String,
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

}