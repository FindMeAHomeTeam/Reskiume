package com.findmeahometeam.reskiume.domain.usecases.review

import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository

class DeleteReviewsFromLocalRepository(private val repository: LocalReviewRepository) {
    suspend operator fun invoke(reviewedUid: String, onDeletedReviews: (rowsDeleted: Int) -> Unit) {
        repository.deleteLocalReviews(reviewedUid, onDeletedReviews)
    }
}
