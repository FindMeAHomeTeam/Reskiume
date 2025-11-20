package com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview

import com.findmeahometeam.reskiume.data.remote.response.RemoteReview
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate {
    @NativeCoroutines
    val reviewedUidFlow: Flow<String>
    fun updateReviewedUid(reviewedUid: String)
    val realtimeDatabaseRemoteReviewsRepositoryForIosDelegateFlow: Flow<List<RemoteReview>>
    fun updateRealtimeDatabaseRemoteReviewsRepositoryForIosDelegate(delegate: List<RemoteReview>)
}
