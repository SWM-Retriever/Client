package org.retriever.dailypet.test.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.retriever.dailypet.test.data.repository.login.LoginRepository
import org.retriever.dailypet.test.model.Resource
import org.retriever.dailypet.test.model.login.LoginResponse
import org.retriever.dailypet.test.model.login.Member
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Resource<LoginResponse>>()
    val loginResponse: LiveData<Resource<LoginResponse>> = _loginResponse

    fun postIsMember(member: Member) = viewModelScope.launch{
        _loginResponse.postValue(Resource.Loading())

        _loginResponse.postValue(loginRepository.postIsMember(member))
    }

}

