package org.retriever.dailypet.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.retriever.dailypet.data.repository.login.LoginRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.login.LoginResponse
import org.retriever.dailypet.model.login.Member
import org.retriever.dailypet.model.login.ProgressStatusResponse
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Resource<LoginResponse>>()
    val loginResponse: LiveData<Resource<LoginResponse>> = _loginResponse

    private val _progressStatusResponse = MutableLiveData<Resource<ProgressStatusResponse>>()
    val progressStatusResponse: LiveData<Resource<ProgressStatusResponse>> = _progressStatusResponse

    fun postIsMember(member: Member) = viewModelScope.launch {
        _loginResponse.postValue(Resource.Loading())

        _loginResponse.postValue(loginRepository.postIsMember(member))
    }

    fun getProgressStatus(jwt: String) = viewModelScope.launch {
        _progressStatusResponse.postValue(Resource.Loading())

        _progressStatusResponse.postValue(loginRepository.getProgressStatus(jwt))
    }

}

