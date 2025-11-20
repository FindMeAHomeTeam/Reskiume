package com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteReview
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabaseRemoteReviewRepository {
    suspend fun insertRemoteReview(remoteReview: RemoteReview, onInsertRemoteReview: (result: DatabaseResult) -> Unit)
    fun getRemoteReviews(reviewedUid: String): Flow<List<RemoteReview>>
    fun deleteRemoteReviews(reviewedUid: String, onDeletedRemoteReviews: (result: DatabaseResult) -> Unit)
}
