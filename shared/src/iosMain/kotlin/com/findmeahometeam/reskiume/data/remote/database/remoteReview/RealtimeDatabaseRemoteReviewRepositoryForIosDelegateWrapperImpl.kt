package com.findmeahometeam.reskiume.data.remote.database.remoteReview

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapperImpl :
    RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper {

    private val _realtimeDatabaseRemoteReviewRepositoryForIosDelegateState: MutableStateFlow<RealtimeDatabaseRemoteReviewRepositoryForIosDelegate?> =
        MutableStateFlow(null)

    override val realtimeDatabaseRemoteReviewRepositoryForIosDelegateState: StateFlow<RealtimeDatabaseRemoteReviewRepositoryForIosDelegate?> =
        _realtimeDatabaseRemoteReviewRepositoryForIosDelegateState.asStateFlow()

    override fun updateRealtimeDatabaseRemoteReviewRepositoryForIosDelegate(delegate: RealtimeDatabaseRemoteReviewRepositoryForIosDelegate?) {
        _realtimeDatabaseRemoteReviewRepositoryForIosDelegateState.value = delegate
    }
}
