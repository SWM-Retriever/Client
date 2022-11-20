package org.retriever.dailypet.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.image.ImageRepository
import org.retriever.dailypet.data.repository.mypage.MyPageRepository
import org.retriever.dailypet.data.repository.signup.ProfileRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.image.ImageResponse
import org.retriever.dailypet.ui.signup.EditTextValidateState
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _withdrawalResponse = MutableLiveData<Resource<ResponseBody>>()
    val withdrawalResponse: LiveData<Resource<ResponseBody>> = _withdrawalResponse

    private val _editTextValidateState = MutableLiveData(EditTextValidateState.DEFAULT_STATE)
    val editTextValidateState: LiveData<EditTextValidateState> = _editTextValidateState

    private val _registerButtonState = MutableStateFlow(false)
    val registerButtonState : StateFlow<Boolean> = _registerButtonState

    private val _postImageResponse = MutableLiveData<Resource<ImageResponse>>()
    val postImageResponse: LiveData<Resource<ImageResponse>> = _postImageResponse

    private val _nickNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val nickNameResponse: LiveData<Resource<ResponseBody>> = _nickNameResponse

    fun deleteMemberWithdrawal(jwt: String) = viewModelScope.launch {
        _withdrawalResponse.postValue(Resource.Loading())

        _withdrawalResponse.postValue(myPageRepository.deleteMemberWithdrawal(jwt))
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


}