package org.retriever.dailypet.test.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.test.data.repository.login.LoginRepository
import org.retriever.dailypet.test.model.Resource
import org.retriever.dailypet.test.model.login.LoginResponse
import org.retriever.dailypet.test.model.login.Member
import org.retriever.dailypet.test.model.login.RegisterProfile
import org.retriever.dailypet.test.model.login.RegisterProfileResponse
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Resource<LoginResponse>>()
    val loginResponse: LiveData<Resource<LoginResponse>> = _loginResponse

    private val _nickNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val nickNameResponse: LiveData<Resource<ResponseBody>> = _nickNameResponse

    private val _registerProfileResponse = MutableLiveData<Resource<RegisterProfileResponse>>()
    val registerProfileResponse: LiveData<Resource<RegisterProfileResponse>> = _registerProfileResponse

    fun postIsMember(member: Member) = viewModelScope.launch {
        _loginResponse.postValue(Resource.Loading())

        _loginResponse.postValue(loginRepository.postIsMember(member))
    }

    fun postCheckProfileNickname(nickName: String) = viewModelScope.launch {
        _nickNameResponse.postValue(Resource.Loading())

        _nickNameResponse.postValue(loginRepository.postCheckProfileNickname(nickName))
    }

    fun postProfile(registerProfile: RegisterProfile, image: MultipartBody.Part?) = viewModelScope.launch {
        _registerProfileResponse.postValue(Resource.Loading())

        _registerProfileResponse.postValue(loginRepository.postProfile(registerProfile, image))
    }

}

