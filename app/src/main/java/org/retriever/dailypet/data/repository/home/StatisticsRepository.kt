package org.retriever.dailypet.data.repository.home

import org.retriever.dailypet.data.network.home.StatisticsApiService
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.statistics.ContributionResponse
import org.retriever.dailypet.model.statistics.DetailContributionResponse
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val statisticsApiService: StatisticsApiService) : BaseRepo() {

    suspend fun getContributionDetailList(familyId: Int, petId: Int, startDate: String, endDate: String, jwt: String): Resource<ContributionResponse> =
        safeApiCall { statisticsApiService.getContributionDetailList(familyId, petId, startDate, endDate, jwt) }

    suspend fun getGraphList(familyId: Int, petId: Int, startDate: String, endDate: String, jwt: String): Resource<DetailContributionResponse> =
        safeApiCall { statisticsApiService.getGraphList(familyId, petId, startDate, endDate, jwt) }

}