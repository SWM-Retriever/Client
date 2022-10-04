package org.retriever.dailypet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.signup.PetRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.BreedResponse
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(private val petRepository: PetRepository) : ViewModel() {

    private val _petNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val petNameResponse: LiveData<Resource<ResponseBody>> = _petNameResponse

    private val _petBreedList = MutableLiveData<Resource<BreedResponse>>()
    val petBreedList: LiveData<Resource<BreedResponse>> = _petBreedList

    fun postCheckPetName(familyId: Int, jwt: String, petName: String) = viewModelScope.launch {
        _petNameResponse.postValue(Resource.Loading())

        _petNameResponse.postValue(petRepository.postCheckPetName(familyId, jwt, petName))
    }

    fun getPetBreedList(petType: String, jwt: String) = viewModelScope.launch {
        _petBreedList.postValue(Resource.Loading())

        _petBreedList.postValue(petRepository.getPetBreedList(petType, jwt))
    }

}