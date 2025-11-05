package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRemoteUserRepositoryForIosDelegate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RealtimeDatabaseRemoteUserRepositoryForIosDelegateImpl :
    RealtimeDatabaseRemoteUserRepositoryForIosDelegate {
    private val _userUidState: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)

    override val userUidFlow: Flow<String> = _userUidState.asSharedFlow()

    override fun updateUserUid(userUid: String) {
        _userUidState.tryEmit(userUid)
    }

    private val _realtimeDatabaseRemoteUserRepositoryForIosDelegateState: MutableSharedFlow<RemoteUser?> =
        MutableSharedFlow(extraBufferCapacity = 1)

    override val realtimeDatabaseRemoteUserRepositoryForIosDelegateFlow: Flow<RemoteUser?> =
        _realtimeDatabaseRemoteUserRepositoryForIosDelegateState.asSharedFlow()

    override fun updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: RemoteUser?) {
        _realtimeDatabaseRemoteUserRepositoryForIosDelegateState.tryEmit(delegate)
    }
}
