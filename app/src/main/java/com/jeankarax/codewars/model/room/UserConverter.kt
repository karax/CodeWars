package com.jeankarax.codewars.model.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.jeankarax.codewars.model.response.Rank
import com.jeankarax.codewars.model.response.UserResponse

class UserConverter {

    @TypeConverter
    fun userToString(user: UserResponse?): String {
        return Gson().toJson(user)
    }

    @TypeConverter
    fun stringToLanguageRanks(string: String): UserResponse? {
        return Gson().fromJson(string, UserResponse::class.java)
    }

}