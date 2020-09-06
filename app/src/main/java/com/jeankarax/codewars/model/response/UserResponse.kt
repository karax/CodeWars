package com.jeankarax.codewars.model.response

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "User")
data class UserResponse (

    @NonNull
    @PrimaryKey
    val username: String = "",

    val name: String? = null,

    val honor: Long? = null,

    val clan: String? = null,

    val leaderboardPosition: Long? = null,

    val skills: List<String>? = null,

    @Embedded(prefix = "ranks_")
    val ranks: Ranks? = null,

    @Embedded
    val codeChallenges: CodeChallenges? = null,

    var creationDate: Date? = null
)

data class CodeChallenges (
    val totalAuthored: Long? = null,
    val totalCompleted: Long? = null
):Serializable

data class Ranks (
    val overall: Rank? = null,
    val languages: Map<String, Rank>? = null
):Serializable

data class Rank (
    val rank: Long? = null,
    val name: String? = null,
    val color: String? = null,
    val score: Long? = null
):Serializable


