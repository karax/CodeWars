package com.jeankarax.codewars.model.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.jeankarax.codewars.model.response.Rank
import com.jeankarax.codewars.model.response.Ranks

class LanguageRankConverter {

    @TypeConverter
    fun languageRankToString(languages: Map<String, Rank>?): String {
        return Gson().toJson(Ranks(languages = languages))
    }

    @TypeConverter
    fun stringToLanguageRanks(string: String): Map<String, Rank>? {
        return Gson().fromJson(string, Ranks::class.java).languages
    }
}