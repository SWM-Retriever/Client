package org.retriever.dailypet.ui.main.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.diary.DiaryRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryPost
import org.retriever.dailypet.model.diary.DiaryResponse
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(private val diaryRepository: DiaryRepository) : ViewModel() {

    private val _diaryListResponse = MutableLiveData<Resource<DiaryResponse>>()
    val diaryListResponse: LiveData<Resource<DiaryResponse>> = _diaryListResponse

    private val _diaryPostResponse = MutableLiveData<Resource<ResponseBody>>()
    val diaryPostResponse: LiveData<Resource<ResponseBody>> = _diaryPostResponse

    private val _diaryDeleteResponse = MutableLiveData<Resource<ResponseBody>>()
    val diaryDeleteResponse: LiveData<Resource<ResponseBody>> = _diaryDeleteResponse

    fun getDiaryList(familyId: Int, jwt: String) = viewModelScope.launch {
        _diaryListResponse.postValue(Resource.Loading())

        _diaryListResponse.postValue(diaryRepository.getDiaryList(familyId, jwt))
    }

    fun postDiary(familyId: Int, jwt: String, diaryPost: DiaryPost) = viewModelScope.launch {
        _diaryPostResponse.postValue(Resource.Loading())

        _diaryPostResponse.postValue(diaryRepository.postDiary(familyId, jwt, diaryPost))
    }

    fun deleteDiary(familyId: Int, diaryId: Int, jwt: String) = viewModelScope.launch {
        _diaryDeleteResponse.postValue(Resource.Loading())

        _diaryDeleteResponse.postValue(diaryRepository.deleteDiary(familyId, diaryId, jwt))
    }

}