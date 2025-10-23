package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRemoteUserRepositoryForIosDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RealtimeDatabaseRemoteUserRepositoryForIosDelegateImpl :
    RealtimeDatabaseRemoteUserRepositoryForIosDelegate {

    private val _realtimeDatabaseRemoteUserRepositoryForIosDelegateState: MutableStateFlow<RemoteUser?> =
        MutableStateFlow(null)

    override val realtimeDatabaseRemoteUserRepositoryForIosDelegateState: StateFlow<RemoteUser?> =
        _realtimeDatabaseRemoteUserRepositoryForIosDelegateState.asStateFlow()

    override fun updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: RemoteUser?) {
        _realtimeDatabaseRemoteUserRepositoryForIosDelegateState.value = delegate
    }
}
