package com.jeankarax.codewars.model.api

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jeankarax.codewars.model.response.*
import com.jeankarax.livedataretrofitadapterlibrary.BaseApiResponse
import com.jeankarax.livedataretrofitadapterlibrary.BaseApiSuccessResponse
import com.jeankarax.livedataretrofitadapterlibrary.NetworkBoundResource
import com.jeankarax.livedataretrofitadapterlibrary.ViewResponse
import javax.inject.Inject

class UserAPI
@Inject
constructor(
    private val apiCalls: APICalls
)
{

    @VisibleForTesting
    val userLiveData = MediatorLiveData<ViewResponse<UserResponse>>()

    fun getUser(userName: String): LiveData<ViewResponse<UserResponse>> {
        return object: NetworkBoundResource<UserResponse>(){
            override fun createCall(): LiveData<BaseApiResponse<UserResponse>> {
                return apiCalls.getUser(userName)
            }
            override fun handleApiSuccessResponse(response: BaseApiSuccessResponse<UserResponse>) {
                userLiveData.value = ViewResponse.success(response.body)
                result.value = ViewResponse.success(response.body)
            }
            override fun handleApiErrorResponse(errorMessage: String, throwable: Throwable?) {
                userLiveData.value = ViewResponse.error(errorMessage, null, throwable)
                result.value = ViewResponse.error(errorMessage, null, throwable)
            }


        }.asLiveData()
    }
}
