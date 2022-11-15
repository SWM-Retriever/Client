package org.retriever.dailypet.data.network.home

import okhttp3.ResponseBody
import org.retriever.dailypet.model.main.*
import org.retriever.dailypet.model.signup.pet.PetList
import org.retriever.dailypet.model.statistics.ContributionResponse
import retrofit2.Response
import retrofit2.http.*

interface HomeApiService {
    @GET("api/v1/main/pets/{petId}/days")
    suspend fun getDay(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<PetDaysResponse>

    @GET("api/v1/pets/{petId}/cares")
    suspend fun getCareList(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<CareList>

    @GET("api/v1/families/{familyId}/pets")
    suspend fun getPetList(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<PetList>

    @POST("api/v1/pets/{petId}/care")
    suspend fun postPetCare(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body careInfo: CareInfo
    ): Response<ResponseBody>

    @DELETE("api/v1/pets/{petId}/cares/{careId}")
    suspend fun deletePetCare(
        @Path("petId") petId: Int,
        @Path("careId") careId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<ResponseBody>

    @PATCH("api/v1/pets/{petId}/cares/{careId}")
    suspend fun patchPetCare(
        @Path("petId") petId: Int,
        @Path("careId") careId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body careModifyInfo: CareModifyInfo
    ): Response<ResponseBody>

    @POST("api/v1/pets/{petId}/cares/{careId}/check")
    suspend fun postCareCheck(
        @Path("petId") petId: Int,
        @Path("careId") careId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<Care>

    @POST("api/v1/pets/{petId}/cares/{careId}/cancel")
    suspend fun postCareCancel(
        @Path("petId") petId: Int,
        @Path("careId") careId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<Care>

    @GET("api/v1/families/{familyId}/detail")
    suspend fun getGroupInfo(
        @Path("familyId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<GroupInfo>

    @GET("api/v1/main/pets/{petId}/contribution")
    suspend fun getMyContribution(
        @Path("petId") petId : Int,
        @Query("startDate") startDate : String,
        @Query("endDate") endDate : String,
        @Header("X-AUTH-TOKEN") jwt: String,
    ) : Response<MyContributionResponse>
}