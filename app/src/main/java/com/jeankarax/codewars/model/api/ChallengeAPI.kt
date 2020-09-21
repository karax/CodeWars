package com.jeankarax.codewars.model.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jeankarax.codewars.model.response.*
import javax.inject.Inject

class ChallengeAPI
@Inject
constructor(
    private val apiCalls: APICalls
){

    val challengeLiveData = MediatorLiveData<ViewResponse<ChallengeResponse>>()

    fun getCompletedChallenges(userName: String, page: Int) = apiCalls.getCompletedChallenges(userName, page)

    fun getAuthoredChallenges(userName: String) = apiCalls.getAuthoredChallenges(userName)

    fun getChallenge(id: String): LiveData<ViewResponse<ChallengeResponse>>{
        challengeLiveData.value = ViewResponse.loading(null)
        challengeLiveData.addSource(apiCalls.getChallenge(id)){ response ->
            when(response){
                is BaseApiSuccessResponse ->{
                    challengeLiveData.value = ViewResponse.success(response.body)
                }
                is BaseApiErrorResponse -> {
                    challengeLiveData.value = ViewResponse.error(response.errorMessage, null, response.throwable)
                }
            }
        }
        return challengeLiveData
    }

}