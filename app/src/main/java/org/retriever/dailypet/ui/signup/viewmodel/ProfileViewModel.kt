package org.retriever.dailypet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.presignedurl.PreSignedUrlRepository
import org.retriever.dailypet.data.repository.signup.ProfileRepository
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.presignedurl.PreSignedUrlResponse
import org.retriever.dailypet.model.signup.profile.RegisterProfile
import org.retriever.dailypet.model.signup.profile.RegisterProfileResponse
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val preSignedUrlRepository: PreSignedUrlRepository,
) : ViewModel() {

    private val _nickNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val nickNameResponse: LiveData<Resource<ResponseBody>> = _nickNameResponse

    private val _registerProfileResponse = MutableLiveData<Event<Resource<RegisterProfileResponse>>>()
    val registerProfileResponse: LiveData<Event<Resource<RegisterProfileResponse>>> = _registerProfileResponse

    private val _preSignedUrlResponse = MutableLiveData<Resource<PreSignedUrlResponse>>()
    val preSignedUrlResponse: LiveData<Resource<PreSignedUrlResponse>> = _preSignedUrlResponse

    private val _putImageUrlResponse = MutableLiveData<Resource<ResponseBody>>()
    val putImageUrlResponse: LiveData<Resource<ResponseBody>> = _putImageUrlResponse

    fun postCheckProfileNickname(nickName: String) = viewModelScope.launch {
        _nickNameResponse.postValue(Resource.Loading())

        _nickNameResponse.postValue(profileRepository.postCheckProfileNickname(nickName))
    }

    fun postProfile(registerProfile: RegisterProfile) = viewModelScope.launch {
        _registerProfileResponse.postValue(Event(Resource.Loading()))

        _registerProfileResponse.postValue(Event(profileRepository.postProfile(registerProfile)))
    }

    fun getPreSignedUrl(s3Path: String, fileName: String) {
        viewModelScope.launch {
            _preSignedUrlResponse.postValue(Resource.Loading())

            _preSignedUrlResponse.postValue(preSignedUrlRepository.getPreSignedUrl(s3Path, fileName))
        }
    }

    fun putImageUrl(contentType: String, url: String, file: MultipartBody.Part) {
        viewModelScope.launch {
            _putImageUrlResponse.postValue(Resource.Loading())

            _putImageUrlResponse.postValue(preSignedUrlRepository.putImageUrl(contentType, url, file))
        }
    }

}