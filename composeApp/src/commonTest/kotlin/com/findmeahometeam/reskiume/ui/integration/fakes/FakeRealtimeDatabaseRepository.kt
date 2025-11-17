package com.findmeahometeam.reskiume.ui.integration.fakes

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.user
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRealtimeDatabaseRepository(
    private val remoteUserList: MutableList<RemoteUser> = mutableListOf()
) : RealtimeDatabaseRepository {

    override suspend fun insertRemoteUser(
        remoteUser: RemoteUser,
        onInsertRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        val storedRemoteUser = remoteUserList.firstOrNull{ it.uid == remoteUser.uid }
        if (storedRemoteUser == null) {
            remoteUserList.add(remoteUser)
            onInsertRemoteUser(DatabaseResult.Success)
        } else {
            onInsertRemoteUser(DatabaseResult.Error("User already exists"))
        }
    }

    override fun getRemoteUser(uid: String): Flow<RemoteUser?> =
        flowOf(remoteUserList.firstOrNull{ it.uid == user.uid })

    override suspend fun updateRemoteUser(
        remoteUser: RemoteUser,
        onUpdateRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        val remoteUser = remoteUserList.firstOrNull{ it.uid == user.uid }
        if (remoteUser == null) {
            onUpdateRemoteUser(DatabaseResult.Error("User not found"))
        } else {
            remoteUserList[remoteUserList.indexOf(remoteUser)] = user.toData()
            onUpdateRemoteUser(DatabaseResult.Success)
        }
    }

    override fun deleteRemoteUser(
        uid: String,
        onDeleteRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        val remoteUser = remoteUserList.firstOrNull{ it.uid == user.uid }
        if (remoteUser == null) {
            onDeleteRemoteUser(DatabaseResult.Error("User not deleted"))
        } else {
            remoteUserList.remove(remoteUser)
            onDeleteRemoteUser(DatabaseResult.Success)
        }
    }
}
