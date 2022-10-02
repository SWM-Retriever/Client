package org.retriever.dailypet.data.repository

import android.util.Log
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.retriever.dailypet.model.ExampleErrorResponse
import org.retriever.dailypet.model.Resource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepo() {

    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()
                if (response.isSuccessful) {
                    Resource.Success(data = response.body()!!)
                } else {
                    val errorResponse: ExampleErrorResponse? =
                        convertErrorBody(response.errorBody())
                    Resource.Error(errorMessage = errorResponse?.message ?: "Something went wrong", errorCode = response.code())
                }

            } catch (e: HttpException) {
                Log.e("BaseRepoError", e.printStackTrace().toString())
                Resource.Error(errorMessage = e.message ?: "Something went wrong", errorCode = 403)
            } catch (e: IOException) {
                Log.e("BaseRepoError", e.printStackTrace().toString())
                Resource.Error("Please check your network connection", errorCode = 403)
            } catch (e: Exception) {
                Log.e("BaseRepoError", e.printStackTrace().toString())
                Resource.Error(errorMessage = "Something went wrong", errorCode = 403)
            }
        }
    }

    private fun convertErrorBody(errorBody: ResponseBody?): ExampleErrorResponse? {
        return try {
            errorBody?.source()?.let {
                val moshiAdapter = Moshi.Builder().build().adapter(ExampleErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (exception: Exception) {
            null
        }
    }
}