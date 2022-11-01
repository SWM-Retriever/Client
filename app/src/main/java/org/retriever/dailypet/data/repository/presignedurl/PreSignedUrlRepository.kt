package org.retriever.dailypet.data.repository.presignedurl

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.presignedurl.PreSignedUrlApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.presignedurl.PreSignedUrlResponse
import javax.inject.Inject

class PreSignedUrlRepository @Inject constructor(private val preSignedUrlApiService: PreSignedUrlApiService) : BaseRepo() {

    suspend fun getPreSignedUrl(s3path: String, fileName: String): Resource<PreSignedUrlResponse> =
        safeApiCall { preSignedUrlApiService.getPreSignedUrl(s3path, fileName) }

    suspend fun putImageUrl(url: String, image: MultipartBody.Part): Resource<ResponseBody> =
        safeApiCall { preSignedUrlApiService.putImageUrl(url, image) }

}