package org.retriever.dailypet.data.repository.home

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.home.HomeApiService
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

    suspend fun postCareCheck(petId: Int, careId: Int, jwt: String): Resource<Care> =
        safeApiCall { homeApiService.postCareCheck(petId, careId, jwt) }

    suspend fun postCareCancel(petId: Int, careId: Int, jwt: String): Resource<Care> =
        safeApiCall { homeApiService.postCareCancel(petId, careId, jwt) }

    suspend fun getGroupInfo(familyId: Int, jwt: String): Resource<GroupInfo> =
        safeApiCall { homeApiService.getGroupInfo(familyId, jwt) }

    suspend fun getContribution(startDate: String, endDate: String, jwt: String): Resource<MyContributionResponse> =
        safeApiCall { homeApiService.getMyContribution(startDate, endDate, jwt) }
}