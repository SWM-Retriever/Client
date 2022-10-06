package org.retriever.dailypet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.signup.PetRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.BreedResponse
import org.retriever.dailypet.model.signup.pet.PetInfo
import org.retriever.dailypet.model.signup.pet.PetResponse
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(private val petRepository: PetRepository) : ViewModel() {

    private val _petNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val petNameResponse: LiveData<Resource<ResponseBody>> = _petNameResponse

    private val _petBreedList = MutableLiveData<Resource<BreedResponse>>()
    val petBreedList: LiveData<Resource<BreedResponse>> = _petBreedList

    private val _petResponse = MutableLiveData<Resource<PetResponse>>()
    val petResponse: LiveData<Resource<PetResponse>> = _petResponse

    fun postCheckPetName(familyId: Int, jwt: String, petName: String) = viewModelScope.launch {
        _petNameResponse.postValue(Resource.Loading())

        _petNameResponse.postValue(petRepository.postCheckPetName(familyId, jwt, petName))
    }

    fun getPetBreedList(petType: String, jwt: String) = viewModelScope.launch {
        _petBreedList.postValue(Resource.Loading())

        _petBreedList.postValue(petRepository.getPetBreedList(petType, jwt))
    }

    fun postPet(familyId: Int, jwt: String, petInfo: PetInfo, image: MultipartBody.Part?) = viewModelScope.launch {
        _petResponse.postValue(Resource.Loading())

        _petResponse.postValue(petRepository.postPet(familyId, jwt, petInfo, image))
    }

}