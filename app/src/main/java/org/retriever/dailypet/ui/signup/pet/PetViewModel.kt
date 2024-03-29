package org.retriever.dailypet.ui.signup.pet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.image.ImageRepository
import org.retriever.dailypet.data.repository.signup.PetRepository
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.image.ImageResponse
import org.retriever.dailypet.model.signup.pet.*
import org.retriever.dailypet.ui.signup.EditTextState
import org.retriever.dailypet.ui.signup.EditTextValidateState
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val imageRepository: ImageRepository,
    ) : ViewModel() {

    private val _petNameState = MutableStateFlow(EditTextValidateState.DEFAULT_STATE)
    val petNameState: StateFlow<EditTextValidateState> = _petNameState

    // DEFAULT -> 선택안된상태 INVALID -> 강아지 선택 VALID -> 고양이 선택
    private val _petTypeState = MutableStateFlow(EditTextState.DEFAULT_STATE)
    val petTypeState: StateFlow<EditTextState> = _petTypeState

    // DEFAULT -> 선택안된상태 INVALID -> 수컷 선택 VALID -> 암컷 선택
    private val _petSexState = MutableStateFlow(EditTextState.DEFAULT_STATE)
    val petSexState: StateFlow<EditTextState> = _petSexState

    private val _birthState = MutableStateFlow(EditTextState.DEFAULT_STATE)
    val birthState: StateFlow<EditTextState> = _birthState

    private val _breedState = MutableStateFlow(EditTextState.DEFAULT_STATE)
    val breedState: StateFlow<EditTextState> = _breedState

    private val _notKnowState = MutableStateFlow(false)
    val notKnowState: StateFlow<Boolean> = _notKnowState

    private val _weightState = MutableStateFlow(EditTextState.DEFAULT_STATE)
    val weightState: StateFlow<EditTextState> = _weightState

    private val _registerButtonState = MutableStateFlow(false)
    val registerButtonState: StateFlow<Boolean> = _registerButtonState

    val petInfo = PetInfo("", "", "", "", -1, -1f, false, "", "")
    var petBreed = ""

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

    private val _postImageResponse = MutableLiveData<Resource<ImageResponse>>()
    val postImageResponse: LiveData<Resource<ImageResponse>> = _postImageResponse

    fun setPetNameState(state: EditTextValidateState) {
        _petNameState.value = state
        setRegisterButtonState()
    }

    fun setPetTypeState(state: EditTextState) {
        if (state == EditTextState.INVALID_STATE) {
            petInfo.petType = DOG
        } else if (state == EditTextState.VALID_STATE) {
            petInfo.petType = CAT
        }

        _petTypeState.value = state
        setRegisterButtonState()
    }

    fun setPetSexState(state: EditTextState) {
        if (state == EditTextState.INVALID_STATE) {
            petInfo.gender = MALE
        } else {
            petInfo.gender = FEMALE
        }

        _petSexState.value = state
        setRegisterButtonState()
    }

    fun setBirthState(state: EditTextState) {
        _birthState.value = state
        setRegisterButtonState()
    }

    fun setBreedState(state: EditTextState) {
        _breedState.value = state
        setRegisterButtonState()
    }

    fun setNotKnowState(check: Boolean) {
        _notKnowState.value = check
        if (check) {
            setBreedState(EditTextState.VALID_STATE)
        } else {
            setBreedState(EditTextState.DEFAULT_STATE)
        }
        setRegisterButtonState()
    }

    fun setWeightState(state: EditTextState) {
        _weightState.value = state
        setRegisterButtonState()
    }

    private fun setRegisterButtonState() {
        _registerButtonState.value =
            (_petNameState.value == EditTextValidateState.VALID_STATE) &&
                    (_petTypeState.value != EditTextState.DEFAULT_STATE) &&
                    (_petSexState.value != EditTextState.DEFAULT_STATE) &&
                    (_birthState.value == EditTextState.VALID_STATE) &&
                    (_breedState.value == EditTextState.VALID_STATE) &&
                    (_weightState.value == EditTextState.VALID_STATE)
    }

    fun getPetTypeSelected() =
        _petTypeState.value != EditTextState.DEFAULT_STATE


    fun getPetType() =
        if (_petTypeState.value == EditTextState.INVALID_STATE) DOG else if (_petTypeState.value == EditTextState.VALID_STATE) CAT else ""

    fun postCheckPetName(familyId: Int, jwt: String, petName: String) = viewModelScope.launch {
        _petNameResponse.postValue(Event(Resource.Loading()))

        _petNameResponse.postValue(Event(petRepository.postCheckPetName(familyId, jwt, petName)))
    }

    fun getPetBreedList(petType: String, jwt: String) = viewModelScope.launch {
        _petBreedList.postValue(Resource.Loading())

        _petBreedList.postValue(petRepository.getPetBreedList(petType, jwt))
    }

    fun postPet(familyId: Int, jwt: String) = viewModelScope.launch {
        _petResponse.postValue(Event(Resource.Loading()))

        _petResponse.postValue(Event(petRepository.postPet(familyId, jwt, petInfo)))
    }

    fun getPetList(familyId: Int, jwt: String) = viewModelScope.launch {
        _getPetListResponse.postValue(Event(Resource.Loading()))

        _getPetListResponse.postValue(Event(petRepository.getPetList(familyId, jwt)))
    }

    fun postImage(s3Path: String, file: MultipartBody.Part) = viewModelScope.launch {
        _postImageResponse.postValue(Resource.Loading())

        _postImageResponse.postValue(imageRepository.postImage(s3Path, file))
    }

    companion object {
        const val DOG = "DOG"
        private const val CAT = "CAT"
        private const val MALE = "MALE"
        private const val FEMALE = "FEMALE"
    }

}