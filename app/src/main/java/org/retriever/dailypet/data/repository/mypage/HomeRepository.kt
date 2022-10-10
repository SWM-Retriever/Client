package org.retriever.dailypet.data.repository.mypage

import org.retriever.dailypet.data.network.main.HomeApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.PetDaysResponse
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeApiService: HomeApiService) : BaseRepo(){

    suspend fun getDays(petId: Int, jwt: String): Resource<PetDaysResponse> =
        safeApiCall { homeApiService.getDay(petId, jwt) }

}