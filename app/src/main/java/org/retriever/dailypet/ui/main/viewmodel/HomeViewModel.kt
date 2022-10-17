package org.retriever.dailypet.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.Event
import org.retriever.dailypet.data.repository.main.HomeRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.CareInfo
import org.retriever.dailypet.model.main.CareList
import org.retriever.dailypet.model.main.PetDaysResponse
import org.retriever.dailypet.model.signup.pet.PetList
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _getDaysResponse = MutableLiveData<Event<Resource<PetDaysResponse>>>()
    val getDaysResponse: LiveData<Event<Resource<PetDaysResponse>>> = _getDaysResponse

    private val _getCareListResponse = MutableLiveData<Event<Resource<CareList>>>()
    val getCareListResponse: LiveData<Event<Resource<CareList>>> = _getCareListResponse

    private val _getPetListResponse = MutableLiveData<Event<Resource<PetList>>>()
    val getPetListResponse: LiveData<Event<Resource<PetList>>> = _getPetListResponse

    private val _postCareResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val postCareResponse: LiveData<Event<Resource<ResponseBody>>> = _postCareResponse

    fun getDays(petId: Int, jwt: String) = viewModelScope.launch {
        _getDaysResponse.postValue(Event(Resource.Loading()))

        _getDaysResponse.postValue(Event(homeRepository.getDays(petId, jwt)))
    }

    fun getCareList(petId: Int, jwt: String) = viewModelScope.launch {
        _getCareListResponse.postValue(Event(Resource.Loading()))

        _getCareListResponse.postValue(Event(homeRepository.getCareList(petId, jwt)))
    }

    fun getPetList(familyId: Int, jwt: String) = viewModelScope.launch {
        _getPetListResponse.postValue(Event(Resource.Loading()))

        _getPetListResponse.postValue(Event(homeRepository.getPetList(familyId, jwt)))
    }

    fun postCare(petId: Int, jwt: String, careInfo: CareInfo) = viewModelScope.launch {
        _postCareResponse.postValue(Event(Resource.Loading()))

        _postCareResponse.postValue(Event(homeRepository.postPetCare(petId, jwt, careInfo)))
    }

}