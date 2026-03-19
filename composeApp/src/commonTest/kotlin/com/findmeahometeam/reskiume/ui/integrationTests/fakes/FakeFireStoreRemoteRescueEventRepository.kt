package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFireStoreRemoteRescueEventRepository(
    private val remoteRescueEventList: MutableList<RemoteRescueEvent> = mutableListOf()
) : FireStoreRemoteRescueEventRepository {

    override suspend fun insertRemoteRescueEvent(
        remoteRescueEvent: RemoteRescueEvent,
        onInsertRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        val rescueEvent =
            remoteRescueEventList.firstOrNull { it.id == remoteRescueEvent.id }
        if (rescueEvent == null) {
            remoteRescueEventList.add(remoteRescueEvent)
            onInsertRemoteRescueEvent(DatabaseResult.Success)
        } else {
            onInsertRemoteRescueEvent(DatabaseResult.Error("error adding a rescue event"))
        }
    }

    override suspend fun modifyRemoteRescueEvent(
        remoteRescueEvent: RemoteRescueEvent,
        onModifyRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        val rescueEvent =
            remoteRescueEventList.firstOrNull { it.id == remoteRescueEvent.id }
        if (rescueEvent == null) {
            onModifyRemoteRescueEvent(DatabaseResult.Error("error modifying a rescue event"))
        } else {
            remoteRescueEventList[remoteRescueEventList.indexOf(rescueEvent)] = remoteRescueEvent
            onModifyRemoteRescueEvent(DatabaseResult.Success)
        }
    }

    override suspend fun deleteRemoteRescueEvent(
        id: String,
        onDeleteRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        val rescueEvent =
            remoteRescueEventList.firstOrNull { it.id == id }
        if (rescueEvent == null) {
            onDeleteRemoteRescueEvent(DatabaseResult.Error("error deleting a rescue event"))
        } else {
            remoteRescueEventList.remove(rescueEvent)
            onDeleteRemoteRescueEvent(DatabaseResult.Success)
        }
    }

    override suspend fun deleteAllMyRemoteRescueEvents(
        creatorId: String,
        onDeleteAllMyRemoteRescueEvents: (result: DatabaseResult) -> Unit
    ) {
        val rescueEventList = remoteRescueEventList.filter { it.creatorId == creatorId }
        if (rescueEventList.isEmpty()) {
            onDeleteAllMyRemoteRescueEvents(DatabaseResult.Error("error deleting all rescue events"))
        } else {
            remoteRescueEventList.removeAll(rescueEventList)
            onDeleteAllMyRemoteRescueEvents(DatabaseResult.Success)
        }
    }

    override fun getRemoteRescueEvent(id: String): Flow<RemoteRescueEvent?> =
        flowOf(remoteRescueEventList.firstOrNull { it.id == id })

    override fun getAllMyRemoteRescueEvents(creatorId: String): Flow<List<RemoteRescueEvent>> =
        flowOf(remoteRescueEventList.filter { it.creatorId == creatorId })

    override fun getAllRemoteRescueEventsByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RemoteRescueEvent?>> =
        flowOf(remoteRescueEventList.filter { it.country == country && it.city == city })

    override fun getAllRemoteRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RemoteRescueEvent?>> =
        flowOf(
            remoteRescueEventList.filter {
                it.longitude!! >= activistLongitude - rangeLongitude
                        && it.longitude <= activistLongitude + rangeLongitude
                        && it.latitude!! >= activistLatitude - rangeLatitude
                        && it.latitude <= activistLatitude + rangeLatitude
            }
        )
}
