package org.retriever.dailypet.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.retriever.dailypet.data.repository.mypage.HomeRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.main.PetDaysResponse
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _getDaysResponse = MutableLiveData<Resource<PetDaysResponse>>()
    val getDaysResponse: LiveData<Resource<PetDaysResponse>> = _getDaysResponse

    fun getDays(petId: Int, jwt: String) = viewModelScope.launch {
        _getDaysResponse.postValue(Resource.Loading())

        _getDaysResponse.postValue(homeRepository.getDays(petId, jwt))
    }

}