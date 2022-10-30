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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val statisticsRepository: StatisticsRepository) : ViewModel() {

    private val _contributionResponse = MutableLiveData<Resource<ContributionResponse>>()
    val contributionResponse: LiveData<Resource<ContributionResponse>> = _contributionResponse

    private var startDate = ""
    private var endDate = ""

    fun getContributionDetailList(familyId: Int, petId: Int, jwt: String) {
        viewModelScope.launch {
            _contributionResponse.postValue(Resource.Loading())

            getDate()

            _contributionResponse.postValue(statisticsRepository.getContributionDetailList(familyId, petId, startDate, endDate, jwt))
        }
    }

    private fun getDate(){
        val cal = Calendar.getInstance()
        cal.time = Date()
        val df : DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

        startDate = df.format(cal.time)
        cal.add(Calendar.DATE, ONE_WEEKS_LATER)
        endDate = df.format(cal.time)
    }

    fun changeContributionResponse(list: List<ContributionItem>) {
        _contributionResponse.value = Resource.Success(ContributionResponse(list))
    }

    companion object{
        private const val ONE_WEEKS_LATER = -7
    }

}