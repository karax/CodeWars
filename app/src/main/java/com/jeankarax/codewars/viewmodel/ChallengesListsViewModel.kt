package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.di.DaggerChallengeRepositoryComponent
import com.jeankarax.codewars.model.repository.IChallengeRepository
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.codewars.model.response.Status
import com.jeankarax.codewars.model.response.ViewResponse
import com.jeankarax.codewars.model.room.UserLocalDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class ChallengesListsViewModel(application: Application): AndroidViewModel(application) {


    val challengesListLiveDate by lazy { MutableLiveData<ViewResponse<List<ChallengesListResponse>>>() }
    val nextChallengesListPageLiveData by lazy { MutableLiveData<ViewResponse<Boolean>>() }
    private var auxNextPage: Long = 0
    private var auxUserName: String =""
    var auxCompletedList: ChallengesListResponse = ChallengesListResponse()

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectInChallengeListsViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
    }

    fun getChallengesLists(userName: String){
        auxUserName = userName
        challengeRepository.getChallengesList(userName, 0).observeForever{
            if (it.status == Status.SUCCESS){
                auxNextPage++
                it.data?.get(0)!!.id = auxUserName+"completed"
                it.data[0].pageNumber = 0
                it.data[0].type = "completed"
                it.data?.get(1)!!.id = auxUserName+"authored"
                it.data[1].pageNumber = 0
                it.data[1].type = "authored"
                CoroutineScope(Dispatchers.IO).launch {
                    UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(it.data[0])
                }
                CoroutineScope(Dispatchers.IO).launch {
                    UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(it.data[1])
                }
            }
            challengesListLiveDate.value = it
        }
    }

    fun getNextPage(){
        challengeRepository.getNextPage(auxUserName, auxNextPage).observeOnce(Observer {
            when(it.status){
                Status.SUCCESS -> {
                    auxNextPage++
                    it.data?.id = auxUserName+"completed"
                    it.data?.pageNumber = 0
                    it.data?.type = "completed"
                    CoroutineScope(Dispatchers.IO).launch {
                        it.data?.let { completeChallengesList ->
                            UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(completeChallengesList)
                        }
                    }
                    auxCompletedList = it.data!!
                    nextChallengesListPageLiveData.value = ViewResponse.success(true)
                }
                Status.LOADING -> {nextChallengesListPageLiveData.value = ViewResponse.loading(true)}
                Status.ERROR ->{nextChallengesListPageLiveData.value = ViewResponse.error("unknow error", true, null)}
            }
        })
    }

    private fun <T> LiveData<T>.observeOnce(observer: Observer<T>){
        observeForever(object: Observer<T>{
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

}