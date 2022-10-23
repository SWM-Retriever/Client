package org.retriever.dailypet.data.repository.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.signup.FamilyApiInterface
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.family.FamilyInfo
import org.retriever.dailypet.model.signup.family.FamilyResponse
import org.retriever.dailypet.model.signup.family.ModifyFamilyResponse
import javax.inject.Inject

class FamilyRepository @Inject constructor(private val familyApiInterface: FamilyApiInterface) : BaseRepo() {

    suspend fun postCheckFamilyName(jwt: String, familyName: String): Resource<ResponseBody> =
        safeApiCall { familyApiInterface.postCheckFamilyName(jwt, familyName) }

    suspend fun postFamily(jwt: String, familyInfo: FamilyInfo): Resource<FamilyResponse> =
        safeApiCall { familyApiInterface.postFamily(jwt, familyInfo) }

    suspend fun modifyGroup(familyId: Int, jwt: String, familyInfo: FamilyInfo): Resource<ModifyFamilyResponse> =
        safeApiCall { familyApiInterface.modifyGroup(familyId, jwt, familyInfo) }

    suspend fun makeAlone(jwt: String): Resource<FamilyResponse> =
        safeApiCall { familyApiInterface.makeAlone(jwt) }

}