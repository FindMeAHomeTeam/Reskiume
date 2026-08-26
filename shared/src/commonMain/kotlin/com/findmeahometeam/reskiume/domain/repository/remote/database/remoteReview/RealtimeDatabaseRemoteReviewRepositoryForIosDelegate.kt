package com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteReview

interface RealtimeDatabaseRemoteReviewRepositoryForIosDelegate {
    suspend fun insertRemoteReview(remoteReview: RemoteReview, onInsertRemoteReview: (result: DatabaseResult) -> Unit)
    fun deleteRemoteReviews(reviewedUid: String, onDeletedRemoteReviews: (result: DatabaseResult) -> Unit)
}
