package org.retriever.dailypet.data.repository.login

import org.retriever.dailypet.data.network.login.LoginApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.login.LoginResponse
import org.retriever.dailypet.model.login.Member
import org.retriever.dailypet.model.login.ProgressStatusResponse
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginApiService: LoginApiService) : BaseRepo() {

    suspend fun postIsMember(member: Member): Resource<LoginResponse> =
        safeApiCall { loginApiService.postIsMember(member) }

    suspend fun getProgressStatus(jwt: String): Resource<ProgressStatusResponse> =
        safeApiCall { loginApiService.getProgressStatus(jwt) }

}