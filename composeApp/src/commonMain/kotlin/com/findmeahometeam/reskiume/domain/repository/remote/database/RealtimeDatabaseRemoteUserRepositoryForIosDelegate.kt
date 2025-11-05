package com.findmeahometeam.reskiume.domain.repository.remote.database

import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabaseRemoteUserRepositoryForIosDelegate {
    @NativeCoroutines
    val userUidFlow: Flow<String>
    fun updateUserUid(userUid: String)
    val realtimeDatabaseRemoteUserRepositoryForIosDelegateFlow: Flow<RemoteUser?>
    fun updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: RemoteUser?)
}
