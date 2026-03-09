package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent

interface FireStoreRemoteRescueEventRepositoryForIosDelegate {
    
    suspend fun insertRemoteRescueEvent(
        remoteRescueEvent: RemoteRescueEvent,
        onInsertRemoteRescueEvent: (result: DatabaseResult) -> Unit
    )

    suspend fun modifyRemoteRescueEvent(
        remoteRescueEvent: RemoteRescueEvent,
        onModifyRemoteRescueEvent: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteRemoteRescueEvent(
        id: String,
        onDeleteRemoteRescueEvent: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteAllMyRemoteRescueEvents(
        creatorId: String,
        onDeleteAllMyRemoteRescueEvents: (result: DatabaseResult) -> Unit
    )
}
