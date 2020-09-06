package com.jeankarax.codewars.model.response

import android.graphics.Color
import java.io.Serializable

data class UserResponse (

    val username: String = "",

    val name: String? = null,

    val honor: Long? = null,

    val clan: String? = null,

    val leaderboardPosition: Long? = null,

    val skills: List<String>? = null

    //val ranks: Ranks? = null,

    //val codeChallenges: CodeChallenges? = null
)

data class CodeChallenges (
    val totalAuthored: Long? = null,
    val totalCompleted: Long? = null
): Serializable

data class Ranks (
    val overall: Rank? = null,
    val languages: Map<String, Rank>? = null
): Serializable

data class Rank (
    val rank: Long? = null,
    val name: String? = null,
    val color: Color? = null,
    val score: Long? = null
): Serializable
