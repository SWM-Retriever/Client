package org.retriever.dailypet.ui.mypage.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.model.Event
import org.retriever.dailypet.data.repository.mypage.MyPageRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.model.mypage.GroupResponse
import org.retriever.dailypet.model.mypage.PetDetailResponse
import javax.inject.Inject

@HiltViewModel
class MyPageDetailViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    private val _petDetailResponse = MutableLiveData<Resource<PetDetailResponse>>()
    val petDetailResponse: LiveData<Resource<PetDetailResponse>> = _petDetailResponse

    private val _deletePetResponse = MutableLiveData<Event<Resource<ResponseBody>>>()
    val deletePetResponse: LiveData<Event<Resource<ResponseBody>>> = _deletePetResponse

    private val _groupResponse = MutableLiveData<Resource<GroupResponse>>()
    val groupResponse: LiveData<Resource<GroupResponse>> = _groupResponse

    private val _getRecentDiaryResponse = MutableLiveData<Resource<DiaryItem>>()
    val getRecentDiaryResponse: LiveData<Resource<DiaryItem>> = _getRecentDiaryResponse

    private val _patchLeaderResponse = MutableLiveData<Resource<ResponseBody>>()
    val patchLeaderResponse: LiveData<Resource<ResponseBody>> = _patchLeaderResponse

    fun getPetList(familyId: Int, jwt: String) {
        viewModelScope.launch {
            _petDetailResponse.postValue(Resource.Loading())

            _petDetailResponse.postValue(myPageRepository.getPetList(familyId, jwt))
        }
    }

    fun deletePet(jwt: String, familyId: Int, petId: Int) {
        viewModelScope.launch {
            _deletePetResponse.postValue(Event(Resource.Loading()))

            _deletePetResponse.postValue(Event(myPageRepository.deletePet(jwt, familyId, petId)))
        }
    }

    fun getGroupInfo(familyId: Int, jwt: String) {
        viewModelScope.launch {
            _groupResponse.postValue(Resource.Loading())

            _groupResponse.postValue(myPageRepository.getGroupInfo(familyId, jwt))
        }
    }

    fun getRecentDiary(familyId: Int, jwt: String) {
        viewModelScope.launch {
            _getRecentDiaryResponse.postValue(Resource.Loading())

            _getRecentDiaryResponse.postValue(myPageRepository.getRecentDiary(familyId, jwt))
        }
    }

    fun patchLeader(familyId: Int, memberId: Int, jwt: String) {
        viewModelScope.launch {
            _patchLeaderResponse.postValue(Resource.Loading())

            _patchLeaderResponse.postValue(myPageRepository.patchLeader(familyId, memberId, jwt))
        }
    }

}