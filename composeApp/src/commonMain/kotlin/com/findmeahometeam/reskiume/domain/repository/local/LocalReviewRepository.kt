package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface LocalReviewRepository {
    suspend fun insertUserReview(review: Review, onInsertReview: (rowId: Long) -> Unit)
    fun getUserReviews(reviewedUserUid: String): Flow<List<Review>>
    suspend fun deleteUserReviews(reviewedUserUid: String, onDeletedReviews: (rowsDeleted: Int) -> Unit)
}
