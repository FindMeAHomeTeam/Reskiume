package com.findmeahometeam.reskiume.domain.usecases.review

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository

class DeleteReviewsFromRemoteRepository(private val repository: RealtimeDatabaseRemoteReviewRepository) {
    operator fun invoke(reviewedUid: String, onDeletedRemoteReviews: (result: DatabaseResult) -> Unit) {
        repository.deleteRemoteReviews(reviewedUid, onDeletedRemoteReviews)
    }
}
