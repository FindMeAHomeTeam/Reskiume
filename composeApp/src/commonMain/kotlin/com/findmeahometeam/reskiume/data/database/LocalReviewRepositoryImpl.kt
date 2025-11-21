package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import kotlinx.coroutines.flow.Flow

class LocalReviewRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
): LocalReviewRepository {
    override suspend fun insertLocalReview(reviewEntity: ReviewEntity, onInsertReview: (rowId: Long) -> Unit) {
        onInsertReview(reskiumeDatabase.getReviewDao().insertLocalReview(reviewEntity))
    }

    override fun getLocalReviews(reviewedUid: String): Flow<List<ReviewEntity>> =
        reskiumeDatabase.getReviewDao().getLocalReviews(reviewedUid)


    override suspend fun deleteLocalReviews(reviewedUid: String, onDeletedReviews: (rowsDeleted: Int) -> Unit) {
        onDeletedReviews(reskiumeDatabase.getReviewDao().deleteLocalReviews(reviewedUid))
    }
}
