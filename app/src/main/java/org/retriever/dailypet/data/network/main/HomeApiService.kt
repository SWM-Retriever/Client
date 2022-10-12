package org.retriever.dailypet.data.network.main

import org.retriever.dailypet.model.main.PetDaysResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface HomeApiService {
    @GET("api/v1/main/pets/{petId}/days")
    suspend fun getDay(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<PetDaysResponse>

}