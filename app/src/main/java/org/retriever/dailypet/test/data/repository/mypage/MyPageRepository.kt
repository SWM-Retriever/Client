package org.retriever.dailypet.test.data.repository.mypage

import okhttp3.ResponseBody
import org.retriever.dailypet.test.data.network.MyPageApiService
import org.retriever.dailypet.test.data.repository.BaseRepo
import org.retriever.dailypet.test.model.Resource
import javax.inject.Inject

class MyPageRepository @Inject constructor(private val myPageApiService: MyPageApiService) : BaseRepo() {

    suspend fun deleteMemberWithdrawal(jwt: String): Resource<ResponseBody> =
        safeApiCall { myPageApiService.deleteMemberWithdrawal(jwt) }

}