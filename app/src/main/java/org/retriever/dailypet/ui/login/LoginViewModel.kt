package org.retriever.dailypet.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.login.LoginRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.login.*
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

    private val _familyNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val familyNameResponse: LiveData<Resource<ResponseBody>> = _familyNameResponse

    private val _registerFamilyResponse = MutableLiveData<Resource<ResponseBody>>()
    val registerFamilyResponse: LiveData<Resource<ResponseBody>> = _registerFamilyResponse

    private val _petNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val petNameResponse: LiveData<Resource<ResponseBody>> = _petNameResponse

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

    fun postCheckFamilyName(jwt: String, familyName: String) = viewModelScope.launch {
        _familyNameResponse.postValue(Resource.Loading())

        _familyNameResponse.postValue(loginRepository.postCheckFamilyName(jwt, familyName))
    }

    fun postFamily(jwt: String, familyInfo: FamilyInfo) = viewModelScope.launch {
        _registerFamilyResponse.postValue(Resource.Loading())

        _registerFamilyResponse.postValue(loginRepository.postFamily(jwt, familyInfo))
    }

    fun postCheckPetName(familyId: Int, jwt: String, petName: String) = viewModelScope.launch {
        _petNameResponse.postValue(Resource.Loading())

        _petNameResponse.postValue(loginRepository.postCheckPetName(familyId, jwt, petName))
    }

}

