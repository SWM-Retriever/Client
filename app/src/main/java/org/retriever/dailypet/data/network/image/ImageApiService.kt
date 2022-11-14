package org.retriever.dailypet.data.network.image

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ImageApiService {

    @Multipart
    @POST("/api/v1/{S3Path}/image")
    suspend fun postImage(
        @Path("S3Path") S3Path: String,
        @Part file: MultipartBody.Part
    ): Response<String>

//    @Multipart
//    @PUT
//    suspend fun putImageUrl(
//        @Header("Content-Type") contentType: String,
//        @Url url: String,
//        @Part file: MultipartBody.Part
//    ): Response<ResponseBody>

}