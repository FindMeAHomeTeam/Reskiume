package com.findmeahometeam.reskiume.data.remote.response

import com.findmeahometeam.reskiume.domain.model.Review

data class RemoteReview(
    val id: String? = "",
    val timestamp: Long? = 0L,
    val authorUid: String? = "",
    val reviewedUid: String? = "",
    val description: String? = "",
    val rating: Float? = 0F
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "timestamp" to timestamp,
            "authorUid" to authorUid,
            "reviewedUid" to reviewedUid,
            "description" to description,
            "rating" to rating
        )
    }

    fun toData(): Review {
        return Review(
            id = id ?: "",
            savedBy = "",
            timestamp = timestamp ?: 0L,
            authorUid = authorUid ?: "",
            reviewedUid = reviewedUid ?: "",
            description = description ?: "",
            rating = rating ?: 0f
        )
    }
}
