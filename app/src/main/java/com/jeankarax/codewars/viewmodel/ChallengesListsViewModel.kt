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
    val irError by lazy { MutableLiveData<Boolean>() }
    private lateinit var auxCompletedChallengesList: ChallengesListResponse
    private lateinit var auxAuthoredChallengeList: ChallengesListResponse

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectInChallengeListsViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
    }

    private val mapListsObserver = Observer<List<ChallengesListResponse>>{
        auxCompletedChallengesList = it[0]
        auxAuthoredChallengeList = it[1]
        areListsOk.value = true
        isLoading.value = false
    }

    private val mapChallengeObserver = Observer<ChallengeResponse>{
        challengeLiveData.value = it
        isLoading.value = false
    }

    fun getLists(userName: String){
        isLoading.value = true
        challengeRepository.getCompletedChallenges(userName, 0, true)
        mapLists()
    }

    private fun mapLists() {
        return challengeRepository.getAllChallengesLiveData().observeForever(mapListsObserver)
    }

    fun getChallenge(challengeId: String){
        isLoading.value = true
        challengeRepository.getChallenge(challengeId)
        mapChallenge()
    }

    private fun mapChallenge() {
        return challengeRepository.getChallengeLiveData().observeForever(mapChallengeObserver)
    }

    fun getLoadedCompletedList() = auxCompletedChallengesList

    fun getLoadedAuthoredList() = auxAuthoredChallengeList

    override fun onCleared() {
        super.onCleared()
        challengeRepository.getAllChallengesLiveData().removeObserver(mapListsObserver)
        challengeRepository.clearDisposable()
    }
}