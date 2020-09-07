package com.jeankarax.codewars.model.response

data class ChallengesListResponse (
    var totalPages: Long? = null,
    var totalItems: Long? = null,
    var data: List<ChallengeResponse>? = null
)
