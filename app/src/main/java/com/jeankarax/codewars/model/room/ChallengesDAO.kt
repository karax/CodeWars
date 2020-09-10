package com.jeankarax.codewars.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeankarax.codewars.model.response.ChallengeResponse

@Dao
interface ChallengesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChallenge(challenge: ChallengeResponse): Long

    @Query("SELECT * FROM Challenge WHERE id = :queryChallengeId")
    fun getChallenge(queryChallengeId: String): ChallengeResponse

}