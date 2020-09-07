package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jeankarax.codewars.model.di.DaggerUserComponent
import com.jeankarax.codewars.model.di.DaggerUserRepositoryComponent
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.codewars.model.repository.IUserRepository
import javax.inject.Inject

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    val userLiveData by lazy { MutableLiveData<UserResponse>() }
    val userListLiveData by lazy { MutableLiveData<List<UserResponse>>() }
    val errorLiveData by lazy { MutableLiveData<Boolean>() }
    val loading by lazy { MutableLiveData<Boolean>() }
    private var unsortedList = ArrayList<UserResponse>()

    @Inject
    lateinit var userRepository: IUserRepository

    init{
        DaggerUserComponent.create().inject(this)
        DaggerUserRepositoryComponent.builder()
            .build()
            .inject(application)
        userRepository.setApplicationContext(getApplication())
    }

    private val mapUserObserver = Observer<UserResponse> {
        loading.value = false
        userLiveData.value = it
    }

    private val mapErrorObserver = Observer<Throwable> {
        loading.value = false
        errorLiveData.value = true
    }

    private val mapUserListObserver = Observer<ArrayList<UserResponse>> {
        loading.value = false
        unsortedList = it
        userListLiveData.value = it
    }

    fun getUser(userName: String){
        loading.value = true
        userRepository.getUser(userName)
        mapUser()
        mapError()
    }

    fun getUsersList(){
        loading.value = true
        userRepository.getUsersList(5)
        mapUserList()
        mapError()
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

    private fun mapUser() {
        return userRepository.getUserObservable().observeForever(mapUserObserver)
    }

    private fun mapError(){
        return userRepository.getErrorObservable().observeForever(mapErrorObserver)
    }

    override fun onCleared() {
        super.onCleared()
        userRepository.getUserObservable().removeObserver(mapUserObserver)
        userRepository.getErrorObservable().removeObserver(mapErrorObserver)
        userRepository.clearDisposable()
    }

}
