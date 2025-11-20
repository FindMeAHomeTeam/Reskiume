package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteReview

data class Review(
    val timestamp: Long,
    val authorUid: String,
    val reviewedUid: String,
    val description: String,
    val rating: Float
) {
    fun toEntity(): ReviewEntity {
        return ReviewEntity(
            timestamp = timestamp,
            authorUid = authorUid,
            reviewedUid = reviewedUid,
            description = description,
            rating = rating
        )
    }

    fun toData(): RemoteReview {
        return RemoteReview(
            timestamp = timestamp,
            authorUid = authorUid,
            reviewedUid = reviewedUid,
            description = description,
            rating = rating
        )
    }
}
