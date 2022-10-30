package org.retriever.dailypet.ui.home.care

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.data.repository.main.HomeRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.*
import org.retriever.dailypet.model.signup.pet.PetList
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _getDaysResponse = MutableLiveData<Event<Resource<PetDaysResponse>>>()
    val getDaysResponse: LiveData<Event<Resource<PetDaysResponse>>> = _getDaysResponse

    private val _getCareListResponse = MutableLiveData<Resource<CareList>>()
    val getCareListResponse: LiveData<Resource<CareList>> = _getCareListResponse

    private val _getPetListResponse = MutableLiveData<Event<Resource<PetList>>>()
    val getPetListResponse: LiveData<Event<Resource<PetList>>> = _getPetListResponse

    private val _postCareResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val postCareResponse: LiveData<Event<Resource<ResponseBody>>> = _postCareResponse

    private val _deletePetCareResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val deletePetCareResponse: LiveData<Event<Resource<ResponseBody>>> = _deletePetCareResponse

    private val _patchPetCareResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val patchPetCareResponse: LiveData<Event<Resource<ResponseBody>>> = _patchPetCareResponse

    private val _postCareCheckResponse = MutableLiveData<Resource<Care>>()
    val postCareCheckResponse: LiveData<Resource<Care>> = _postCareCheckResponse

    private val _postCareCancelResponse = MutableLiveData<Resource<Care>>()
    val postCareCancelResponse: LiveData<Resource<Care>> = _postCareCancelResponse

    private val _getGroupInfoResponse = MutableLiveData<Resource<GroupInfo>>()
    val getGroupInfoResponse: LiveData<Resource<GroupInfo>> = _getGroupInfoResponse

    fun getDays(petId: Int, jwt: String) = viewModelScope.launch {
        _getDaysResponse.postValue(Event(Resource.Loading()))

        _getDaysResponse.postValue(Event(homeRepository.getDays(petId, jwt)))
    }

    fun getCareList(petId: Int, jwt: String) = viewModelScope.launch {
        _getCareListResponse.postValue(Resource.Loading())

        _getCareListResponse.postValue(homeRepository.getCareList(petId, jwt))
    }

    fun getPetList(familyId: Int, jwt: String) = viewModelScope.launch {
        _getPetListResponse.postValue(Event(Resource.Loading()))

        _getPetListResponse.postValue(Event(homeRepository.getPetList(familyId, jwt)))
    }

    fun postCare(petId: Int, jwt: String, careInfo: CareInfo) = viewModelScope.launch {
        _postCareResponse.postValue(Event(Resource.Loading()))

        _postCareResponse.postValue(Event(homeRepository.postPetCare(petId, jwt, careInfo)))
    }

    fun deletePetCare(petId: Int, careId: Int, jwt: String) = viewModelScope.launch {
        _deletePetCareResponse.postValue(Event(Resource.Loading()))

        _deletePetCareResponse.postValue(Event(homeRepository.deletePetCare(petId, careId, jwt)))
    }

    fun patchPetCare(petId: Int, careId: Int, jwt: String, careModifyInfo: CareModifyInfo) = viewModelScope.launch {
        _patchPetCareResponse.postValue(Event(Resource.Loading()))

        _patchPetCareResponse.postValue(Event(homeRepository.patchPetCare(petId, careId, jwt, careModifyInfo)))
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
}