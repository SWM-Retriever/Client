package org.retriever.dailypet.data.repository.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.signup.PetApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.BreedResponse
import javax.inject.Inject

class PetRepository @Inject constructor(private val petApiService: PetApiService) : BaseRepo() {

    suspend fun postCheckPetName(familyId: Int, jwt: String, petName: String): Resource<ResponseBody> =
        safeApiCall { petApiService.postCheckPetName(familyId, jwt, petName) }

    suspend fun getPetBreedList(petType: String, jwt: String): Resource<BreedResponse> =
        safeApiCall { petApiService.getPetBreedList(petType, jwt) }

}