package com.findmeahometeam.reskiume.data.remote.database.remoteUser

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalForeignApi::class)
class RealtimeDatabaseRemoteUserRepositoryIosImpl(
    private val realtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper: RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper,
    private val realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate,
    private val log: Log
) : RealtimeDatabaseRemoteUserRepository {

    private suspend fun initialCheck(
        remoteUserUid: String?,
        onSuccess: suspend (RealtimeDatabaseRemoteUserRepositoryForIosDelegate) -> Unit,
        onFailure: () -> Unit
    ) {
        val value =
            realtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper.realtimeDatabaseRemoteUserRepositoryForIosDelegateState.value
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
                log.e("RealtimeDatabaseRepositoryIosImpl", "insertRemoteUser: Error inserting the remote user ${remoteUser.uid?.ifBlank { "" }}")
                onInsertRemoteUser(DatabaseResult.Error())
            }
        )
    }

    override fun getRemoteUser(uid: String): Flow<RemoteUser?> {
        realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate.updateUserUid(uid)
        return realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate.realtimeDatabaseRemoteUserRepositoryForIosDelegateFlow
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
                log.e("RealtimeDatabaseRepositoryIosImpl", "updateRemoteUser: Error updating the remote user ${remoteUser.uid?.ifBlank { "" }}")
                onUpdateRemoteUser(DatabaseResult.Error())
            }
        )
    }

    override fun deleteRemoteUser(
        uid: String,
        onDeleteRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        val value =
            realtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper.realtimeDatabaseRemoteUserRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteRemoteUser(uid, onDeleteRemoteUser)
        } else {
            log.e("RealtimeDatabaseRepositoryIosImpl", "deleteRemoteUser: Error deleting the remote user ${uid.ifBlank { "" }}")
            onDeleteRemoteUser(DatabaseResult.Error())
        }
    }
}
