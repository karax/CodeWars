package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse

interface IChallengeRepository {

    fun getChallenge(id: String)

    fun getCompletedChallenges(userName: String, page: Long, isFirstCall: Boolean)

    fun getAuthoredChallenges(userName: String)

    fun getChallengeLiveData(): LiveData<ChallengeResponse>

    fun getCompletedChallengesLiveData(): LiveData<ChallengesListResponse>

    fun getAllChallengesLiveData(): LiveData<MutableList<ChallengesListResponse>>

    fun getErrorObservable(): LiveData<Throwable>

    fun clearDisposable()

    fun setApplicationContext(application: Application)

}