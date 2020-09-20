package com.jeankarax.codewars.model.response

import retrofit2.Response
import java.lang.Exception

sealed class BaseApiResponse<T> {

    companion object {

        fun <T> create(error: Throwable): BaseApiErrorResponse<T> {
            return BaseApiErrorResponse(error.message ?: "unknown error", error)
        }

        fun <T> create(response: Response<T>): BaseApiResponse<T> {

            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    BaseApiEmptyResponse()
                } else {
                    BaseApiSuccessResponse(body)
                }
            } else {

                val msg = response.errorBody()?.string()

                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }

                val exception = when(response.code()) {
                    404 -> ResourceNotFoundException()
                    else -> null
                }

                BaseApiErrorResponse(errorMsg ?: "unknown error", exception)
            }
        }
    }
}

class BaseApiEmptyResponse<T> : BaseApiResponse<T>()

data class BaseApiSuccessResponse<T>(val body: T) : BaseApiResponse<T>()

data class BaseApiErrorResponse<T>(
    val errorMessage: String,
    val throwable: Throwable? = null
) : BaseApiResponse<T>()

class ResourceNotFoundException : Exception()