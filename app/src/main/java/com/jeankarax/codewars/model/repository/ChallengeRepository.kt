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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ChallengeRepository
@Inject
constructor(
    private val challengeAPI: ChallengeAPI
):IChallengeRepository, CoroutineScope {

    lateinit var mApplication: Application

    private val disposable = CompositeDisposable()
    private val allChallenges = MutableLiveData<MutableList<ChallengesListResponse>>()
    private val completedChallenges = MutableLiveData<ChallengesListResponse>()
    private val challenge = MutableLiveData<ChallengeResponse>()
    private val error = MediatorLiveData<Throwable>()
    private var auxAllChallengesList = mutableListOf<ChallengesListResponse>()

    override fun getChallenge(id: String) {
        if(!Utils.isOnline(mApplication)){
            val challengeResponse = UserLocalDataBase(mApplication).challengeDAO().getChallenge(id)
            challenge.postValue(challengeResponse)
        }else{
        disposable.add(challengeAPI.getChallenge(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<ChallengeResponse>(){
                override fun onSuccess(t: ChallengeResponse) {
                    saveChallengeToDataBase(t)
                    challenge.postValue(t)
                }

                override fun onError(e: Throwable) {
                    error.postValue(e)
                }

            }))
        }
    }

    private fun saveChallengeToDataBase(challenge: ChallengeResponse) {
        UserLocalDataBase(mApplication).challengeDAO().saveChallenge(challenge)
    }

    private fun saveChallengesListToDataBase(challengesList: ChallengesListResponse,
                                             userName: String, page: Long, type: String){
        challengesList.id = userName+type
        challengesList.pageNumber = page
        challengesList.type = type
        UserLocalDataBase(mApplication).challengeDAO().saveChallengesList(challengesList)
    }

    override fun getCompletedChallenges(userName: String, page: Long, isFirstCall: Boolean) {
        if(!Utils.isOnline(mApplication)){
            if(isFirstCall) {
                auxAllChallengesList.add(getChallengeListFromDataBase(userName, 0, "completed"))
                getAuthoredChallenges(userName)
            }else{
                auxAllChallengesList.add(getChallengeListFromDataBase(userName, page, "completed"))
                allChallenges.postValue(auxAllChallengesList)
            }
        }else {
            disposable.add(challengeAPI.getCompletedChallenges(userName, page.toInt())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>() {
                    override fun onSuccess(t: ChallengesListResponse) {
                        if (isFirstCall) {
                            saveChallengesListToDataBase(t, userName, 0, "completed")
                            auxAllChallengesList.add(t)
                            getAuthoredChallenges(userName)
                        } else {
                            saveChallengesListToDataBase(t, userName, page, "completed")
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

    private fun getChallengeListFromDataBase(userName: String, page: Long, type: String): ChallengesListResponse {
        val queryUserName = userName + type
        return UserLocalDataBase.invoke(mApplication).challengeDAO().getChallengesList(queryUserName, page)
    }


    override fun getAuthoredChallenges(userName: String) {
        if(!Utils.isOnline(mApplication)){
            auxAllChallengesList.add(getChallengeListFromDataBase(userName, 0, "authored"))
            allChallenges.postValue(auxAllChallengesList)
        }
        disposable.add(challengeAPI.getAuthoredChallenges(userName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>(){
                override fun onSuccess(t: ChallengesListResponse) {
                    saveChallengesListToDataBase(t, userName, 0, "authored")
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

    override fun clearDisposable() {
        disposable.clear()
        job.cancel()
    }

    override fun setApplicationContext(application: Application) {
        mApplication = application
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


}