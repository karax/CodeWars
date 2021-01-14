package com.jeankarax.codewars.model.api

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.response.*
import com.jeankarax.livedataretrofitadapterlibrary.*
import javax.inject.Inject

class ChallengeAPI
@Inject
constructor(
    private val apiCalls: APICalls
){

    val challengesListLiveData = MediatorLiveData<ViewResponse<List<ChallengesListResponse>>>()

    @VisibleForTesting
    val challengeLiveData = MutableLiveData<ViewResponse<ChallengeResponse>>()

    @VisibleForTesting
    val nextPageLiveData = MediatorLiveData<ViewResponse<ChallengesListResponse>>()

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
        return object: NetworkBoundResource<ChallengeResponse>(){
            override fun createCall(): LiveData<BaseApiResponse<ChallengeResponse>> {
                return apiCalls.getChallenge(id)
            }
            override fun handleApiSuccessResponse(response: BaseApiSuccessResponse<ChallengeResponse>) {
                challengeLiveData.value = ViewResponse.success(response.body)
                result.value = ViewResponse.success(response.body)
            }
            override fun handleApiErrorResponse(errorMessage: String, throwable: Throwable?) {
                challengeLiveData.value = ViewResponse.error(errorMessage, null, throwable)
                result.value = ViewResponse.error(errorMessage, null, throwable)
            }
        }.asLiveData()
    }

    fun getNextPage(userName: String, page: Long): LiveData<ViewResponse<ChallengesListResponse>> {
        return object: NetworkBoundResource<ChallengesListResponse>(){
            override fun createCall(): LiveData<BaseApiResponse<ChallengesListResponse>> {
                return apiCalls.getCompletedChallenges(userName, page)
            }
            override fun handleApiSuccessResponse(response: BaseApiSuccessResponse<ChallengesListResponse>) {
                nextPageLiveData.value = ViewResponse.success(response.body)
                result.value = ViewResponse.success(response.body)
            }
            override fun handleApiErrorResponse(errorMessage: String, throwable: Throwable?) {
                nextPageLiveData.value = ViewResponse.error(errorMessage, null, throwable)
                result.value = ViewResponse.error(errorMessage, null, throwable)
            }
        }.asLiveData()
    }

}