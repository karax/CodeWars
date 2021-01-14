package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.livedataretrofitadapterlibrary.ViewResponse

interface IUserRepository {

    fun setApplicationContext(application: Application)

    fun getUser(userName: String): LiveData<ViewResponse<UserResponse>>

    fun getUsersList(limit: Int): LiveData<ViewResponse<ArrayList<UserResponse>>>
}