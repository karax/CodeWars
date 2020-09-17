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
    suspend fun saveChallenge(challenge: ChallengeResponse): Long

    @Query("SELECT * FROM Challenge WHERE id = :queryChallengeId")
    suspend fun getChallenge(queryChallengeId: String): ChallengeResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChallengesList(challengesList: ChallengesListResponse): Long

    @Query("SELECT * FROM ChallengesList WHERE id = :queryChallengesListId AND pageNumber= :queryPage")
    suspend fun getChallengesList(queryChallengesListId: String, queryPage: Long): ChallengesListResponse

}