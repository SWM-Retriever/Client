package org.retriever.dailypet.ui.signup.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.signup.GroupRepository
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.group.GroupInfo
import org.retriever.dailypet.model.signup.group.GroupResponse
import org.retriever.dailypet.model.signup.group.ModifyGroupResponse
import org.retriever.dailypet.ui.signup.EditTextState
import org.retriever.dailypet.ui.signup.EditTextValidateState
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val groupRepository: GroupRepository) : ViewModel() {

    private val _aloneButtonState = MutableStateFlow(false)
    val aloneButtonState: StateFlow<Boolean> = _aloneButtonState

    private val _groupButtonState = MutableStateFlow(false)
    val groupButtonState: StateFlow<Boolean> = _groupButtonState

    private val _chooseButtonState = MutableStateFlow(false)
    val chooseButtonState: StateFlow<Boolean> = _chooseButtonState

    private val _groupNameState = MutableStateFlow(EditTextValidateState.DEFAULT_STATE)
    val groupNameState: StateFlow<EditTextValidateState> = _groupNameState

    private val _groupRoleNameState = MutableStateFlow(EditTextState.DEFAULT_STATE)
    val groupRoleNameState: StateFlow<EditTextState> = _groupRoleNameState

    private val _nextButtonState = MutableStateFlow(false)
    val nextButtonState: StateFlow<Boolean> = _nextButtonState

    private val _groupNameResponse = MutableLiveData<Resource<ResponseBody>>()
    val groupNameResponse: LiveData<Resource<ResponseBody>> = _groupNameResponse

    private val _registerGroupResponse = MutableLiveData<Event<Resource<GroupResponse>>>()
    val registerGroupResponse: LiveData<Event<Resource<GroupResponse>>> = _registerGroupResponse

    private val _modifyGroupResponse = MutableLiveData<Resource<ModifyGroupResponse>>()
    val modifyGroupResponse: LiveData<Resource<ModifyGroupResponse>> = _modifyGroupResponse

    fun setAloneButtonState() {
        _aloneButtonState.value = true
        _groupButtonState.value = false
        _chooseButtonState.value = true
    }

    fun setGroupButtonState() {
        _aloneButtonState.value = false
        _groupButtonState.value = true
        _chooseButtonState.value = true
    }

    fun setGroupNameState(state: EditTextValidateState) {
        _groupNameState.value = state
        setNextButtonState()
    }

    fun setGroupRoleNameState(state: EditTextState) {
        _groupRoleNameState.value = state
        setNextButtonState()
    }

    private fun setNextButtonState() {
        _nextButtonState.value = (_groupNameState.value == EditTextValidateState.VALID_STATE) && (_groupRoleNameState.value == EditTextState.VALID_STATE)
    }

    fun postCheckFamilyName(jwt: String, familyName: String) {
        viewModelScope.launch {
            _groupNameResponse.postValue(Resource.Loading())

            _groupNameResponse.postValue(groupRepository.postCheckFamilyName(jwt, familyName))
        }
    }

    fun postFamily(jwt: String, groupInfo: GroupInfo) {
        viewModelScope.launch {
            _registerGroupResponse.postValue(Event(Resource.Loading()))

            _registerGroupResponse.postValue(Event(groupRepository.postFamily(jwt, groupInfo)))
        }
    }

    fun modifyFamily(familyId: Int, jwt: String, groupInfo: GroupInfo) {
        viewModelScope.launch {
            _modifyGroupResponse.postValue(Resource.Loading())

            _modifyGroupResponse.postValue(groupRepository.modifyGroup(familyId, jwt, groupInfo))
        }
    }

    fun makeAlone(jwt: String) {
        viewModelScope.launch {
            _registerGroupResponse.postValue(Event(Resource.Loading()))

            _registerGroupResponse.postValue(Event(groupRepository.makeAlone(jwt)))
        }
    }

}