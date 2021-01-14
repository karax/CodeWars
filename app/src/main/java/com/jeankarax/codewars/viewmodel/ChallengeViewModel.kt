package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.jeankarax.codewars.model.di.DaggerChallengeRepositoryComponent
import com.jeankarax.codewars.model.repository.IChallengeRepository
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.room.UserLocalDataBase
import com.jeankarax.livedataretrofitadapterlibrary.Status
import com.jeankarax.livedataretrofitadapterlibrary.ViewResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChallengeViewModel(application: Application): AndroidViewModel(application) {

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectChallengeViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
    }

    fun getChallenge(challengeId: String): LiveData<ViewResponse<ChallengeResponse>> {
        return Transformations.switchMap(challengeRepository.getChallenge(challengeId)) { response ->
            object : LiveData<ViewResponse<ChallengeResponse>>() {
                override fun onActive() {
                    super.onActive()
                    if(response.status == Status.SUCCESS){
                        CoroutineScope(IO).launch {
                            response.data?.let {challenge -> UserLocalDataBase(getApplication()).challengeDAO().saveChallenge(challenge) }
                        }
                    }
                    value = response
                }
            }
        }
    }

}