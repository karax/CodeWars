package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.codewars.model.room.UserLocalDataBase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class UserRepository
@Inject
constructor(
    private val userAPI: UserAPI
): IUserRepository, CoroutineScope
{
    @Inject
    lateinit var mApplication: Application

    private val disposable = CompositeDisposable()
    private val user = MediatorLiveData<UserResponse>()
    private val error = MediatorLiveData<Throwable>()
    private val userList = MediatorLiveData<ArrayList<UserResponse>>()
    private val isEmptyList = MediatorLiveData<Boolean>()

    override fun getUser(userName: String) {
        val userFromDataBase: UserResponse?
        userFromDataBase = getUserFromDataBase(userName)
        if(null != userFromDataBase){
            saveUserToDataBase(userFromDataBase)
            user.postValue(userFromDataBase)
        }else{
            disposable.add(userAPI.getUser(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<UserResponse>(){
                    override fun onSuccess(t: UserResponse) {
                        saveUserToDataBase(t)
                        user.postValue(t)
                    }
                    override fun onError(e: Throwable) {
                        error.postValue(e)
                    }

                })
            )
        }
    }

    override fun getUsersList(limit: Int) {
        var userListFromDataBase: ArrayList<UserResponse>
        userListFromDataBase = getLastUsers(limit) as ArrayList<UserResponse>
        if(null != userListFromDataBase){
            userList.postValue(userListFromDataBase)
        }else{
            isEmptyList.postValue(true)
        }
    }

    override fun getUsersListObservable(): LiveData<ArrayList<UserResponse>> {
        return userList
    }

    override fun getUserObservable(): LiveData<UserResponse> {
        return user
    }

    override fun getErrorObservable(): LiveData<Throwable> {
        return error
    }



    private fun saveUserToDataBase(user: UserResponse){
        launch {
            user.creationDate = Date()
            UserLocalDataBase(mApplication).userDAO().saveUser(user)
        }
    }

    private fun getUserFromDataBase(userName: String): UserResponse{
        return UserLocalDataBase(mApplication).userDAO().getUser(userName)
    }

    private fun getLastUsers(limit: Int):List<UserResponse>{
        return UserLocalDataBase(mApplication).userDAO().getLastUsersList(limit)
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun clearDisposable(){
        disposable.clear()
        job.cancel()
    }

    override fun setApplicationContext(application: Application) {
        mApplication = application
    }

}