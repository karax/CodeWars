package com.jeankarax.codewars.model.user

import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.UserResponse
import io.reactivex.Single

interface IUserRepository {

    fun getUserFromAPI(userName: String): Single<UserResponse>

    fun getUser(userName: String)

    fun getUserObservable(): LiveData<UserResponse>

    fun getErrorObservable(): LiveData<Throwable>

}