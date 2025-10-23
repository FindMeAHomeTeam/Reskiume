package com.findmeahometeam.reskiume.domain.repository.remote.database

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser

interface RealtimeDatabaseRepositoryForIosDelegate {
    fun insertRemoteUser(remoteUser: RemoteUser, onInsertRemoteUser: (result: DatabaseResult) -> Unit)
    fun updateRemoteUser(remoteUser: RemoteUser, onUpdateRemoteUser: (result: DatabaseResult) -> Unit)
    fun deleteRemoteUser(uid: String, onDeleteRemoteUser: (result: DatabaseResult) -> Unit)
}
