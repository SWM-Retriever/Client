package org.retriever.dailypet.data.repository.signup

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.signup.PetApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.BreedResponse
import org.retriever.dailypet.model.signup.pet.PetInfo
import org.retriever.dailypet.model.signup.pet.PetResponse
import javax.inject.Inject

class PetRepository @Inject constructor(private val petApiService: PetApiService) : BaseRepo() {

    suspend fun postCheckPetName(familyId: Int, jwt: String, petName: String): Resource<ResponseBody> =
        safeApiCall { petApiService.postCheckPetName(familyId, jwt, petName) }

    suspend fun getPetBreedList(petType: String, jwt: String): Resource<BreedResponse> =
        safeApiCall { petApiService.getPetBreedList(petType, jwt) }

    suspend fun postPet(familyId: Int, jwt : String, petInfo: PetInfo, image: MultipartBody.Part?): Resource<PetResponse> =
        safeApiCall { petApiService.postPet(familyId, jwt, petInfo, image) }

}