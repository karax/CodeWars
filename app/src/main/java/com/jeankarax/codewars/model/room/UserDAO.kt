package com.jeankarax.codewars.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeankarax.codewars.model.response.UserResponse

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: UserResponse): Long

    @Query("SELECT * FROM User WHERE username = :queryUserName")
    fun getUser(queryUserName: String): UserResponse

    @Query("SELECT * FROM User ORDER BY creationDate desc LIMIT :numberOfUsers;")
    fun getLastUsersList(numberOfUsers: Int): List<UserResponse>
}