package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jeankarax.codewars.model.di.DaggerUserComponent
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.codewars.model.user.IUserRepository
import javax.inject.Inject

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    val userLiveData by lazy { MutableLiveData<UserResponse>() }
    val errorLiveData by lazy { MutableLiveData<Boolean>() }
    val loading by lazy { MutableLiveData<Boolean>() }

    @Inject
    lateinit var userRepository: IUserRepository

    init{
        DaggerUserComponent.create().inject(this)
    }

    private val mapUserObserver = Observer<UserResponse> {
        loading.value = false
        userLiveData.value = it
    }

    private val mapErrorObserver = Observer<Throwable> {
        loading.value = false
        errorLiveData.value = true
    }

    fun getUser(userName: String){
        userRepository.getUser(userName)
        mapUser()
        mapError()
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
    }


}
