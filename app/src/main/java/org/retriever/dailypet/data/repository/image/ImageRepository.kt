package org.retriever.dailypet.data.repository.image

import okhttp3.MultipartBody
import org.retriever.dailypet.data.network.image.ImageApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import javax.inject.Inject

class ImageRepository @Inject constructor(private val preSignedUrlApiService: ImageApiService) : BaseRepo() {

    suspend fun postImage(s3path: String, file: MultipartBody.Part): Resource<String> =
        safeApiCall { preSignedUrlApiService.postImage(s3path, file) }

}