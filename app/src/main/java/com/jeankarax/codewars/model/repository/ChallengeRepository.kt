package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.api.ChallengeAPI
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
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

    @Inject
    lateinit var mApplication: Application

    private val disposable = CompositeDisposable()
    val authoredChallenges = MutableLiveData<ChallengesListResponse>()
    val completedChallenges = MutableLiveData<ChallengesListResponse>()
    val challenge = MutableLiveData<ChallengeResponse>()
    val error = MediatorLiveData<Throwable>()

    override fun getChallenge(id: Int) {
        disposable.add(challengeAPI.getChallenge(id.toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<ChallengeResponse>(){
                override fun onSuccess(t: ChallengeResponse) {
                    challenge.postValue(t)
                }

                override fun onError(e: Throwable) {
                    error.postValue(e)
                }

            }))
    }

    override fun getCompletedChallenges(userName: String, page: Int) {
        disposable.add(challengeAPI.getCompletedChallenges(userName, page)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>(){
                override fun onSuccess(t: ChallengesListResponse) {
                    completedChallenges.postValue(t)
                }

                override fun onError(e: Throwable) {
                    error.postValue(e)
                }

            }))
    }

    override fun getAuthoredChallenges(userName: String) {
        disposable.add(challengeAPI.getAuthoredChallenges(userName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>(){
                override fun onSuccess(t: ChallengesListResponse) {
                    authoredChallenges.postValue(t)
                }

                override fun onError(e: Throwable) {
                    error.postValue(e)
                }

            }))
    }

    override fun getErrorObservable(): LiveData<Throwable> = error

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