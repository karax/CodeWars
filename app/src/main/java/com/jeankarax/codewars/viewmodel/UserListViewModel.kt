package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jeankarax.codewars.model.di.DaggerUserRepositoryComponent
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.codewars.model.repository.IUserRepository
import com.jeankarax.codewars.model.room.UserLocalDataBase
import com.jeankarax.livedataretrofitadapterlibrary.Status
import com.jeankarax.livedataretrofitadapterlibrary.ViewResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var userRepository: IUserRepository

    init{
        DaggerUserRepositoryComponent.create().inject(this)
        userRepository.setApplicationContext(getApplication())
    }

    fun getUser(userName: String): LiveData<ViewResponse<UserResponse>>{
        return Transformations.switchMap(userRepository.getUser(userName)){ response ->
            object: LiveData<ViewResponse<UserResponse>>(){
                override fun onActive() {
                    super.onActive()
                    if (response.status == Status.SUCCESS){
                        CoroutineScope(IO).launch {
                            response.data?.creationDate = Date()
                            response.data?.let { user -> UserLocalDataBase(getApplication()).userDAO().saveUser(user) }
                        }
                    }
                    value = response
                }
            }
        }
    }

    fun getUsersList(isSorted: Boolean):LiveData<ViewResponse<List<UserResponse>>> {
        return Transformations.switchMap(userRepository.getUsersList(5)) { response ->
            object : LiveData<ViewResponse<List<UserResponse>>>() {
                override fun onActive() {
                    super.onActive()
                    value = if (isSorted){
                        ViewResponse.success(response.data?.sortedWith(compareBy { it.ranks?.overall?.rank }))
                    }else {
                        response
                    }
                }
            }
        }
    }

}
