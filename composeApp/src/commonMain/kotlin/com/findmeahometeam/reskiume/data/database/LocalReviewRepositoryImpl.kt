package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalReviewRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
): LocalReviewRepository {
    override suspend fun insertUserReview(review: Review, onInsertReview: (rowId: Long) -> Unit) {
        onInsertReview(reskiumeDatabase.getReviewDao().insertUserReview(review.toEntity()))
    }

    override fun getUserReviews(reviewedUserUid: String): Flow<List<Review>> =
        reskiumeDatabase.getReviewDao().getUserReviews(reviewedUserUid).map {
            reviewEntities -> reviewEntities.map { it.toDomain() }
        }


    override suspend fun deleteUserReviews(reviewedUserUid: String, onDeletedReviews: (rowsDeleted: Int) -> Unit) {
        onDeletedReviews(reskiumeDatabase.getReviewDao().deleteUserReviews(reviewedUserUid))
    }
}
