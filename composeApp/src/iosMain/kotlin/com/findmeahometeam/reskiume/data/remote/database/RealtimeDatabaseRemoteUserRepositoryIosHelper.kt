package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RealtimeDatabaseRemoteUserRepositoryIosHelper: KoinComponent {
    val realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate by inject()
    val realtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper: RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper by inject()
    val log: Log by inject()
}
