package com.findmeahometeam.reskiume.domain.repository.remote.database

import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow

interface RealtimeDatabaseRemoteUserRepositoryForIosDelegate {
    @NativeCoroutinesState
    val userUidState: StateFlow<String>
    fun updateUserUidState(userUid: String)
    val realtimeDatabaseRemoteUserRepositoryForIosDelegateState: StateFlow<RemoteUser?>
    fun updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: RemoteUser?)
}
