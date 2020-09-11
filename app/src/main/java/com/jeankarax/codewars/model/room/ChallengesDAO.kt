package com.jeankarax.codewars.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse

@Dao
interface ChallengesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChallenge(challenge: ChallengeResponse): Long

    @Query("SELECT * FROM Challenge WHERE id = :queryChallengeId")
    fun getChallenge(queryChallengeId: String): ChallengeResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChallengesList(challengesList: ChallengesListResponse): Long

    @Query("SELECT * FROM ChallengesList WHERE id = :queryChallengesListId AND pageNumber= :queryPage")
    fun getChallengesList(queryChallengesListId: String, queryPage: Long): ChallengesListResponse

}