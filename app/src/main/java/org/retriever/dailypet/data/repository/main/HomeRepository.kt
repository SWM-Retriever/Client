package org.retriever.dailypet.data.repository.main

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.main.HomeApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.CareInfo
import org.retriever.dailypet.model.main.CareList
import org.retriever.dailypet.model.main.PetDaysResponse
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeApiService: HomeApiService) : BaseRepo() {

    suspend fun getDays(petId: Int, jwt: String): Resource<PetDaysResponse> =
        safeApiCall { homeApiService.getDay(petId, jwt) }

    suspend fun getCareList(petId: Int, jwt: String): Resource<CareList> =
        safeApiCall { homeApiService.getCareList(petId, jwt) }

    suspend fun postPetCare(petId: Int, jwt: String, careInfo: CareInfo): Resource<ResponseBody> =
        safeApiCall { homeApiService.postPetCare(petId, jwt, careInfo) }
}