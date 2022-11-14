package org.retriever.dailypet.ui.signup.profile

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
import org.retriever.dailypet.data.repository.signup.ProfileRepository
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.image.ImageResponse
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.model.signup.profile.RegisterProfileResponse
import org.retriever.dailypet.ui.signup.EditTextValidateState
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _allCheckState = MutableStateFlow(false)
    val allCheckState: StateFlow<Boolean> = _allCheckState

    private val _firstCheckState = MutableStateFlow(false)
    val firstCheckState: StateFlow<Boolean> = _firstCheckState

    private val _secondCheckState = MutableStateFlow(false)
    val secondCheckState: StateFlow<Boolean> = _secondCheckState

    private val _thirdCheckState = MutableStateFlow(false)
    val thirdCheckState: StateFlow<Boolean> = _thirdCheckState

    private val _nextButtonState = MutableStateFlow(false)
    val nextButtonState: StateFlow<Boolean> = _nextButtonState

    private val _editTextValidateState = MutableLiveData(EditTextValidateState.DEFAULT_STATE)
    val editTextValidateState: LiveData<EditTextValidateState> = _editTextValidateState

    private val _registerButtonState = MutableStateFlow(false)
    val registerButtonState : StateFlow<Boolean> = _registerButtonState

    private val _nickNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val nickNameResponse: LiveData<Resource<ResponseBody>> = _nickNameResponse

    private val _registerProfileResponse = MutableLiveData<Event<Resource<RegisterProfileResponse>>>()
    val registerProfileResponse: LiveData<Event<Resource<RegisterProfileResponse>>> = _registerProfileResponse

    private val _postImageResponse = MutableLiveData<Resource<ImageResponse>>()
    val postImageResponse: LiveData<Resource<ImageResponse>> = _postImageResponse


    fun setAllCheckState(check: Boolean) {
        _allCheckState.value = check
        _firstCheckState.value = check
        _secondCheckState.value = check
        _thirdCheckState.value = check
        setNextButtonState()
    }

    fun setFirstCheckState(check: Boolean) {
        _firstCheckState.value = check
        setAllCheckState()
        setNextButtonState()
    }

    fun setSecondCheckState(check: Boolean) {
        _secondCheckState.value = check
        setAllCheckState()
        setNextButtonState()
    }

    fun setThirdCheckState(check: Boolean) {
        _thirdCheckState.value = check
        setAllCheckState()
    }

    private fun setAllCheckState() {
        _allCheckState.value = _firstCheckState.value && _secondCheckState.value && _thirdCheckState.value
    }

    private fun setNextButtonState() {
        _nextButtonState.value = _firstCheckState.value && _secondCheckState.value
    }

    fun setNickNameState(state : EditTextValidateState){
        _editTextValidateState.value = state
    }

    fun setRegisterButtonState(check : Boolean){
        _registerButtonState.value = check
    }

    fun postCheckProfileNickname(nickName: String) = viewModelScope.launch {
        _nickNameResponse.postValue(Resource.Loading())

        _nickNameResponse.postValue(profileRepository.postCheckProfileNickname(nickName))
    }

    fun postProfile(registerProfile: RegisterProfile) = viewModelScope.launch {
        _registerProfileResponse.postValue(Event(Resource.Loading()))

        _registerProfileResponse.postValue(Event(profileRepository.postProfile(registerProfile)))
    }

    fun postImage(s3Path: String, file: MultipartBody.Part) = viewModelScope.launch {
        _postImageResponse.postValue(Resource.Loading())

        _postImageResponse.postValue(imageRepository.postImage(s3Path, file))
    }

}