package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRemoteUserRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepositoryForIosDelegateWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RealtimeDatabaseRepositoryIosHelper: KoinComponent {
    val realtimeDatabaseRemoteUserRepositoryForIosDelegate: RealtimeDatabaseRemoteUserRepositoryForIosDelegate by inject()
    val realtimeDatabaseRepositoryForIosDelegateWrapper: RealtimeDatabaseRepositoryForIosDelegateWrapper by inject()
    val log: Log by inject()
}
