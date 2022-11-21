package org.retriever.dailypet.ui.mypage.detail

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
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.data.repository.mypage.MyPageRepository
import org.retriever.dailypet.data.repository.signup.PetRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.model.image.ImageResponse
import org.retriever.dailypet.model.mypage.GroupResponse
import org.retriever.dailypet.model.mypage.PetDetailItem
import org.retriever.dailypet.model.mypage.PetDetailResponse
import org.retriever.dailypet.model.signup.pet.ModifyPetRequest
import org.retriever.dailypet.model.signup.pet.ModifyPetResponse
import org.retriever.dailypet.ui.signup.EditTextState
import org.retriever.dailypet.ui.signup.EditTextValidateState
import javax.inject.Inject

@HiltViewModel
class MyPageDetailViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val petRepository: PetRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _petDetailResponse = MutableLiveData<Resource<PetDetailResponse>>()
    val petDetailResponse: LiveData<Resource<PetDetailResponse>> = _petDetailResponse

    private val _deletePetResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val deletePetResponse: LiveData<Event<Resource<ResponseBody>>> = _deletePetResponse

    private val _groupResponse = MutableLiveData<Resource<GroupResponse>>()
    val groupResponse: LiveData<Resource<GroupResponse>> = _groupResponse

    private val _getRecentDiaryResponse = MutableLiveData<Resource<DiaryItem>>()
    val getRecentDiaryResponse: LiveData<Resource<DiaryItem>> = _getRecentDiaryResponse

    private val _patchLeaderResponse = MutableLiveData<Resource<ResponseBody>>()
    val patchLeaderResponse: LiveData<Resource<ResponseBody>> = _patchLeaderResponse

    var clickPetDetailItem : PetDetailItem? = null

    private val _petNameState = MutableStateFlow(EditTextValidateState.DEFAULT_STATE)
    val petNameState: StateFlow<EditTextValidateState> = _petNameState

    private val _weightState = MutableStateFlow(EditTextState.DEFAULT_STATE)
    val weightState: StateFlow<EditTextState> = _weightState

    private val _registerButtonState = MutableStateFlow(false)
    val registerButtonState: StateFlow<Boolean> = _registerButtonState

    private val _petNameResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val petNameResponse: LiveData<Event<Resource<ResponseBody>>> = _petNameResponse

    private val _postImageResponse = MutableLiveData<Resource<ImageResponse>>()
    val postImageResponse: LiveData<Resource<ImageResponse>> = _postImageResponse

    private val _modifyPetResponse = MutableLiveData<Event<Resource<ModifyPetResponse>>>()
    val modifyPetResponse: LiveData<Event<Resource<ModifyPetResponse>>> = _modifyPetResponse

    fun getPetList(familyId: Int, jwt: String) {
        viewModelScope.launch {
            _petDetailResponse.postValue(Resource.Loading())

            _petDetailResponse.postValue(myPageRepository.getPetList(familyId, jwt))
        }
    }

    fun deletePet(jwt: String, familyId: Int, petId: Int) {
        viewModelScope.launch {
            _deletePetResponse.postValue(Event(Resource.Loading()))

            _deletePetResponse.postValue(Event(myPageRepository.deletePet(jwt, familyId, petId)))
        }
    }

    fun getGroupInfo(familyId: Int, jwt: String) {
        viewModelScope.launch {
            _groupResponse.postValue(Resource.Loading())

            _groupResponse.postValue(myPageRepository.getGroupInfo(familyId, jwt))
        }
    }

    fun getRecentDiary(familyId: Int, jwt: String) {
        viewModelScope.launch {
            _getRecentDiaryResponse.postValue(Resource.Loading())

            _getRecentDiaryResponse.postValue(myPageRepository.getRecentDiary(familyId, jwt))
        }
    }

    fun patchLeader(familyId: Int, memberId: Int, jwt: String) {
        viewModelScope.launch {
            _patchLeaderResponse.postValue(Resource.Loading())

            _patchLeaderResponse.postValue(myPageRepository.patchLeader(familyId, memberId, jwt))
        }
    }

    fun setModifyPet(petDetailItem: PetDetailItem){
        clickPetDetailItem = petDetailItem
    }

    fun setPetNameState(state: EditTextValidateState) {
        _petNameState.value = state
        setRegisterButtonState()
    }

    fun setWeightState(state: EditTextState) {
        _weightState.value = state
        setRegisterButtonState()
    }

    private fun setRegisterButtonState() {
        _registerButtonState.value =
            (_petNameState.value == EditTextValidateState.VALID_STATE) && (_weightState.value == EditTextState.VALID_STATE)
    }

    fun postCheckPetName(familyId: Int, jwt: String, petName: String) = viewModelScope.launch {
        _petNameResponse.postValue(Event(Resource.Loading()))

        _petNameResponse.postValue(Event(petRepository.postCheckPetName(familyId, jwt, petName)))
    }

    fun postImage(s3Path: String, file: MultipartBody.Part) = viewModelScope.launch {
        _postImageResponse.postValue(Resource.Loading())

        _postImageResponse.postValue(imageRepository.postImage(s3Path, file))
    }

    fun modifyPet(familyId: Int, petId: Int, jwt: String, modifyPetRequest: ModifyPetRequest) {
        viewModelScope.launch {
            _modifyPetResponse.postValue(Event(Resource.Loading()))

            _modifyPetResponse.postValue(Event(petRepository.modifyPet(familyId, petId, jwt, modifyPetRequest)))
        }
    }

}