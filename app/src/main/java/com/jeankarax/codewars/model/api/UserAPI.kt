package com.jeankarax.codewars.model.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jeankarax.codewars.model.response.ViewResponse
import com.jeankarax.codewars.model.response.BaseApiErrorResponse
import com.jeankarax.codewars.model.response.BaseApiSuccessResponse
import com.jeankarax.codewars.model.response.UserResponse
import javax.inject.Inject

class UserAPI
@Inject
constructor(
    private val apiCalls: APICalls
)
{

    val userLiveData = MediatorLiveData<ViewResponse<UserResponse>>()

    fun getUser(userName: String): LiveData<ViewResponse<UserResponse>> {
        userLiveData.value = ViewResponse.loading(null)
        userLiveData.addSource(apiCalls.getUser(userName)){ response ->
            when(response){
                is BaseApiSuccessResponse ->{
                    userLiveData.value = ViewResponse.success(response.body)
                }
                is BaseApiErrorResponse -> {
                    userLiveData.value = ViewResponse.error(response.errorMessage, null, response.throwable)
                }
            }
        }
        return userLiveData
    }
}
