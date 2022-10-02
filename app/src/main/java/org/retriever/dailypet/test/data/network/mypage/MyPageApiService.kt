package org.retriever.dailypet.test.data.network.mypage

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header

interface MyPageApiService {

    @DELETE("api/v1/auth/member")
    suspend fun deleteMemberWithdrawal(
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<ResponseBody>

}