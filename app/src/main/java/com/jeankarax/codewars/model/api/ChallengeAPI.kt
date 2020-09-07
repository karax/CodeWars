package com.jeankarax.codewars.model.api

import javax.inject.Inject

class ChallengeAPI
@Inject
constructor(
    private val apiCalls: APICalls
){

    fun getCompletedChallenges(userName: String, page: Int) = apiCalls.getCompletedChallenges(userName, page)

    fun getAuthoredChallenges(userName: String) = apiCalls.getAuthoredChallenges(userName)

    fun getChallenge(id: String) = apiCalls.getChallenge(id)

}