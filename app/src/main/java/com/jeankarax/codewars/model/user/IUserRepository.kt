package com.jeankarax.codewars.model.user

import com.jeankarax.codewars.model.response.UserResponse
import io.reactivex.Single

interface IUserRepository {

    fun getUserFromAPI(userName: String): Single<UserResponse>

}