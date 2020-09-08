package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jeankarax.codewars.model.di.DaggerChallengeRepositoryComponent
import com.jeankarax.codewars.model.repository.IChallengeRepository
import com.jeankarax.codewars.model.response.ChallengesListResponse
import javax.inject.Inject

class ChallengesListsViewModel(application: Application): AndroidViewModel(application) {


    val areListsOk by lazy { MutableLiveData<Boolean>() }
    val isLoading by lazy { MutableLiveData<Boolean>() }
    val irError by lazy { MutableLiveData<Boolean>() }
    private lateinit var auxCompletedChallengesList: ChallengesListResponse
    private lateinit var auxAuthoredChallengeList: ChallengesListResponse

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectInChallengeListsViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
    }

    private lateinit var mUserName: String

    private val mapListsObserver = Observer<List<ChallengesListResponse>>{
        auxCompletedChallengesList = it[0]
        auxAuthoredChallengeList = it[1]
        areListsOk.value = true
        isLoading.value = false
    }

    fun getLists(userName: String){
        isLoading.value = true
        mUserName = userName
        challengeRepository.getCompletedChallenges(userName, 0, true)
        mapLists()
    }

    private fun mapLists() {
        return challengeRepository.getAllChallengesLiveData().observeForever(mapListsObserver)
    }

    fun getLoadedCompletedList() = auxCompletedChallengesList

    fun getLoadedAuthoredList() = auxAuthoredChallengeList

    override fun onCleared() {
        super.onCleared()
        challengeRepository.getAllChallengesLiveData().removeObserver(mapListsObserver)
        challengeRepository.clearDisposable()
    }
}