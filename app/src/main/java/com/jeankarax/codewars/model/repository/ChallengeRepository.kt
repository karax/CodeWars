package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.api.ChallengeAPI
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.codewars.model.room.UserLocalDataBase
import com.jeankarax.codewars.utils.Utils
import com.jeankarax.livedataretrofitadapterlibrary.ViewResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

class ChallengeRepository
@Inject
constructor(
    private val challengeAPI: ChallengeAPI
):IChallengeRepository {

    lateinit var mApplication: Application

    override fun getChallenge(id: String): LiveData<ViewResponse<ChallengeResponse>>{
        return if (!Utils.isOnline(mApplication)){
            getChallengeFromDataBase(id)
        }else{
            challengeAPI.getChallenge(id)
        }
    }

    private fun getChallengeFromDataBase(id: String): LiveData<ViewResponse<ChallengeResponse>> {
        val challenge = MutableLiveData<ViewResponse<ChallengeResponse>>()
        challenge.value = ViewResponse.loading(null)
        var dataBaseResponse: ChallengeResponse
        CoroutineScope(IO).launch {
            dataBaseResponse = UserLocalDataBase(mApplication).challengeDAO().getChallenge(id)
            withContext(Main){
                if(null != dataBaseResponse){
                    challenge.value = ViewResponse.success(dataBaseResponse)
                }else{
                    challenge.value = ViewResponse.error("Challenge not found", dataBaseResponse, null)
                }
            }
        }
        return challenge
    }

    override fun getChallengesList(userName: String, page: Long): LiveData<ViewResponse<List<ChallengesListResponse>>>{
        return challengeAPI.getChallengesList(userName, page)
    }

    override fun getNextPage(userName: String, page: Long): LiveData<ViewResponse<ChallengesListResponse>> {
        return challengeAPI.getNextPage(userName, page)
    }

    private suspend fun getChallengeListFromDataBase(userName: String, page: Long, type: String): ChallengesListResponse {
        val queryUserName = userName + type
        return UserLocalDataBase.invoke(mApplication).challengeDAO().getChallengesList(queryUserName, page)
    }

    override fun setApplicationContext(application: Application) {
        mApplication = application
    }
}