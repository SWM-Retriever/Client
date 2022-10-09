package org.retriever.dailypet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.retriever.dailypet.Event
import org.retriever.dailypet.data.repository.signup.FindGroupRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.family.FindGroupResponse
import javax.inject.Inject

@HiltViewModel
class FindGroupViewModel @Inject constructor(private val findGroupRepository: FindGroupRepository) : ViewModel() {

    private val _findGroupResponse = MutableLiveData<Resource<FindGroupResponse>>()
    val findGroupResponse: LiveData<Resource<FindGroupResponse>> = _findGroupResponse

    fun getGroupInfo(invitationCode: String, jwt: String) = viewModelScope.launch {
        _findGroupResponse.postValue(Resource.Loading())

        _findGroupResponse.postValue(findGroupRepository.getGroupInfo(invitationCode, jwt))
    }
}