package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRemoteUserRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepositoryForIosDelegateWrapper
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow

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
                Log.e("RealtimeDatabaseRepositoryIosImpl", "insertRemoteUser: Error inserting the remote user ${remoteUser.uid?.ifBlank { "" }}")
                onInsertRemoteUser(DatabaseResult.Error())
            }
        )
    }

    override fun getRemoteUser(uid: String): Flow<RemoteUser?> {
        realtimeDatabaseRemoteUserRepositoryForIosDelegate.updateUserUidState(uid)
        return realtimeDatabaseRemoteUserRepositoryForIosDelegate.realtimeDatabaseRemoteUserRepositoryForIosDelegateState
    }

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
                Log.e("RealtimeDatabaseRepositoryIosImpl", "updateRemoteUser: Error updating the remote user ${remoteUser.uid?.ifBlank { "" }}")
                onUpdateRemoteUser(DatabaseResult.Error())
            }
        )
    }

    override fun deleteRemoteUser(
        uid: String,
        onDeleteRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        val value =
            realtimeDatabaseRepositoryForIosDelegateWrapper.realtimeDatabaseRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteRemoteUser(uid, onDeleteRemoteUser)
        } else {
            Log.e("RealtimeDatabaseRepositoryIosImpl", "deleteRemoteUser: Error deleting the remote user ${uid.ifBlank { "" }}")
            onDeleteRemoteUser(DatabaseResult.Error())
        }
    }
}
