package com.findmeahometeam.reskiume.ui.integration.fakes

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteReview
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRealtimeDatabaseRemoteReviewRepository(
    private val remoteReviews: MutableList<RemoteReview> = mutableListOf()
) : RealtimeDatabaseRemoteReviewRepository {

    override suspend fun insertRemoteReview(
        remoteReview: RemoteReview,
        onInsertRemoteReview: (DatabaseResult) -> Unit
    ) {
        val review = remoteReviews.firstOrNull { it.id == remoteReview.id }
        if (review == null) {
            remoteReviews.add(remoteReview)
            onInsertRemoteReview(DatabaseResult.Success)
        } else {
            onInsertRemoteReview(DatabaseResult.Error("Remote review already exists"))
        }
    }

    override fun getRemoteReviews(reviewedUid: String): Flow<List<RemoteReview>> =
        flowOf(remoteReviews.filter { it.reviewedUid == reviewedUid })

    override fun deleteRemoteReviews(
        reviewedUid: String,
        onDeletedRemoteReviews: (DatabaseResult) -> Unit
    ) {
        val reviewList = remoteReviews.filter { it.reviewedUid == reviewedUid }
        if (reviewList.isEmpty()) {
            onDeletedRemoteReviews(DatabaseResult.Error("No remote reviews found to delete"))
        } else {
            remoteReviews.removeAll(reviewList)
            onDeletedRemoteReviews(DatabaseResult.Success)
        }
    }
}
