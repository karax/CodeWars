package com.jeankarax.codewars.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.codewars.model.room.UserLocalDataBase
import com.jeankarax.codewars.utils.Utils
import com.jeankarax.livedataretrofitadapterlibrary.ViewResponse
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
    private lateinit var mApplication: Application

    override fun getUser(userName: String): LiveData<ViewResponse<UserResponse>> {
        return if (!Utils.isOnline(mApplication)){
            getUserFromDatabase(userName)
        }else{
            userAPI.getUser(userName)
        }
    }

    override fun getUsersList(limit: Int): LiveData<ViewResponse<ArrayList<UserResponse>>> {
        val userList = MutableLiveData<ViewResponse<ArrayList<UserResponse>>>()
        userList.value = ViewResponse.loading(null)
        CoroutineScope(IO).launch {
            var userListFromDataBase: ArrayList<UserResponse> = UserLocalDataBase(mApplication).userDAO().getLastUsersList(limit) as ArrayList<UserResponse>
            withContext(Main){
                if(null != userListFromDataBase){
                    userList.value  = ViewResponse.success(userListFromDataBase)
                }else{
                    userList.value = ViewResponse.error("User not found", null, null)
                }
            }
        }
        return userList
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

    override fun setApplicationContext(application: Application) {
        mApplication = application
    }

}