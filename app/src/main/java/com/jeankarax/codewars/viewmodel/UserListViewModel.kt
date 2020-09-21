package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.*
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

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    val userLiveData by lazy { MutableLiveData<ViewResponse<UserResponse>>() }
    val userListLiveData by lazy { MutableLiveData<ViewResponse<List<UserResponse>>>()}


    @Inject
    lateinit var userRepository: IUserRepository

    init{
        DaggerUserRepositoryComponent.create().inject(this)
        userRepository.setApplicationContext(getApplication())
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
        userRepository.getUsersList(5).observeForever{
            userListLiveData.value = it
        }
    }

    fun getSortedUserList(){
        val sortedList = ViewResponse.success(userListLiveData.value?.data?.sortedWith(compareBy { it.ranks?.overall?.rank }))
        userListLiveData.value = sortedList
    }

}
