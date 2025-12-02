package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteReview

data class Review(
    val id: String = "",
    val savedBy : String,
    val timestamp: Long,
    val authorUid: String,
    val reviewedUid: String,
    val description: String,
    val rating: Float
) {
    fun setId(): String = timestamp.toString() + authorUid

    fun toEntity(): ReviewEntity {
        return ReviewEntity(
            id = id.ifBlank { setId() },
            savedBy = savedBy,
            timestamp = timestamp,
            authorUid = authorUid,
            reviewedUid = reviewedUid,
            description = description,
            rating = rating
        )
    }

    fun toData(): RemoteReview {
        return RemoteReview(
            id = id.ifBlank { setId() },
            timestamp = timestamp,
            authorUid = authorUid,
            reviewedUid = reviewedUid,
            description = description,
            rating = rating
        )
    }
}
