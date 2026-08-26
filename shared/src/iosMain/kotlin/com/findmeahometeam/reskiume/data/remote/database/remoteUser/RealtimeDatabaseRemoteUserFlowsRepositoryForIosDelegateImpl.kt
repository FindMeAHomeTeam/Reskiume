package com.findmeahometeam.reskiume.data.remote.database.remoteUser

import com.findmeahometeam.reskiume.data.remote.response.remoterUser.RemoteUser
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegateImpl :
    RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate {
    private val _userUidState: MutableSharedFlow<String> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val userUidFlow: Flow<String> = _userUidState.asSharedFlow()

    override fun updateUserUid(userUid: String) {
        _userUidState.tryEmit(userUid)
    }

    private val _realtimeDatabaseRemoteUserRepositoryForIosDelegateState: MutableSharedFlow<RemoteUser?> =
        MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override val realtimeDatabaseRemoteUserRepositoryForIosDelegateFlow: Flow<RemoteUser?> =
        _realtimeDatabaseRemoteUserRepositoryForIosDelegateState.asSharedFlow()

    override fun updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: RemoteUser?) {
        _realtimeDatabaseRemoteUserRepositoryForIosDelegateState.tryEmit(delegate)
    }
}
