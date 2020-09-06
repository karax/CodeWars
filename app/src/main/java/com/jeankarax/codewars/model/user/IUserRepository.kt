package com.jeankarax.codewars.model.user

import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.UserResponse

interface IUserRepository {

    fun getUser(userName: String)

    fun getUserObservable(): LiveData<UserResponse>

    fun getErrorObservable(): LiveData<Throwable>

    fun clearDisposable()

}