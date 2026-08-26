package com.findmeahometeam.reskiume.domain.usecases.review

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository

class InsertReviewInRemoteRepository(private val repository: RealtimeDatabaseRemoteReviewRepository) {
    suspend operator fun invoke(review: Review, onInsertRemoteReview: (result: DatabaseResult) -> Unit) =
        repository.insertRemoteReview(review.toData(), onInsertRemoteReview)
}
