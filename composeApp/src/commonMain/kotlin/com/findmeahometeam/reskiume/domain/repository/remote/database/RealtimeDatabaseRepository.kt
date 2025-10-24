package com.findmeahometeam.reskiume.domain.repository.remote.database

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabaseRepository {
    suspend fun insertRemoteUser(remoteUser: RemoteUser, onInsertRemoteUser: (result: DatabaseResult) -> Unit)
    fun getRemoteUser(uid: String): Flow<RemoteUser?>
    suspend fun updateRemoteUser(remoteUser: RemoteUser, onUpdateRemoteUser: (result: DatabaseResult) -> Unit)
    fun deleteRemoteUser(uid: String, onDeleteRemoteUser: (result: DatabaseResult) -> Unit)
}
