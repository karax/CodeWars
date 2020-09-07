package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData

interface IChallengeRepository {

    fun getChallenge(id: Int)

    fun getCompletedChallenges(userName: String, page: Int)

    fun getAuthoredChallenges(userName: String)

    fun getErrorObservable(): LiveData<Throwable>

    fun clearDisposable()

    fun setApplicationContext(application: Application)

}