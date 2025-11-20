package com.findmeahometeam.reskiume.data.remote.database.remoteReview

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RealtimeDatabaseRemoteReviewRepositoryIosHelper: KoinComponent {
    val realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate by inject()
    val realtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper: RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper by inject()
    val log: Log by inject()
}
