package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.jeankarax.codewars.model.response.Status
import com.jeankarax.codewars.model.response.ViewResponse
import com.jeankarax.codewars.model.di.DaggerUserRepositoryComponent
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.codewars.model.repository.IUserRepository
import com.jeankarax.codewars.model.room.UserLocalDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    val userLiveData by lazy { MutableLiveData<ViewResponse<UserResponse>>() }
    val userListLiveData by lazy { MutableLiveData<List<UserResponse>>() }
    private var unsortedList = ArrayList<UserResponse>()

    @Inject
    lateinit var userRepository: IUserRepository

    init{
        DaggerUserRepositoryComponent.create().inject(this)
        userRepository.setApplicationContext(getApplication())
    }

    private val mapUserListObserver = Observer<ArrayList<UserResponse>> {
        unsortedList = it
        userListLiveData.value = it
    }

    fun getUser(userName: String){
        userRepository.getUser(userName).observeForever {
            if (it.status == Status.SUCCESS){
                CoroutineScope(IO).launch {
                    it.data?.creationDate = Date()
                    it.data?.let { user -> UserLocalDataBase(getApplication()).userDAO().saveUser(user) }
                }
            }
            userLiveData.value = it
        }
    }

    fun getUsersList(){
        userRepository.getUsersList(5)
        mapUserList()
    }

    fun getSortedUserList(){
        val sortedList: List<UserResponse> = unsortedList.sortedWith(compareBy { it.ranks?.overall?.rank})
        userListLiveData.value = sortedList
    }

    fun getUnsortedUserList() {
        userListLiveData.value = unsortedList
    }

    private fun mapUserList() {
        return userRepository.getUsersListObservable().observeForever(mapUserListObserver)
    }

    override fun onCleared() {
        super.onCleared()
        userRepository.getUsersListObservable().removeObserver(mapUserListObserver)
        userRepository.clearDisposable()
    }

}
