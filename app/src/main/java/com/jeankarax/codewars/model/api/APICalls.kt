package com.jeankarax.codewars.model.api

import com.jeankarax.codewars.model.response.UserResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface APICalls {

    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Single<UserResponse>

}