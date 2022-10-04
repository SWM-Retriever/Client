package org.retriever.dailypet.model

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int? = null,
) {

    class Success<T>(data: T) : Resource<T>(data = data)

    class Error<T>(errorMessage: String, errorCode: Int) :
        Resource<T>(message = errorMessage, code = errorCode)

    class Loading<T> : Resource<T>()

}