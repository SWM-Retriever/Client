package org.retriever.dailypet.test.data.repository.login

import org.retriever.dailypet.test.data.network.LoginApiService
import org.retriever.dailypet.test.data.repository.BaseRepo
import org.retriever.dailypet.test.model.Resource
import org.retriever.dailypet.test.model.login.LoginResponse
import org.retriever.dailypet.test.model.login.Member
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginApiService: LoginApiService) : BaseRepo() {

    suspend fun postIsMember(member: Member) : Resource<LoginResponse> = safeApiCall { loginApiService.postIsMember(member) }

}