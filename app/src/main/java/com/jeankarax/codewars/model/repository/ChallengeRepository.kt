package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.api.ChallengeAPI
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.codewars.model.response.ViewResponse
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

class ChallengeRepository
@Inject
constructor(
    private val challengeAPI: ChallengeAPI
):IChallengeRepository {

    lateinit var mApplication: Application

    private val disposable = CompositeDisposable()
    private val allChallenges = MutableLiveData<MutableList<ChallengesListResponse>>()
    private val completedChallenges = MutableLiveData<ChallengesListResponse>()
    private val error = MediatorLiveData<Throwable>()
    private var auxAllChallengesList = mutableListOf<ChallengesListResponse>()

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

    override fun getCompletedChallenges(userName: String, page: Long, isFirstCall: Boolean) {
        if(!Utils.isOnline(mApplication)){
            if(isFirstCall) {
                CoroutineScope(IO).launch {
                    auxAllChallengesList.add(getChallengeListFromDataBase(userName, 0, "completed"))
                    getAuthoredChallenges(userName)
                }
            }else{
                CoroutineScope(IO).launch {
                    auxAllChallengesList.add(getChallengeListFromDataBase(userName, page, "completed"))
                    withContext(Main){
                        allChallenges.postValue(auxAllChallengesList)
                    }
                }
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

    override fun getAuthoredChallenges(userName: String) {
        if(!Utils.isOnline(mApplication)){
            CoroutineScope(IO).launch {
                auxAllChallengesList.add(getChallengeListFromDataBase(userName, 0, "authored"))
                withContext(Main){
                    allChallenges.postValue(auxAllChallengesList)
                }
            }
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

    override fun getCompletedChallengesLiveData(): LiveData<ChallengesListResponse> = completedChallenges

    override fun getAllChallengesLiveData(): LiveData<MutableList<ChallengesListResponse>> = allChallenges

    override fun getErrorLiveData(): LiveData<Throwable> = error

    private fun saveChallengesListToDataBase(challengesList: ChallengesListResponse,
                                                     userName: String, page: Long, type: String){
        CoroutineScope(IO).launch{
            challengesList.id = userName+type
            challengesList.pageNumber = page
            challengesList.type = type
            UserLocalDataBase(mApplication).challengeDAO().saveChallengesList(challengesList)
        }
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