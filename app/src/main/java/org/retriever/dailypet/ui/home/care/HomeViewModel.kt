package org.retriever.dailypet.ui.home.care

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.data.repository.home.HomeRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.*
import org.retriever.dailypet.model.signup.pet.PetList
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _getDaysResponse = MutableLiveData<Resource<PetDaysResponse>>()
    val getDaysResponse: LiveData<Resource<PetDaysResponse>> = _getDaysResponse

    private val _getCareListResponse = MutableLiveData<Resource<CareList>>()
    val getCareListResponse: LiveData<Resource<CareList>> = _getCareListResponse

    private val _getPetListResponse = MutableLiveData<Resource<PetList>>()
    val getPetListResponse: LiveData<Resource<PetList>> = _getPetListResponse

    private val _postCareResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val postCareResponse: LiveData<Event<Resource<ResponseBody>>> = _postCareResponse

    private val _deletePetCareResponse = MutableLiveData<Resource<ResponseBody>>()
    val deletePetCareResponse: LiveData<Resource<ResponseBody>> = _deletePetCareResponse

    private val _patchPetCareResponse = MutableLiveData<Resource<ResponseBody>>()
    val patchPetCareResponse: LiveData<Resource<ResponseBody>> = _patchPetCareResponse

    private val _postCareCheckResponse = MutableLiveData<Resource<Care>>()
    val postCareCheckResponse: LiveData<Resource<Care>> = _postCareCheckResponse

    private val _postCareCancelResponse = MutableLiveData<Resource<Care>>()
    val postCareCancelResponse: LiveData<Resource<Care>> = _postCareCancelResponse

    private val _getGroupInfoResponse = MutableLiveData<Resource<GroupInfo>>()
    val getGroupInfoResponse: LiveData<Resource<GroupInfo>> = _getGroupInfoResponse

    private val _getMyContributionResponse = MutableLiveData<Resource<MyContributionResponse>>()
    val getMyContributionResponse: LiveData<Resource<MyContributionResponse>> = _getMyContributionResponse

    private var startDate = ""
    private var endDate = ""

    fun getDays(petId: Int, jwt: String) = viewModelScope.launch {
        _getDaysResponse.postValue(Resource.Loading())

        _getDaysResponse.postValue(homeRepository.getDays(petId, jwt))
    }

    fun getCareList(petId: Int, jwt: String) = viewModelScope.launch {
        _getCareListResponse.postValue(Resource.Loading())

        _getCareListResponse.postValue(homeRepository.getCareList(petId, jwt))
    }

    fun getPetList(familyId: Int, jwt: String) = viewModelScope.launch {
        _getPetListResponse.postValue(Resource.Loading())

        _getPetListResponse.postValue(homeRepository.getPetList(familyId, jwt))
    }

    fun postCare(petId: Int, jwt: String, careInfo: CareInfo) = viewModelScope.launch {
        _postCareResponse.postValue(Event(Resource.Loading()))

        _postCareResponse.postValue(Event(homeRepository.postPetCare(petId, jwt, careInfo)))
    }

    fun deletePetCare(petId: Int, careId: Int, jwt: String) = viewModelScope.launch {
        _deletePetCareResponse.postValue(Resource.Loading())

        _deletePetCareResponse.postValue(homeRepository.deletePetCare(petId, careId, jwt))
    }

    fun patchPetCare(petId: Int, careId: Int, jwt: String, careModifyInfo: CareModifyInfo) = viewModelScope.launch {
        _patchPetCareResponse.postValue(Resource.Loading())

        _patchPetCareResponse.postValue(homeRepository.patchPetCare(petId, careId, jwt, careModifyInfo))
    }

    fun postCareCheck(petId: Int, careId: Int, jwt: String) = viewModelScope.launch {
        _postCareCheckResponse.postValue(Resource.Loading())

        _postCareCheckResponse.postValue(homeRepository.postCareCheck(petId, careId, jwt))
    }

    fun postCareCancel(petId: Int, careId: Int, jwt: String) = viewModelScope.launch {
        _postCareCancelResponse.postValue(Resource.Loading())

        _postCareCancelResponse.postValue(homeRepository.postCareCancel(petId, careId, jwt))
    }

    fun getGroupInfo(familyId: Int, jwt: String) = viewModelScope.launch {
        _getGroupInfoResponse.postValue(Resource.Loading())

        _getGroupInfoResponse.postValue(homeRepository.getGroupInfo(familyId, jwt))
    }

    fun getMyContribution(jwt: String) = viewModelScope.launch {
        _getMyContributionResponse.postValue(Resource.Loading())

        getDate()

        _getMyContributionResponse.postValue(homeRepository.getContribution(startDate, endDate, jwt))
    }

    private fun getDate() {
        val cal = Calendar.getInstance()
        cal.time = Date()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

        endDate = df.format(cal.time)
        cal.add(Calendar.DATE, ONE_WEEKS_LATER)
        startDate = df.format(cal.time)
    }

    companion object {
        private const val ONE_WEEKS_LATER = -7
    }
}