package com.findmeahometeam.reskiume.data.remote.database.remoteReview

import com.findmeahometeam.reskiume.data.remote.response.RemoteReview
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegateImpl :
    RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate {
    private val _reviewedUidState: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)

    override val reviewedUidFlow: Flow<String> = _reviewedUidState.asSharedFlow()

    override fun updateReviewedUid(reviewedUid: String) {
        _reviewedUidState.tryEmit(reviewedUid)
    }

    private val _realtimeDatabaseRemoteReviewsRepositoryForIosDelegateState: MutableSharedFlow<List<RemoteReview>> =
        MutableSharedFlow(extraBufferCapacity = 1)

    override val realtimeDatabaseRemoteReviewsRepositoryForIosDelegateFlow: Flow<List<RemoteReview>> =
        _realtimeDatabaseRemoteReviewsRepositoryForIosDelegateState.asSharedFlow()

    override fun updateRealtimeDatabaseRemoteReviewsRepositoryForIosDelegate(delegate: List<RemoteReview>) {
        _realtimeDatabaseRemoteReviewsRepositoryForIosDelegateState.tryEmit(delegate)
    }
}
