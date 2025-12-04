package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalReviewRepository(
    private val reviews: MutableList<ReviewEntity> = mutableListOf()
) : LocalReviewRepository {

    override suspend fun insertLocalReview(
        reviewEntity: ReviewEntity,
        onInsertReview: (rowId: Long) -> Unit
    ) {
        val review = reviews.firstOrNull { it.id == reviewEntity.id }
        if (review == null) {
            reviews.add(reviewEntity)
            onInsertReview(1L)
        } else {
            onInsertReview(0)
        }
    }

    override fun getLocalReviews(reviewedUid: String): Flow<List<ReviewEntity>> =
        flowOf(reviews.filter { it.reviewedUid == reviewedUid })

    override suspend fun deleteLocalReviews(
        reviewedUid: String,
        onDeletedReviews: (rowsDeleted: Int) -> Unit
    ) {
        val reviewList =
            reviews.filter { it.reviewedUid == reviewedUid || it.savedBy == reviewedUid || it.savedBy == "" }
        if (reviewList.isEmpty()) {
            onDeletedReviews(0)
        } else {
            reviews.removeAll(reviewList)
            onDeletedReviews(1)
        }
    }
}
