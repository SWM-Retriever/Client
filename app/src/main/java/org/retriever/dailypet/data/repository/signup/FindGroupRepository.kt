package org.retriever.dailypet.data.repository.signup

import org.retriever.dailypet.data.network.signup.FindGroupApiInterface
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.family.FindGroupResponse
import javax.inject.Inject

class FindGroupRepository @Inject constructor(private val findGroupApiInterface: FindGroupApiInterface) : BaseRepo() {

    suspend fun getGroupInfo(invitationCode: String, jwt: String): Resource<FindGroupResponse> =
        safeApiCall { findGroupApiInterface.getFamilyInfo(invitationCode, jwt) }

}