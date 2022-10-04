package org.retriever.dailypet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.signup.FamilyRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.family.FamilyInfo
import org.retriever.dailypet.model.signup.family.FamilyResponse
import javax.inject.Inject

@HiltViewModel
class FamilyViewModel @Inject constructor(private val familyRepository: FamilyRepository) : ViewModel() {

    private val _familyNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val familyNameResponse: LiveData<Resource<ResponseBody>> = _familyNameResponse

    private val _registerFamilyResponse = MutableLiveData<Resource<FamilyResponse>>()
    val registerFamilyResponse: LiveData<Resource<FamilyResponse>> = _registerFamilyResponse

    fun postCheckFamilyName(jwt: String, familyName: String) = viewModelScope.launch {
        _familyNameResponse.postValue(Resource.Loading())

        _familyNameResponse.postValue(familyRepository.postCheckFamilyName(jwt, familyName))
    }

    fun postFamily(jwt: String, familyInfo: FamilyInfo) = viewModelScope.launch {
        _registerFamilyResponse.postValue(Resource.Loading())

        _registerFamilyResponse.postValue(familyRepository.postFamily(jwt, familyInfo))
    }

}