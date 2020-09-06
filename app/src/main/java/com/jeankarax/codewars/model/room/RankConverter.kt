package com.jeankarax.codewars.model.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.jeankarax.codewars.model.response.Rank

class RankConverter {

    @TypeConverter
    fun languageRankToString(rank: Rank?): String {
        return Gson().toJson(rank)
    }

    @TypeConverter
    fun stringToLanguageRanks(string: String):Rank? {
        return Gson().fromJson(string, Rank::class.java)
    }

}