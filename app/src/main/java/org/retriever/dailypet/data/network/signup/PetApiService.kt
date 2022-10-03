package org.retriever.dailypet.data.network.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.model.signup.pet.BreedResponse
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
    suspend fun getPetBreed(
        @Path("petType") petType : String,
        @Header("X-AuUTH-TOKEN") jwt : String
    ) : Response<BreedResponse>

}