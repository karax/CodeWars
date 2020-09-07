package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jeankarax.codewars.model.di.DaggerChallengeRepositoryComponent
import com.jeankarax.codewars.model.repository.ChallengeRepository
import com.jeankarax.codewars.model.repository.IChallengeRepository
import com.jeankarax.codewars.model.response.ChallengeResponse
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

    private val mapAuthoredListObserver = Observer<ChallengesListResponse>{
        auxAuthoredChallengeList = it
        challengeRepository.getCompletedChallenges(mUserName, 0)
        mapCompletedList()
    }

    private val mapCompletedListObserver = Observer<ChallengesListResponse>{
        auxCompletedChallengesList = it
        areListsOk.value = true
    }

    private lateinit var mUserName: String

    fun getLists(userName: String){
        isLoading.value = true
        mUserName = userName
        challengeRepository.getAuthoredChallenges(userName)
        mapLists()
    }

    private fun mapLists() {
        return challengeRepository.getAuthoredChallengesLiveData().observeForever(mapAuthoredListObserver)
    }

    private fun mapCompletedList(){
        return challengeRepository.getCompletedChallengesLiveData().observeForever(mapCompletedListObserver)
    }


}