package org.retriever.dailypet.data.network.presignedurl

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.model.presignedurl.PreSignedUrlResponse
import retrofit2.Response
import retrofit2.http.*

interface PreSignedUrlApiService {

    @GET("/api/v1/presigned-url/{S3Path}/{fileName}")
    suspend fun getPreSignedUrl(
        @Path("S3Path") S3Path: String,
        @Path("fileName") fileName: String,
    ): Response<PreSignedUrlResponse>

    @Multipart
    @PUT
    suspend fun putImageUrl(
        @Url url: String,
        @Part image: MultipartBody.Part?
    ): Response<ResponseBody>

}