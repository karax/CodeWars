package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.response.ViewResponse
import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.codewars.model.room.UserLocalDataBase
import com.jeankarax.codewars.utils.Utils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserRepository
@Inject
constructor(
    private val userAPI: UserAPI
): IUserRepository
{
    lateinit var mApplication: Application
    private val disposable = CompositeDisposable()
    private val user = MediatorLiveData<UserResponse>()
    private val error = MediatorLiveData<Throwable>()
    private val userList = MediatorLiveData<ArrayList<UserResponse>>()
    private val isEmptyList = MediatorLiveData<Boolean>()

    override fun getUsersList(limit: Int) {
        CoroutineScope(IO).launch {
            var userListFromDataBase: ArrayList<UserResponse> = getLastUsers(limit) as ArrayList<UserResponse>
            withContext(Main){
                if(null != userListFromDataBase){
                    userList.postValue(userListFromDataBase)
                }else{
                    isEmptyList.postValue(true)
                }
            }
        }

    }

    override fun getUsersListObservable(): LiveData<ArrayList<UserResponse>> {
        return userList
    }

    override fun getUser(userName: String): LiveData<ViewResponse<UserResponse>> {
        return if (!Utils.isOnline(mApplication)){
            getUserFromDatabase(userName)
        }else{
            userAPI.getUser(userName)
        }
    }

    override fun getUserObservable(): LiveData<UserResponse> {
        return user
    }

    override fun getErrorObservable(): LiveData<Throwable> {
        return error
    }

    private fun getUserFromDatabase(userName: String): LiveData<ViewResponse<UserResponse>>{
        val user = MutableLiveData<ViewResponse<UserResponse>>()
        user.value = ViewResponse.loading(null)
        var dataBaseResponse: UserResponse
        CoroutineScope(IO).launch {
            dataBaseResponse = UserLocalDataBase(mApplication).userDAO().getUser(userName)
            withContext(Main){
                if (null != dataBaseResponse){
                    user.value = ViewResponse.success(dataBaseResponse)
                }else{
                    user.value = ViewResponse.error("User not found", dataBaseResponse, null)
                }
            }
        }
        return user
    }

    private suspend fun getLastUsers(limit: Int):List<UserResponse>{
        return UserLocalDataBase(mApplication).userDAO().getLastUsersList(limit)
    }

    override fun clearDisposable(){
        disposable.clear()
    }

    override fun setApplicationContext(application: Application) {
        mApplication = application
    }

}