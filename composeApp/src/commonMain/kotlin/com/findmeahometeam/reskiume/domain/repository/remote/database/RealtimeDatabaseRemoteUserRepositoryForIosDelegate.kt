package com.findmeahometeam.reskiume.domain.repository.remote.database

import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import kotlinx.coroutines.flow.StateFlow

interface RealtimeDatabaseRemoteUserRepositoryForIosDelegate {
    val realtimeDatabaseRemoteUserRepositoryForIosDelegateState: StateFlow<RemoteUser?>
    fun updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: RemoteUser?)
}
