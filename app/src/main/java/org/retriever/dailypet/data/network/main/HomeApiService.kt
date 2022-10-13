package org.retriever.dailypet.data.network.main

import okhttp3.ResponseBody
import org.retriever.dailypet.model.main.CareInfo
import org.retriever.dailypet.model.main.PetDaysResponse
import retrofit2.Response
import retrofit2.http.*

interface HomeApiService {
    @GET("api/v1/main/pets/{petId}/days")
    suspend fun getDay(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<PetDaysResponse>

    @POST("api/v1/pets/{petId}/care")
    suspend fun postPetCare(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body careInfo: CareInfo
    ): Response<ResponseBody>

}