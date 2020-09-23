package com.jeankarax.codewars.model.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jeankarax.codewars.model.response.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class NetworkBoundResource<ResponseObject> {

    protected val result = MediatorLiveData<ViewResponse<ResponseObject>>()

    init {
        result.value = ViewResponse.loading(null)

        GlobalScope.launch(IO) {
            withContext(Main){
                val apiResponse = createCall()
                result.addSource(apiResponse){response ->
                    result.removeSource(apiResponse)
                    handleNetworkCall(response)
                }
            }
        }
    }

    private fun handleNetworkCall(response: BaseApiResponse<ResponseObject>){
        when(response){
            is BaseApiSuccessResponse ->{
                handleApiSuccessResponse(response)
            }
            is BaseApiErrorResponse -> {
                handleApiErrorResponse(response.errorMessage, response.throwable)
            }
            is BaseApiEmptyResponse -> {
                onReturnError("HTTP 204. Returned nothing.", null)
            }
        }
    }

    private fun onReturnError(errorMessage: String, throwable: Throwable?) {
        result.value = ViewResponse.error(errorMessage, null, throwable)
    }

    abstract fun handleApiSuccessResponse(response: BaseApiSuccessResponse<ResponseObject>)

    abstract fun handleApiErrorResponse(errorMessage: String, throwable: Throwable?)

    abstract fun createCall(): LiveData<BaseApiResponse<ResponseObject>>

    fun asLiveData() = result as LiveData<ViewResponse<ResponseObject>>

}