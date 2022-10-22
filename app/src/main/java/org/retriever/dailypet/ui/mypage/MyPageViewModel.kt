package org.retriever.dailypet.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.retriever.dailypet.data.repository.mypage.MyPageRepository
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.mypage.PetDetailResponse
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    private val _withdrawalResponse = MutableLiveData<Resource<ResponseBody>>()
    val withdrawalResponse: LiveData<Resource<ResponseBody>> = _withdrawalResponse

    fun deleteMemberWithdrawal(jwt: String) = viewModelScope.launch {
        _withdrawalResponse.postValue(Resource.Loading())

        _withdrawalResponse.postValue(myPageRepository.deleteMemberWithdrawal(jwt))
    }

}