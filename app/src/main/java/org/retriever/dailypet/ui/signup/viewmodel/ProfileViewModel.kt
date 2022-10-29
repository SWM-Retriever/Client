package org.retriever.dailypet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.data.repository.signup.ProfileRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.model.signup.profile.RegisterProfileResponse
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel(){

    private val _nickNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val nickNameResponse: LiveData<Resource<ResponseBody>> = _nickNameResponse

    private val _registerProfileResponse = MutableLiveData<Event<Resource<RegisterProfileResponse>>>()
    val registerProfileResponse: LiveData<Event<Resource<RegisterProfileResponse>>> = _registerProfileResponse

    fun postCheckProfileNickname(nickName: String) = viewModelScope.launch {
        _nickNameResponse.postValue(Resource.Loading())

        _nickNameResponse.postValue(profileRepository.postCheckProfileNickname(nickName))
    }

    fun postProfile(registerProfile: RegisterProfile) = viewModelScope.launch {
        _registerProfileResponse.postValue(Event(Resource.Loading()))

        _registerProfileResponse.postValue(Event(profileRepository.postProfile(registerProfile)))
    }

}