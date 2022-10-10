package org.retriever.dailypet.data.network.diary

import okhttp3.ResponseBody
import org.retriever.dailypet.model.diary.DiaryPost
import org.retriever.dailypet.model.diary.DiaryResponse
import retrofit2.Response
import retrofit2.http.*

interface DiaryApiInterface {

    @GET("api/v1/families/{familyId}/diaries")
    suspend fun getDiaryList(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<DiaryResponse>

    @POST("api/v1/families/{familyId}/diary")
    suspend fun postDiary(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body diaryPost: DiaryPost
    ): Response<ResponseBody>

    @DELETE("api/v1/families/{familyId}/diaries/{diaryId}")
    suspend fun deleteDiary(
        @Path("familyId") familyId: Int,
        @Path("diaryId") diaryId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<ResponseBody>

    @PATCH("api/v1/families/{familyId}/diaries/{diaryId}")
    suspend fun updateDiary(
        @Path("familyId") familyId: Int,
        @Path("diaryId") diaryId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body diaryPost: DiaryPost
    ): Response<ResponseBody>

}