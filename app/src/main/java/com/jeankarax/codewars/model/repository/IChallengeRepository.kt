package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.livedataretrofitadapterlibrary.ViewResponse

interface IChallengeRepository {

    fun setApplicationContext(application: Application)

    fun getChallenge(id: String): LiveData<ViewResponse<ChallengeResponse>>

    fun getChallengesList(
        userName: String,
        page: Long
    ): LiveData<ViewResponse<List<ChallengesListResponse>>>

    fun getNextPage(userName: String, page: Long): LiveData<ViewResponse<ChallengesListResponse>>
}