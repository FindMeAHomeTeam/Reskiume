package com.findmeahometeam.reskiume.domain.usecases.review

import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetReviewsFromRemoteRepository(private val repository: RealtimeDatabaseRemoteReviewRepository) {
    operator fun invoke(reviewedUid: String): Flow<List<Review>> =
        repository.getRemoteReviews(reviewedUid).map { remoteReviews ->
            remoteReviews.map { it.toDomain() }
        }
}
