package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

interface LocalReviewRepository {
    suspend fun insertLocalReview(reviewEntity: ReviewEntity, onInsertReview: (rowId: Long) -> Unit)
    fun getLocalReviews(reviewedUid: String): Flow<List<ReviewEntity>>
    suspend fun deleteLocalReviews(reviewedUid: String, onDeletedReviews: (rowsDeleted: Int) -> Unit)
}
