package org.retriever.dailypet.data.repository.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.signup.GroupApiInterface
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.group.GroupInfo
import org.retriever.dailypet.model.signup.group.GroupResponse
import org.retriever.dailypet.model.signup.group.ModifyGroupResponse
import javax.inject.Inject

class GroupRepository @Inject constructor(private val groupApiInterface: GroupApiInterface) : BaseRepo() {

    suspend fun postCheckFamilyName(jwt: String, familyName: String): Resource<ResponseBody> =
        safeApiCall { groupApiInterface.postCheckFamilyName(jwt, familyName) }

    suspend fun postFamily(jwt: String, groupInfo: GroupInfo): Resource<GroupResponse> =
        safeApiCall { groupApiInterface.postFamily(jwt, groupInfo) }

    suspend fun modifyGroup(familyId: Int, jwt: String, groupInfo: GroupInfo): Resource<ModifyGroupResponse> =
        safeApiCall { groupApiInterface.modifyGroup(familyId, jwt, groupInfo) }

    suspend fun makeAlone(jwt: String): Resource<GroupResponse> =
        safeApiCall { groupApiInterface.makeAlone(jwt) }

}