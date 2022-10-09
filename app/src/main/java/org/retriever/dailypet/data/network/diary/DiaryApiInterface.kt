package org.retriever.dailypet.data.network.diary

import org.retriever.dailypet.model.diary.DiaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DiaryApiInterface {

    @GET("api/v1/families/{familyId}/diaries")
    suspend fun getDiaryList(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<DiaryResponse>

}