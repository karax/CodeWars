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
    val challengesListLiveData = MediatorLiveData<ViewResponse<List<ChallengesListResponse>>>()
    private val nextPageLiveData = MediatorLiveData<ViewResponse<ChallengesListResponse>>()

    fun getChallengesList(userName: String, page: Long): LiveData<ViewResponse<List<ChallengesListResponse>>>{
        challengesListLiveData.value = ViewResponse.loading(null)
        val auxListResponse: MutableList<ChallengesListResponse> = mutableListOf(
            ChallengesListResponse(), ChallengesListResponse()
        )
        challengesListLiveData.addSource(apiCalls.getCompletedChallenges(userName, page)){ response ->
            when(response){
                is BaseApiSuccessResponse -> {
                    auxListResponse[0] = response.body
                    if (!auxListResponse[1].data.isNullOrEmpty()){
                        challengesListLiveData.value = ViewResponse.success(auxListResponse)
                    }
                }
                is BaseApiErrorResponse -> {
                    challengesListLiveData.value = ViewResponse.error(response.errorMessage, null, response.throwable)
                }
            }
        }
        challengesListLiveData.addSource(apiCalls.getAuthoredChallenges(userName)){ response ->
            when(response){
                is BaseApiSuccessResponse -> {
                    auxListResponse[1] = response.body
                    if (!auxListResponse[0].data.isNullOrEmpty()){
                        challengesListLiveData.value = ViewResponse.success(auxListResponse)
                    }
                }
                is BaseApiErrorResponse -> {
                    challengesListLiveData.value = ViewResponse.error(response.errorMessage, null, response.throwable)
                }
            }
        }
        return challengesListLiveData
    }

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

    fun getNextPage(userName: String, page: Long): LiveData<ViewResponse<ChallengesListResponse>> {
        nextPageLiveData.addSource(apiCalls.getCompletedChallenges(userName, page)){ response ->
            when(response){
                is BaseApiSuccessResponse -> {
                    nextPageLiveData.value = ViewResponse.success(response.body)
                }
                is BaseApiErrorResponse -> {
                    nextPageLiveData.value = ViewResponse.error(response.errorMessage, null, response.throwable)
                }
            }
        }
        return nextPageLiveData
    }

}