package org.retriever.dailypet.data.repository.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.signup.FindGroupApiInterface
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.group.EnterGroupResponse
import org.retriever.dailypet.model.signup.group.FindGroupResponse
import javax.inject.Inject

class FindGroupRepository @Inject constructor(private val findGroupApiInterface: FindGroupApiInterface) : BaseRepo() {

    suspend fun getGroupInfo(invitationCode: String, jwt: String): Resource<FindGroupResponse> =
        safeApiCall { findGroupApiInterface.getFamilyInfo(invitationCode, jwt) }

    suspend fun postCheckGroupNickname(familyId : Int, jwt: String, familyRoleName: String): Resource<ResponseBody> =
        safeApiCall { findGroupApiInterface.postCheckGroupNickname(familyId, jwt, familyRoleName) }

    suspend fun postEnterGroup(familyId : Int, jwt: String, familyRoleName: String): Resource<EnterGroupResponse> =
        safeApiCall { findGroupApiInterface.postEnterGroup(familyId, jwt, familyRoleName) }
}