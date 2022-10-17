package org.retriever.dailypet.data.network.mypage

import okhttp3.ResponseBody
import org.retriever.dailypet.model.mypage.PetDetailResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MyPageApiService {

    @DELETE("api/v1/auth/member")
    suspend fun deleteMemberWithdrawal(
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<ResponseBody>

    @GET("api/v1/families/{familyId}/pets")
    suspend fun getPetList(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<PetDetailResponse>

}