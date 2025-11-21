package com.findmeahometeam.reskiume.domain.usecases.review

import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository

class InsertReviewInLocalRepository(private val repository: LocalReviewRepository) {
    suspend operator fun invoke(review: Review, onInsertReview: (rowId: Long) -> Unit) =
        repository.insertLocalReview(review.toEntity(), onInsertReview)
}
