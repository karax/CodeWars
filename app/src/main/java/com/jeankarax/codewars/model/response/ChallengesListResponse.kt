package com.jeankarax.codewars.model.response

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChallengesList")
data class ChallengesListResponse (

    @NonNull
    @PrimaryKey
    var id: String = "",
    var userName: String = "",
    var pageNumber: Long? = 0,
    var type: String? = "",
    var totalPages: Long? = null,
    var totalItems: Long? = null,
    var data: List<ChallengeResponse>? = null
)
