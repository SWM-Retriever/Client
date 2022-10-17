package org.retriever.dailypet.data.repository.mypage

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.mypage.MyPageApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.mypage.PetDetailResponse
import javax.inject.Inject

class MyPageRepository @Inject constructor(private val myPageApiService: MyPageApiService) : BaseRepo() {

    suspend fun deleteMemberWithdrawal(jwt: String): Resource<ResponseBody> =
        safeApiCall { myPageApiService.deleteMemberWithdrawal(jwt) }

    suspend fun getPetList(familyId: Int, jwt: String): Resource<PetDetailResponse> =
        safeApiCall { myPageApiService.getPetList(familyId, jwt) }

}