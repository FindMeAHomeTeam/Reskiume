package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface LocalReviewRepository {
    suspend fun insertLocalReview(review: Review, onInsertReview: (rowId: Long) -> Unit)
    fun getLocalReviews(reviewedUserUid: String): Flow<List<Review>>
    suspend fun deleteLocalReviews(reviewedUserUid: String, onDeletedReviews: (rowsDeleted: Int) -> Unit)
}
