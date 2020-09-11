package com.jeankarax.codewars.model.response

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "Challenge")
data class ChallengeResponse (

    @NonNull
    @PrimaryKey
    val id: String = "",
    val name: String? = null,
    val slug: String? = null,
    val category: String? = null,
    val publishedAt: Date? = null,
    val approvedAt: Date? = null,
    val completedAt: Date? = null,
    val languages: List<String>? = null,
    val url: String? = null,
    val createdAt: String? = null,
    val owner: String? = null,

    val createdBy: UserResponse? = null,

    val approvedBy: UserResponse? = null,
    val description: String? = null,
    val totalAttempts: Long? = null,
    val totalCompleted: Long? = null,
    val totalStars: Long? = null,
    val voteScore: Long? = null,
    val tags: List<String>? = null,
    val contributorsWanted: Boolean? = null,
    val type: String? = null

):Serializable
