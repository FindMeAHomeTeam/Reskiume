package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import kotlinx.coroutines.flow.Flow

interface FireStoreRemoteRescueEventRepository {

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

    fun getRemoteRescueEvent(id: String): Flow<RemoteRescueEvent?>

    fun getAllMyRemoteRescueEvents(creatorId: String): Flow<List<RemoteRescueEvent?>>

    fun getAllRemoteRescueEventsByCountryAndCity(country: String, city: String): Flow<List<RemoteRescueEvent?>>

    fun getAllRemoteRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RemoteRescueEvent?>>
}
