package org.retriever.dailypet.data.network.login

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.model.login.*
import retrofit2.Response
import retrofit2.http.*

interface LoginApiService {

    @Headers("accept: Application/json", "Content-type: Application/json")
    @POST("api/v1/auth/login")
    suspend fun postIsMember(@Body member: Member): Response<LoginResponse>

    @GET("api/v1/progress-status")
    suspend fun getProgressStatus(@Header("X-AUTH-TOKEN") jwt : String) : Response<ProgressStatusResponse>






}