package org.retriever.dailypet.data.repository.main

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.main.HomeApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.*
import org.retriever.dailypet.model.signup.pet.PetList
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeApiService: HomeApiService) : BaseRepo() {

    suspend fun getDays(petId: Int, jwt: String): Resource<PetDaysResponse> =
        safeApiCall { homeApiService.getDay(petId, jwt) }

    suspend fun getCareList(petId: Int, jwt: String): Resource<CareList> =
        safeApiCall { homeApiService.getCareList(petId, jwt) }

    suspend fun getPetList(familyId: Int, jwt: String): Resource<PetList> =
        safeApiCall { homeApiService.getPetList(familyId, jwt) }

    suspend fun postPetCare(petId: Int, jwt: String, careInfo: CareInfo): Resource<ResponseBody> =
        safeApiCall { homeApiService.postPetCare(petId, jwt, careInfo) }

    suspend fun deletePetCare(petId: Int, careId: Int, jwt: String): Resource<ResponseBody> =
        safeApiCall { homeApiService.deletePetCare(petId, careId, jwt) }

    suspend fun patchPetCare(petId: Int, careId: Int, jwt: String, careModifyInfo: CareModifyInfo): Resource<ResponseBody> =
        safeApiCall { homeApiService.patchPetCare(petId, careId, jwt, careModifyInfo) }

    suspend fun postCareCheck(petId: Int, careId: Int, jwt: String): Resource<ResponseBody> =
        safeApiCall { homeApiService.postCareCheck(petId, careId, jwt) }

    suspend fun postCareCancel(petId: Int, careId: Int, jwt: String): Resource<ResponseBody> =
        safeApiCall { homeApiService.postCareCancel(petId, careId, jwt) }
}