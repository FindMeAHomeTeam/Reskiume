package com.findmeahometeam.reskiume.data.remote.database.remoteReview

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteReview
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.Flow

class RealtimeDatabaseRemoteReviewRepositoryIosImpl(
    private val realtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper: RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper,
    private val realtimeDatabaseRemoteReviewRepositoryFlowForIosDelegate: RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate,
    private val log: Log
) : RealtimeDatabaseRemoteReviewRepository {

    private suspend fun initialCheck(
        timestamp: Long?,
        onSuccess: suspend (RealtimeDatabaseRemoteReviewRepositoryForIosDelegate) -> Unit,
        onFailure: () -> Unit
    ) {
        val value =
            realtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper.realtimeDatabaseRemoteReviewRepositoryForIosDelegateState.value
        if (timestamp != null && value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override suspend fun insertRemoteReview(
        remoteReview: RemoteReview,
        onInsertRemoteReview: (result: DatabaseResult) -> Unit
    ) {
        initialCheck(
            remoteReview.timestamp,
            onSuccess = {
                it.insertRemoteReview(remoteReview, onInsertRemoteReview)
            },
            onFailure = {
                log.e(
                    "RealtimeDatabaseRemoteReviewRepositoryIosImpl",
                    "insertRemoteUser: Error inserting the review ${remoteReview.timestamp ?: 0L}"
                )
                onInsertRemoteReview(DatabaseResult.Error())
            }
        )
    }

    override fun getRemoteReviews(reviewedUid: String): Flow<List<RemoteReview>> {
        realtimeDatabaseRemoteReviewRepositoryFlowForIosDelegate.updateReviewedUid(reviewedUid)
        return realtimeDatabaseRemoteReviewRepositoryFlowForIosDelegate.realtimeDatabaseRemoteReviewsRepositoryForIosDelegateFlow
    }

    override fun deleteRemoteReviews(
        reviewedUid: String,
        onDeletedRemoteReviews: (result: DatabaseResult) -> Unit
    ) {
        val value =
            realtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper.realtimeDatabaseRemoteReviewRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteRemoteReviews(reviewedUid, onDeletedRemoteReviews)
        } else {
            log.e(
                "RealtimeDatabaseRemoteReviewRepositoryIosImpl",
                "deleteRemoteReview: Error deleting the remote reviews for the user ${reviewedUid.ifBlank { "" }}"
            )
            onDeletedRemoteReviews(DatabaseResult.Error())
        }
    }
}
