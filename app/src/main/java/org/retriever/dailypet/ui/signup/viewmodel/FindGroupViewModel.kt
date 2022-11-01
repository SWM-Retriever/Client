package org.retriever.dailypet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.signup.FindGroupRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.family.EnterFamilyResponse
import org.retriever.dailypet.model.signup.family.FindGroupResponse
import javax.inject.Inject

@HiltViewModel
class FindGroupViewModel @Inject constructor(private val findGroupRepository: FindGroupRepository) : ViewModel() {

    private val _getGroupInfoResponse = MutableLiveData<Resource<FindGroupResponse>>()
    val getGroupInfoResponse: LiveData<Resource<FindGroupResponse>> = _getGroupInfoResponse

    private val _groupNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val groupNameResponse: LiveData<Resource<ResponseBody>> = _groupNameResponse

    private val _enterGroupResponse = MutableLiveData<Resource<EnterFamilyResponse>>()
    val enterGroupResponse: LiveData<Resource<EnterFamilyResponse>> = _enterGroupResponse

    fun getGroupInfo(invitationCode: String, jwt: String) = viewModelScope.launch {
        _getGroupInfoResponse.postValue(Resource.Loading())

        _getGroupInfoResponse.postValue(findGroupRepository.getGroupInfo(invitationCode, jwt))
    }

    fun postCheckGroupNickname(familyId: Int, jwt: String, familyRoleName: String) = viewModelScope.launch {
        _groupNameResponse.postValue(Resource.Loading())

        _groupNameResponse.postValue(findGroupRepository.postCheckGroupNickname(familyId, jwt, familyRoleName))
    }

    fun postEnterGroup(familyId: Int, jwt: String, familyRoleName: String) = viewModelScope.launch {
        _enterGroupResponse.postValue(Resource.Loading())

        _enterGroupResponse.postValue(findGroupRepository.postEnterGroup(familyId, jwt, familyRoleName))
    }

}