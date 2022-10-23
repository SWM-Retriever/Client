package org.retriever.dailypet.ui.signup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.Event
import org.retriever.dailypet.data.repository.signup.PetRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.*
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(private val petRepository: PetRepository) : ViewModel() {

    private val _petNameResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val petNameResponse: LiveData<Event<Resource<ResponseBody>>> = _petNameResponse

    private val _petBreedList = MutableLiveData<Resource<BreedResponse>>()
    val petBreedList: LiveData<Resource<BreedResponse>> = _petBreedList

    private val _petResponse = MutableLiveData<Event<Resource<PetResponse>>>()
    val petResponse: LiveData<Event<Resource<PetResponse>>> = _petResponse

    private val _modifyPetResponse = MutableLiveData<Resource<ModifyPetResponse>>()
    val modifyPetResponse: LiveData<Resource<ModifyPetResponse>> = _modifyPetResponse

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
            _modifyPetResponse.postValue(Resource.Loading())

            _modifyPetResponse.postValue(petRepository.modifyPet(familyId, petId, jwt, modifyPetRequest))
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
        Log.d("ABC", isValidPetName.toString())
        Log.d("ABC", type.toString())
        Log.d("ABC", sex.toString())
        Log.d("ABC", birth.toString())
        Log.d("ABC", weight.toString())
        Log.d("ABC", "")
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