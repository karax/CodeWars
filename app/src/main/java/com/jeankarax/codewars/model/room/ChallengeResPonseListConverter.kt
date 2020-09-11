package com.jeankarax.codewars.model.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeankarax.codewars.model.response.ChallengeResponse

class ChallengeResponseListConverter {

    @TypeConverter
    fun listChallengeResponseToString(list: List<ChallengeResponse>?): String {
        val type = object : TypeToken<List<ChallengeResponse?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun stringToChallengeResponseList(string: String): List<ChallengeResponse>? {
        val type = object : TypeToken<List<ChallengeResponse?>?>() {}.type
        return Gson().fromJson(string, type)
    }

}