package org.retriever.dailypet.ui.home.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.retriever.dailypet.data.repository.home.StatisticsRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.statistics.ContributionItem
import org.retriever.dailypet.model.statistics.ContributionResponse
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val statisticsRepository: StatisticsRepository) : ViewModel() {

    private val _contributionResponse = MutableLiveData<Resource<ContributionResponse>>()
    val contributionResponse: LiveData<Resource<ContributionResponse>> = _contributionResponse

    fun getContributionDetailList(familyId: Int, petId: Int, startDate: String, endDate: String, jwt: String) {
        viewModelScope.launch {
            _contributionResponse.postValue(Resource.Loading())

            _contributionResponse.postValue(statisticsRepository.getContributionDetailList(familyId, petId, startDate, endDate, jwt))
        }
    }

    fun changeContributionResponse(list: List<ContributionItem>) {
        _contributionResponse.value = Resource.Success(ContributionResponse(list))
    }

}