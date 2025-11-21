package com.findmeahometeam.reskiume.domain.usecases.review

import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetReviewsFromLocalRepository(private val repository: LocalReviewRepository) {
    operator fun invoke(reviewedUid: String): Flow<List<Review>> =
        repository.getLocalReviews(reviewedUid).map { reviewEntities ->
            reviewEntities.map { it.toDomain() }
        }
}
