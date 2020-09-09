package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jeankarax.codewars.model.di.DaggerChallengeRepositoryComponent
import com.jeankarax.codewars.model.repository.IChallengeRepository
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
import javax.inject.Inject

class ChallengesListsViewModel(application: Application): AndroidViewModel(application) {


    val areListsOk by lazy { MutableLiveData<Boolean>() }
    val isLoading by lazy { MutableLiveData<Boolean>() }
    val challengeLiveData by lazy { MutableLiveData<ChallengeResponse>() }
    val isNextPageLoadedLiveData by lazy{MutableLiveData<Boolean>()}
    val irError by lazy { MutableLiveData<Boolean>() }
    private lateinit var auxCompletedChallengesList: MutableList<ChallengeResponse>
    private lateinit var auxAuthoredChallengeList: List<ChallengeResponse>
    private var auxListTotalPages: Long = 0
    private var auxNextPage: Long = 1
    private var auxUserName: String =""
    var completedListWithLoading: MutableList<ChallengeResponse> = mutableListOf()

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectInChallengeListsViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
        completedListWithLoading = mutableListOf()
    }

    private val mapListsObserver = Observer<List<ChallengesListResponse>>{
        val placeHolderChallenge = ChallengeResponse("placeholder")
        auxCompletedChallengesList = completedListWithLoading
        auxAuthoredChallengeList = it[1].data!!
        if(auxNextPage == 1.toLong()){
            completedListWithLoading.clear()
            completedListWithLoading.addAll(it[0].data as MutableList<ChallengeResponse>)
            completedListWithLoading.add(placeHolderChallenge)
            areListsOk.value = true
        }
        else{
            completedListWithLoading.removeAt(completedListWithLoading.size-1)
            completedListWithLoading.addAll(it[0].data as MutableList<ChallengeResponse>)
            if(auxNextPage <= auxListTotalPages){
                completedListWithLoading.add(placeHolderChallenge)
            }else{
                val placeHolderLastChallenge = ChallengeResponse("lastItem")
                completedListWithLoading.add(placeHolderLastChallenge)
            }
            isNextPageLoadedLiveData.value = true
        }
        isLoading.value = false
        auxListTotalPages = it[0].totalPages!!
    }

    private val mapChallengeObserver = Observer<ChallengeResponse>{
        challengeLiveData.value = it
        isLoading.value = false
    }

    fun getLists(userName: String){
        auxUserName = userName
        isLoading.value = true
        challengeRepository.getCompletedChallenges(userName, 0, true)
        mapLists()
        auxNextPage = 1
    }

    fun getChallenge(challengeId: String){
        isLoading.value = true
        challengeRepository.getChallenge(challengeId)
        mapChallenge()
    }

    fun getNextPage() {
        challengeRepository.getCompletedChallenges(auxUserName, auxNextPage, false)
        auxNextPage++
    }

    private fun mapLists() {
        return challengeRepository.getAllChallengesLiveData().observeForever(mapListsObserver)
    }

    private fun mapChallenge() {
        return challengeRepository.getChallengeLiveData().observeForever(mapChallengeObserver)
    }

    fun getLoadedCompletedList()= auxCompletedChallengesList


    fun getLoadedAuthoredList() = auxAuthoredChallengeList

    override fun onCleared() {
        super.onCleared()
        challengeRepository.getAllChallengesLiveData().removeObserver(mapListsObserver)
        challengeRepository.clearDisposable()
    }
}