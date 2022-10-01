package org.retriever.dailypet.test.data.network

import org.retriever.dailypet.test.model.login.LoginResponse
import org.retriever.dailypet.test.model.login.Member
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginApiService {

    @Headers("accept: Application/json", "Content-type: Application/json")
    @POST("api/v1/auth/login")
    suspend fun postIsMember(@Body member: Member): Response<LoginResponse>

}