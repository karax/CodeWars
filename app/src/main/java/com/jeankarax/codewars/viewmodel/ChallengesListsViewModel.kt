package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jeankarax.codewars.model.di.DaggerChallengeRepositoryComponent
import com.jeankarax.codewars.model.repository.IChallengeRepository
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.codewars.model.response.Status
import com.jeankarax.codewars.model.response.ViewResponse
import com.jeankarax.codewars.model.room.UserLocalDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChallengesListsViewModel(application: Application): AndroidViewModel(application) {


    private var auxNextPage: Long = 0
    private var auxUserName: String =""

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectInChallengeListsViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
    }

    fun getChallengesLists(userName: String): LiveData<ViewResponse<List<ChallengesListResponse>>> {
        return Transformations.switchMap(challengeRepository.getChallengesList(userName, 0)){ repositoryResponse ->
            object: LiveData<ViewResponse<List<ChallengesListResponse>>>(){
                override fun onActive() {
                    super.onActive()
                    auxUserName = userName
                    when(repositoryResponse.status){
                        Status.SUCCESS -> {
                            auxNextPage++
                            repositoryResponse.data?.get(0)!!.id = auxUserName+"completed"
                            repositoryResponse.data[0].pageNumber = 0
                            repositoryResponse.data[0].type = "completed"

                            repositoryResponse.data[1].id = auxUserName+"authored"
                            repositoryResponse.data[1].pageNumber = 0
                            repositoryResponse.data[1].type = "authored"
                            CoroutineScope(Dispatchers.IO).launch {
                                UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(repositoryResponse.data[0])
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(repositoryResponse.data[1])
                            }
                        }
                    }
                    value = repositoryResponse
                }
            }
        }
    }

    fun getNextPage(): LiveData<ViewResponse<ChallengesListResponse>>{
        return Transformations.switchMap(challengeRepository.getNextPage(auxUserName, auxNextPage)){ repositoryResponse ->
            object: LiveData<ViewResponse<ChallengesListResponse>>(){
                override fun onActive() {
                    super.onActive()
                    if (repositoryResponse.status == Status.SUCCESS){
                        repositoryResponse.data?.id = auxUserName+"completed"
                        repositoryResponse.data?.pageNumber = auxNextPage
                        repositoryResponse.data?.type = "completed"
                        CoroutineScope(Dispatchers.IO).launch {
                            repositoryResponse.data?.let { completeChallengesList ->
                                UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(completeChallengesList)
                            }
                        }
                        auxNextPage++
                    }
                    value = repositoryResponse
                }
            }
        }
    }

}