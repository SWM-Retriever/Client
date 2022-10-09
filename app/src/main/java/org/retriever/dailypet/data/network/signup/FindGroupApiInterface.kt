package org.retriever.dailypet.data.network.signup

import org.retriever.dailypet.model.signup.family.FindGroupResponse
import retrofit2.Response
import retrofit2.http.*

interface FindGroupApiInterface {
    @GET("api/v1/families/{invitationCode}")
    suspend fun getFamilyInfo(
        @Path("invitationCode") invitationCode: String,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<FindGroupResponse>
}