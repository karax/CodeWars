package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.codewars.model.response.ViewResponse

interface IChallengeRepository {

    fun getCompletedChallenges(userName: String, page: Long, isFirstCall: Boolean)

    fun getAuthoredChallenges(userName: String)

    fun getCompletedChallengesLiveData(): LiveData<ChallengesListResponse>

    fun getAllChallengesLiveData(): LiveData<MutableList<ChallengesListResponse>>

    fun getErrorLiveData(): LiveData<Throwable>

    fun clearDisposable()

    fun setApplicationContext(application: Application)

    fun getChallenge(id: String): LiveData<ViewResponse<ChallengeResponse>>
}