package org.retriever.dailypet.data.network.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.model.signup.group.GroupInfo
import org.retriever.dailypet.model.signup.group.GroupResponse
import org.retriever.dailypet.model.signup.group.ModifyGroupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupApiInterface {

    @POST("api/v1/validation/family-name")
    suspend fun postCheckFamilyName(
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body familyName: String,
    ): Response<ResponseBody>

    @POST("api/v1/family")
    suspend fun postFamily(
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body groupInfo: GroupInfo,
    ): Response<GroupResponse>

    @POST("api/v1/families/{familyId}/type/group")
    suspend fun modifyGroup(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body groupInfo: GroupInfo,
    ): Response<ModifyGroupResponse>

    @POST("api/v1/family/alone")
    suspend fun makeAlone(
        @Header("X-AUTH-TOKEN") jwt: String
    ): Response<GroupResponse>
}