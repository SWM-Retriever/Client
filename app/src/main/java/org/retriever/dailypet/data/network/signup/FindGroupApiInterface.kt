package org.retriever.dailypet.data.network.signup

import okhttp3.ResponseBody
import org.retriever.dailypet.model.signup.family.EnterFamilyResponse
import org.retriever.dailypet.model.signup.family.FamilyInfo
import org.retriever.dailypet.model.signup.family.FindGroupResponse
import retrofit2.Response
import retrofit2.http.*

interface FindGroupApiInterface {
    @GET("api/v1/families/{invitationCode}")
    suspend fun getFamilyInfo(
        @Path("invitationCode") invitationCode: String,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<FindGroupResponse>

    @POST("api/v1/validation/families/{familyId}/family-role-name")
    suspend fun postCheckGroupNickname(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body familyRoleName: String,
    ): Response<ResponseBody>

    @POST("api/v1/families/{familyId}")
    suspend fun postEnterGroup(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body familyRoleName: String,
    ): Response<EnterFamilyResponse>
}