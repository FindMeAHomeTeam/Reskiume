package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapperImpl :
    RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper {

    private val _realtimeDatabaseRemoteUserRepositoryForIosDelegateState: MutableStateFlow<RealtimeDatabaseRemoteUserRepositoryForIosDelegate?> =
        MutableStateFlow(null)

    override val realtimeDatabaseRemoteUserRepositoryForIosDelegateState: StateFlow<RealtimeDatabaseRemoteUserRepositoryForIosDelegate?> =
        _realtimeDatabaseRemoteUserRepositoryForIosDelegateState.asStateFlow()

    override fun updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: RealtimeDatabaseRemoteUserRepositoryForIosDelegate?) {
        _realtimeDatabaseRemoteUserRepositoryForIosDelegateState.value = delegate
    }
}
