package com.jeankarax.codewars.model.api

import javax.inject.Inject

class UserAPI
@Inject
constructor(
    private val apiCalls: APICalls
)
{

    fun getUser(userName: String) = apiCalls.getUser(userName)

}
