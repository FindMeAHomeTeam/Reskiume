package com.findmeahometeam.reskiume.data.remote.fireStore.remoteRescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.QueryRescueEvent
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FireStoreRemoteRescueEventRepositoryIosImpl(
    private val fireStoreRemoteRescueEventRepositoryForIosDelegateWrapper: FireStoreRemoteRescueEventRepositoryForIosDelegateWrapper,
    private val fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
) : FireStoreRemoteRescueEventRepository {

    private suspend fun initialCheck(
        onSuccess: suspend (FireStoreRemoteRescueEventRepositoryForIosDelegate) -> Unit,
        onFailure: () -> Unit
    ) {
        val value =
            fireStoreRemoteRescueEventRepositoryForIosDelegateWrapper.fireStoreRemoteRescueEventRepositoryForIosDelegateState.value
        if (value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override suspend fun insertRemoteRescueEvent(
        remoteRescueEvent: RemoteRescueEvent,
        onInsertRemoteRescueEvent: (DatabaseResult) -> Unit
    ) {
        if (remoteRescueEvent.creatorId.isNullOrBlank()) return onInsertRemoteRescueEvent(DatabaseResult.Error())

        initialCheck(
            onSuccess = {
                it.insertRemoteRescueEvent(remoteRescueEvent, onInsertRemoteRescueEvent)
            },
            onFailure = {
                onInsertRemoteRescueEvent(DatabaseResult.Error())
            }
        )
    }

    override suspend fun modifyRemoteRescueEvent(
        remoteRescueEvent: RemoteRescueEvent,
        onModifyRemoteRescueEvent: (DatabaseResult) -> Unit
    ) {
        if (remoteRescueEvent.creatorId.isNullOrBlank()) return onModifyRemoteRescueEvent(DatabaseResult.Error())

        initialCheck(
            onSuccess = {
                it.modifyRemoteRescueEvent(remoteRescueEvent, onModifyRemoteRescueEvent)
            },
            onFailure = {
                onModifyRemoteRescueEvent(DatabaseResult.Error())
            }
        )
    }

    override suspend fun deleteRemoteRescueEvent(
        id: String,
        onDeleteRemoteRescueEvent: (DatabaseResult) -> Unit
    ) {
        if (id.isBlank()) return onDeleteRemoteRescueEvent(DatabaseResult.Error())

        val value =
            fireStoreRemoteRescueEventRepositoryForIosDelegateWrapper.fireStoreRemoteRescueEventRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteRemoteRescueEvent(id, onDeleteRemoteRescueEvent)
        } else {
            onDeleteRemoteRescueEvent(DatabaseResult.Error())
        }
    }

    override suspend fun deleteAllMyRemoteRescueEvents(
        creatorId: String,
        onDeleteAllMyRemoteRescueEvents: (DatabaseResult) -> Unit
    ) {
        if (creatorId.isBlank()) return onDeleteAllMyRemoteRescueEvents(DatabaseResult.Error())

        val value =
            fireStoreRemoteRescueEventRepositoryForIosDelegateWrapper.fireStoreRemoteRescueEventRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteAllMyRemoteRescueEvents(creatorId, onDeleteAllMyRemoteRescueEvents)
        } else {
            onDeleteAllMyRemoteRescueEvents(DatabaseResult.Error())
        }
    }

    override fun getRemoteRescueEvent(id: String): Flow<RemoteRescueEvent?> {

        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
            .updateQueryRescueEvent(QueryRescueEvent(id = id))
        return fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.remoteRescueEventListFlow.map { it.firstOrNull() }
    }

    override fun getAllMyRemoteRescueEvents(creatorId: String): Flow<List<RemoteRescueEvent>> {

        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
            .updateQueryRescueEvent(QueryRescueEvent(creatorId = creatorId))
        return fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.remoteRescueEventListFlow
    }

    override fun getAllRemoteRescueEventsByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RemoteRescueEvent?>> {

        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
            .updateQueryRescueEvent(QueryRescueEvent(country = country, city = city))
        return fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.remoteRescueEventListFlow
    }

    override fun getAllRemoteRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RemoteRescueEvent?>> {

        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
            .updateQueryRescueEvent(
                QueryRescueEvent(
                    activistLongitude = activistLongitude,
                    activistLatitude = activistLatitude,
                    rangeLongitude = rangeLongitude,
                    rangeLatitude = rangeLatitude
                )
            )
        return fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.remoteRescueEventListFlow
    }
}
