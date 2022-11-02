package org.retriever.dailypet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.presignedurl.PreSignedUrlRepository
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.data.repository.signup.PetRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.presignedurl.PreSignedUrlResponse
import org.retriever.dailypet.model.signup.pet.*
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val preSignedUrlRepository: PreSignedUrlRepository,
) : ViewModel() {

    private val _petNameResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val petNameResponse: LiveData<Event<Resource<ResponseBody>>> = _petNameResponse

    private val _petBreedList = MutableLiveData<Resource<BreedResponse>>()
    val petBreedList: LiveData<Resource<BreedResponse>> = _petBreedList

    private val _petResponse = MutableLiveData<Event<Resource<PetResponse>>>()
    val petResponse: LiveData<Event<Resource<PetResponse>>> = _petResponse

    private val _modifyPetResponse = MutableLiveData<Event<Resource<ModifyPetResponse>>>()
    val modifyPetResponse: LiveData<Event<Resource<ModifyPetResponse>>> = _modifyPetResponse

    private val _getPetListResponse = MutableLiveData<Event<Resource<PetList>>>()
    val getPetListResponse: LiveData<Event<Resource<PetList>>> = _getPetListResponse

    private val _preSignedUrlResponse = MutableLiveData<Resource<PreSignedUrlResponse>>()
    val preSignedUrlResponse: LiveData<Resource<PreSignedUrlResponse>> = _preSignedUrlResponse

    private val _putImageUrlResponse = MutableLiveData<Resource<ResponseBody>>()
    val putImageUrlResponse: LiveData<Resource<ResponseBody>> = _putImageUrlResponse

    private var _submit = MutableLiveData(false)
    val submit: LiveData<Boolean> = _submit

    private var isValidPetName = false
    private var dog = false
    private var cat = false
    private var male = false
    private var female = false
    private var birth = false
    private var weight = false

    fun postCheckPetName(familyId: Int, jwt: String, petName: String) = viewModelScope.launch {
        _petNameResponse.postValue(Event(Resource.Loading()))

        _petNameResponse.postValue(Event(petRepository.postCheckPetName(familyId, jwt, petName)))
    }

    fun getPetBreedList(petType: String, jwt: String) = viewModelScope.launch {
        _petBreedList.postValue(Resource.Loading())

        _petBreedList.postValue(petRepository.getPetBreedList(petType, jwt))
    }

    fun postPet(familyId: Int, jwt: String, petInfo: PetInfo) = viewModelScope.launch {
        _petResponse.postValue(Event(Resource.Loading()))

        _petResponse.postValue(Event(petRepository.postPet(familyId, jwt, petInfo)))
    }

    fun modifyPet(familyId: Int, petId: Int, jwt: String, modifyPetRequest: ModifyPetRequest) {
        viewModelScope.launch {
            _modifyPetResponse.postValue(Event(Resource.Loading()))

            _modifyPetResponse.postValue(Event(petRepository.modifyPet(familyId, petId, jwt, modifyPetRequest)))
        }
    }

    fun getPetList(familyId: Int, jwt: String) = viewModelScope.launch {
        _getPetListResponse.postValue(Event(Resource.Loading()))

        _getPetListResponse.postValue(Event(petRepository.getPetList(familyId, jwt)))
    }

    fun getPreSignedUrl(s3Path: String, fileName: String) {
        viewModelScope.launch {
            _preSignedUrlResponse.postValue(Resource.Loading())

            _preSignedUrlResponse.postValue(preSignedUrlRepository.getPreSignedUrl(s3Path, fileName))
        }
    }

    fun putImageUrl(url: String, image: MultipartBody.Part) {
        viewModelScope.launch {
            _putImageUrlResponse.postValue(Resource.Loading())

            _putImageUrlResponse.postValue(preSignedUrlRepository.putImageUrl(url, image))
        }
    }

    fun setInitial() {
        isValidPetName = false
        dog = false
        cat = false
        male = false
        female = false
        birth = false
        weight = false
        submitCheck()
    }

    fun setValidPetName(boolean: Boolean) {
        isValidPetName = boolean
        submitCheck()
    }

    fun setDogTrue() {
        dog = true
        cat = false
        submitCheck()
    }

    fun setCatTrue() {
        dog = false
        cat = true
        submitCheck()
    }

    fun setMaleTrue() {
        male = true
        female = false
        submitCheck()
    }

    fun setFemaleTrue() {
        male = false
        female = true
        submitCheck()
    }

    fun setBirth() {
        birth = true
        submitCheck()
    }

    fun setWeight(boolean: Boolean) {
        weight = boolean
        submitCheck()
    }

    fun getPetTypeSelected(): Boolean =
        dog || cat

    fun getPetType(): String =
        if (dog) DOG else CAT

    fun getSexType(): String =
        if (male) MALE else FEMALE

    private fun submitCheck() {
        val type = dog || cat
        val sex = male || female

        _submit.value = isValidPetName
                && type
                && sex
                && birth
                && weight
    }

    companion object {
        const val DOG = "DOG"
        private const val CAT = "CAT"
        private const val MALE = "MALE"
        private const val FEMALE = "FEMALE"
    }

}