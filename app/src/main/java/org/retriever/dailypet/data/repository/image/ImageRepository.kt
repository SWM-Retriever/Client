package org.retriever.dailypet.data.repository.image

import okhttp3.MultipartBody
import org.retriever.dailypet.data.network.image.ImageApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.image.ImageResponse
import javax.inject.Inject

class ImageRepository @Inject constructor(private val imageApiService: ImageApiService) : BaseRepo() {

    suspend fun postImage(s3path: String, file: MultipartBody.Part): Resource<ImageResponse> =
        safeApiCall { imageApiService.postImage(s3path, file) }

}