package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRemoteUserRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepositoryForIosDelegateWrapper
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

@OptIn(ExperimentalForeignApi::class)
class RealtimeDatabaseRepositoryIosImpl(
    private val realtimeDatabaseRepositoryForIosDelegateWrapper: RealtimeDatabaseRepositoryForIosDelegateWrapper,
    private val realtimeDatabaseRemoteUserRepositoryForIosDelegate: RealtimeDatabaseRemoteUserRepositoryForIosDelegate
) : RealtimeDatabaseRepository {

    private suspend fun initialCheck(
        remoteUserUid: String?,
        onSuccess: suspend (RealtimeDatabaseRepositoryForIosDelegate) -> Unit,
        onFailure: () -> Unit
    ) {
        val value =
            realtimeDatabaseRepositoryForIosDelegateWrapper.realtimeDatabaseRepositoryForIosDelegateState.value
        if (remoteUserUid != null && value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override suspend fun insertRemoteUser(
        remoteUser: RemoteUser,
        onInsertRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        initialCheck(
            remoteUser.uid,
            onSuccess = {
                it.insertRemoteUser(remoteUser, onInsertRemoteUser)
            },
            onFailure = {
                onInsertRemoteUser(DatabaseResult.Error("Error inserting the remote user ${remoteUser.uid?.ifBlank { "" }}"))
            }
        )
    }

    override fun getRemoteUser(uid: String): Flow<RemoteUser?> =
        realtimeDatabaseRemoteUserRepositoryForIosDelegate.realtimeDatabaseRemoteUserRepositoryForIosDelegateState.filterNotNull()

    override suspend fun updateRemoteUser(
        remoteUser: RemoteUser,
        onUpdateRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        initialCheck(
            remoteUser.uid,
            onSuccess = {
                it.updateRemoteUser(remoteUser, onUpdateRemoteUser)
            },
            onFailure = {
                onUpdateRemoteUser(DatabaseResult.Error("Error updating the remote user ${remoteUser.uid?.ifBlank { "" }}"))
            }
        )
    }

    override fun deleteRemoteUser(
        uid: String,
        onDeleteRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        initialCheck(
            uid,
            onSuccess = {
                it.deleteRemoteUser(uid, onDeleteRemoteUser)
            },
            onFailure = {
                onDeleteRemoteUser(DatabaseResult.Error("Error deleting the remote user ${uid.ifBlank { "" }}"))
            }
        )
    }
}
