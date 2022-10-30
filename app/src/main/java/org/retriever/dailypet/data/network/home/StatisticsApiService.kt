package org.retriever.dailypet.data.network.home

import org.retriever.dailypet.model.statistics.ContributionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface StatisticsApiService {

    @GET("api/v1/families/{familyId}/pets/{petId}/contributions/detail")
    suspend fun getContributionDetailList(
        @Path("familyId") familyId : Int,
        @Path("petId") petId : Int,
        @Query("startDate") startDate : String,
        @Query("endDate") endDate : String,
        @Header("X-AUTH-TOKEN") jwt: String,
    ) : Response<ContributionResponse>

}