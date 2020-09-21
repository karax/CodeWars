package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.response.ViewResponse
import com.jeankarax.codewars.model.response.UserResponse

interface IUserRepository {

    fun setApplicationContext(application: Application)

    fun getUser(userName: String): LiveData<ViewResponse<UserResponse>>

    fun getUsersList(limit: Int): MutableLiveData<ViewResponse<ArrayList<UserResponse>>>
}