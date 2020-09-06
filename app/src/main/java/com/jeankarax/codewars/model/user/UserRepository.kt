package com.jeankarax.codewars.model.user

import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.response.UserResponse
import io.reactivex.Single
import javax.inject.Inject

class UserRepository
@Inject
constructor(
    private val userAPI: UserAPI
): IUserRepository
{
    override fun getUserFromAPI(userName: String): Single<UserResponse> {
        return userAPI.getUser(userName)
    }

}