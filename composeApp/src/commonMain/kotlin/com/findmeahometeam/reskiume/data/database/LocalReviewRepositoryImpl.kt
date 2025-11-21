package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalReviewRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
): LocalReviewRepository {
    override suspend fun insertLocalReview(review: Review, onInsertReview: (rowId: Long) -> Unit) {
        onInsertReview(reskiumeDatabase.getReviewDao().insertLocalReview(review.toEntity()))
    }

    override fun getLocalReviews(reviewedUserUid: String): Flow<List<Review>> =
        reskiumeDatabase.getReviewDao().getLocalReviews(reviewedUserUid).map {
            reviewEntities -> reviewEntities.map { it.toDomain() }
        }


    override suspend fun deleteLocalReviews(reviewedUserUid: String, onDeletedReviews: (rowsDeleted: Int) -> Unit) {
        onDeletedReviews(reskiumeDatabase.getReviewDao().deleteLocalReviews(reviewedUserUid))
    }
}
