package org.retriever.dailypet.data.repository.diary

import okhttp3.ResponseBody
import org.retriever.dailypet.data.network.diary.DiaryApiInterface
import org.retriever.dailypet.data.repository.BaseRepo
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryPost
import org.retriever.dailypet.model.diary.DiaryResponse
import javax.inject.Inject

class DiaryRepository @Inject constructor(private val diaryApiInterface: DiaryApiInterface) : BaseRepo() {

    suspend fun getDiaryList(familyId: Int, jwt: String): Resource<DiaryResponse> =
        safeApiCall { diaryApiInterface.getDiaryList(familyId, jwt) }

    suspend fun postDiary(familyId: Int, jwt: String, diaryPost: DiaryPost): Resource<ResponseBody> =
        safeApiCall { diaryApiInterface.postDiary(familyId, jwt, diaryPost) }

    suspend fun deleteDiary(familyId: Int, diaryId: Int, jwt: String): Resource<ResponseBody> =
        safeApiCall { diaryApiInterface.deleteDiary(familyId, diaryId, jwt) }

}