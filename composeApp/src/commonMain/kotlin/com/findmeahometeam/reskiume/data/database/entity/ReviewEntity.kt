package com.findmeahometeam.reskiume.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.Review

@Entity
data class ReviewEntity(
    @PrimaryKey val id: String,
    val savedBy : String = "",
    val timestamp: Long,
    val authorUid: String,
    val reviewedUid: String,
    val description: String,
    val rating: Float
) {

    fun toDomain(): Review {
        return Review(
            id = id,
            savedBy = savedBy,
            timestamp = timestamp,
            authorUid = authorUid,
            reviewedUid = reviewedUid,
            description = description,
            rating = rating
        )
    }
}
