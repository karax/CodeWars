package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.di.DaggerChallengeRepositoryComponent
import com.jeankarax.codewars.model.repository.IChallengeRepository
import com.jeankarax.codewars.model.response.ChallengeResponse
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class ChallengeViewModel(application: Application): AndroidViewModel(application) {

    val challengeLiveData by lazy { MutableLiveData<ChallengeResponse>() }
    val isError by lazy { MutableLiveData<String>() }
    val isLoading by lazy { MutableLiveData<Boolean>() }

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectChallengeViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
    }

    private val mapChallengeObserver = Observer<ChallengeResponse>{
        challengeLiveData.value = it
        isLoading.value = false
    }

    private val mapErrorObserver = Observer<Throwable> {
        if(it is HttpException){
            isError.value = application.getString(R.string.text_error_challenge_not_found)
        }else if (it is UnknownHostException){
            isError.value = application.getString(R.string.text_connection_error)
            isLoading.value = false
        }
    }


    fun getChallenge(challengeId: String){
        isLoading.value = true
        challengeRepository.getChallenge(challengeId)
        mapChallenge()
        mapError()
    }

    private fun mapChallenge() {
        return challengeRepository.getChallengeLiveData().observeForever(mapChallengeObserver)
    }

    private fun mapError(){
        return challengeRepository.getErrorLiveData().observeForever(mapErrorObserver)
    }

}