package com.jeankarax.codewars.model.response

import java.util.*

class ChallengeResponse (

    var id: String = "",
    var name: String? = null,
    var slug: String? = null,
    var category: String? = null,
    var publishedAt: Date? = null,
    var approvedAt: Date? = null,
    var completedAt: Date? = null,
    var languages: List<String>? = null,
    var url: String? = null,
    var createdAt: String? = null,
    var owner: String? = null,
    var createdBy: UserResponse? = null,

    var approvedBy: UserResponse? = null,

    var description: String? = null,
    var totalAttempts: Long? = null,
    var totalCompleted: Long? = null,
    var totalStars: Long? = null,
    var voteScore: Long? = null,
    var tags: List<String>? = null,
    var contributorsWanted: Boolean? = null,
    var type: String? = null

)
