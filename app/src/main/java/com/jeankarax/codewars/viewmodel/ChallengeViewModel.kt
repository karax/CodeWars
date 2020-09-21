package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.di.DaggerChallengeRepositoryComponent
import com.jeankarax.codewars.model.repository.IChallengeRepository
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.Status
import com.jeankarax.codewars.model.response.ViewResponse
import com.jeankarax.codewars.model.room.UserLocalDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChallengeViewModel(application: Application): AndroidViewModel(application) {

    val challengeLiveData by lazy { MutableLiveData<ViewResponse<ChallengeResponse>>() }

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectChallengeViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
    }

    fun getChallenge(challengeId: String){
        challengeRepository.getChallenge(challengeId).observeForever{
            if(it.status == Status.SUCCESS){
                CoroutineScope(IO).launch {
                    it.data?.let {challenge -> UserLocalDataBase(getApplication()).challengeDAO().saveChallenge(challenge) }
                }
            }
            challengeLiveData.value = it
        }
    }

}