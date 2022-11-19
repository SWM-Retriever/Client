package org.retriever.dailypet.data.network.mypage

import okhttp3.ResponseBody
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.model.mypage.GroupResponse
import org.retriever.dailypet.model.mypage.PetDetailResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
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

    @DELETE("api/v1/families/{familyId}/pets/{petId}")
    suspend fun deletePet(
        @Header("X-AUTH-TOKEN") jwt: String,
        @Path("familyId") familyId: Int,
        @Path("petId") petId: Int,
    ): Response<ResponseBody>

    @GET("api/v1/families/{familyId}/detail")
    suspend fun getGroupInfo(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<GroupResponse>

    @GET("api/v1/families/{familyId}/diaries/recent")
    suspend fun getRecentDiary(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<DiaryItem>

    @PATCH("api/v1/families/{familyId}/group-leader/{memberId}")
    suspend fun patchLeader(
        @Path("familyId") familyId: Int,
        @Path("memberId") memberId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<ResponseBody>

}