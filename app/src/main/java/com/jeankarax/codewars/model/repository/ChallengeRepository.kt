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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ChallengeRepository
@Inject
constructor(
    private val challengeAPI: ChallengeAPI
):IChallengeRepository {

    lateinit var mApplication: Application

    private val disposable = CompositeDisposable()
    private val allChallenges = MutableLiveData<MutableList<ChallengesListResponse>>()
    private val completedChallenges = MutableLiveData<ChallengesListResponse>()
    private val challenge = MutableLiveData<ChallengeResponse>()
    private val error = MediatorLiveData<Throwable>()
    private var auxAllChallengesList = mutableListOf<ChallengesListResponse>()

    override fun getChallenge(id: String) {
        if(!Utils.isOnline(mApplication)){
            var challengeResponse: ChallengeResponse
            CoroutineScope(IO).launch {
                challengeResponse = UserLocalDataBase(mApplication).challengeDAO().getChallenge(id)
                withContext(Main){
                    challenge.postValue(challengeResponse)

                }
            }
        }else{
        disposable.add(challengeAPI.getChallenge(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<ChallengeResponse>(){
                override fun onSuccess(t: ChallengeResponse) {
                    CoroutineScope(IO).launch {
                        saveChallengeToDataBase(t)
                    }
                    challenge.postValue(t)
                }

                override fun onError(e: Throwable) {
                    error.postValue(e)
                }

            }))
        }
    }

    override fun getCompletedChallenges(userName: String, page: Long, isFirstCall: Boolean) {
        if(!Utils.isOnline(mApplication)){
            if(isFirstCall) {
                CoroutineScope(IO).launch {
                    auxAllChallengesList.add(getChallengeListFromDataBase(userName, 0, "completed"))
                }
                getAuthoredChallenges(userName)
            }else{
                CoroutineScope(IO).launch {
                    auxAllChallengesList.add(getChallengeListFromDataBase(userName, page, "completed"))
                }
                allChallenges.postValue(auxAllChallengesList)
            }
        }else {
            disposable.add(challengeAPI.getCompletedChallenges(userName, page.toInt())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>() {
                    override fun onSuccess(t: ChallengesListResponse) {
                        if (isFirstCall) {
                            CoroutineScope(IO).launch{
                                saveChallengesListToDataBase(t, userName, 0, "completed")
                            }
                            auxAllChallengesList.add(t)
                            getAuthoredChallenges(userName)
                        } else {
                            CoroutineScope(IO).launch {
                                saveChallengesListToDataBase(t, userName, page, "completed")
                            }
                            auxAllChallengesList[0] = t
                            allChallenges.postValue(auxAllChallengesList)
                        }
                    }

                    override fun onError(e: Throwable) {
                        error.postValue(e)
                    }

                })
            )
        }
    }

    override fun getAuthoredChallenges(userName: String) {
        if(!Utils.isOnline(mApplication)){
            CoroutineScope(IO).launch {
                auxAllChallengesList.add(getChallengeListFromDataBase(userName, 0, "authored"))
            }
            allChallenges.postValue(auxAllChallengesList)
        }
        disposable.add(challengeAPI.getAuthoredChallenges(userName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>(){
                override fun onSuccess(t: ChallengesListResponse) {
                    CoroutineScope(IO).launch {
                        saveChallengesListToDataBase(t, userName, 0, "authored")
                    }
                    auxAllChallengesList.add(t)
                    allChallenges.postValue(auxAllChallengesList)
                }

                override fun onError(e: Throwable) {
                    error.postValue(e)
                }

            }))
    }

    override fun getChallengeLiveData(): LiveData<ChallengeResponse> = challenge

    override fun getCompletedChallengesLiveData(): LiveData<ChallengesListResponse> = completedChallenges

    override fun getAllChallengesLiveData(): LiveData<MutableList<ChallengesListResponse>> = allChallenges

    override fun getErrorLiveData(): LiveData<Throwable> = error

    private suspend fun saveChallengeToDataBase(challenge: ChallengeResponse) {
        UserLocalDataBase(mApplication).challengeDAO().saveChallenge(challenge)
    }

    private suspend fun saveChallengesListToDataBase(challengesList: ChallengesListResponse,
                                                     userName: String, page: Long, type: String){
        challengesList.id = userName+type
        challengesList.pageNumber = page
        challengesList.type = type
        UserLocalDataBase(mApplication).challengeDAO().saveChallengesList(challengesList)
    }

    private suspend fun getChallengeListFromDataBase(userName: String, page: Long, type: String): ChallengesListResponse {
        val queryUserName = userName + type
        return UserLocalDataBase.invoke(mApplication).challengeDAO().getChallengesList(queryUserName, page)
    }

    override fun clearDisposable() {
        disposable.clear()
    }

    override fun setApplicationContext(application: Application) {
        mApplication = application
    }
}