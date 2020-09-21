package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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


    val isNextPageLoadedLiveData by lazy{MutableLiveData<Boolean>()}

    val challengesListLiveDate by lazy { MutableLiveData<ViewResponse<List<ChallengesListResponse>>>() }
    val nextChallengesListPageLiveData by lazy { MutableLiveData<ViewResponse<ChallengesListResponse>>() }

    private lateinit var auxCompletedChallengesList: MutableList<ChallengeResponse>
    private lateinit var auxAuthoredChallengeList: List<ChallengeResponse>
    private var auxListTotalPages: Long = 0
    private var auxNextPage: Long = 1
    private var auxUserName: String =""
    var completedListWithLoading: MutableList<ChallengeResponse> = mutableListOf()

    @Inject
    lateinit var challengeRepository: IChallengeRepository

    init {
        DaggerChallengeRepositoryComponent.create().injectInChallengeListsViewModel(this)
        challengeRepository.setApplicationContext(getApplication())
        completedListWithLoading = mutableListOf()
    }

    private val mapListsObserver = Observer<List<ChallengesListResponse>>{
        val placeHolderChallenge = ChallengeResponse("placeholder")
        auxCompletedChallengesList = completedListWithLoading
        auxAuthoredChallengeList = it[1].data!!
        if(auxNextPage == 1.toLong()){
            completedListWithLoading.clear()
            completedListWithLoading.addAll(it[0].data as MutableList<ChallengeResponse>)
            completedListWithLoading.add(placeHolderChallenge)
        }
        else{
            completedListWithLoading.removeAt(completedListWithLoading.size-1)
            completedListWithLoading.addAll(it[0].data as MutableList<ChallengeResponse>)
            if(auxNextPage <= auxListTotalPages){
                completedListWithLoading.add(placeHolderChallenge)
            }else{
                val placeHolderLastChallenge = ChallengeResponse("lastItem")
                completedListWithLoading.add(placeHolderLastChallenge)
            }
            isNextPageLoadedLiveData.value = true
        }
        auxListTotalPages = it[0].totalPages!!
    }

    fun getNextPage() {
        challengeRepository.getCompletedChallenges(auxUserName, auxNextPage, false)
        auxNextPage++
    }

    fun getChallengesLists(userName: String){
        challengeRepository.getChallengesList(userName, 0).observeForever{
            if (it.status == Status.SUCCESS){
                CoroutineScope(Dispatchers.IO).launch {
                    it.data?.let { challengeLists ->
                        challengeLists[0].id = userName+"completed"
                        challengeLists[0].pageNumber = 0
                        challengeLists[0].type = "completed"
                        UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(challengeLists[0])
                        challengeLists[1].id = userName+"authored"
                        challengeLists[1].pageNumber = 0
                        challengeLists[1].type = "authored"
                        UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(challengeLists[1])
                    }
                }
            }
            challengesListLiveDate.value = it
        }
    }

    fun getNextPage2(userName: String, page: Long){
        challengeRepository.getNextPage(userName, page).observeForever{
            if(it.status == Status.SUCCESS){
                CoroutineScope(Dispatchers.IO).launch {
                    it.data?.let { completeChallengesList ->
                        UserLocalDataBase(getApplication()).challengeDAO().saveChallengesList(completeChallengesList)
                    }
                }
            }
            nextChallengesListPageLiveData.value = it
        }
    }

}