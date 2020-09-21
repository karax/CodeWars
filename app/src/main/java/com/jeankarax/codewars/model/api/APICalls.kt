package com.jeankarax.codewars.model.api

import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.BaseApiResponse
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.codewars.model.response.UserResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APICalls {

    @GET("users/{username}")
    fun getUser(@Path("username") username: String): LiveData<BaseApiResponse<UserResponse>>

    @GET("users/{username}/code-challenges/completed")
    fun getCompletedChallenges(
        @Path("username") username: String,
        @Query("page") page: Int
    ): Single<ChallengesListResponse>

    @GET("users/{username}/code-challenges/authored")
    fun getAuthoredChallenges(@Path("username") username: String): Single<ChallengesListResponse>

    @GET("code-challenges/{id}")
    fun getChallenge(@Path("id") id: String?): LiveData<BaseApiResponse<ChallengeResponse>>

}