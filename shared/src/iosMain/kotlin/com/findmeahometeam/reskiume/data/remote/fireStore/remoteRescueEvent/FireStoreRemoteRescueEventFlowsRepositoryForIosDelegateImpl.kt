package com.findmeahometeam.reskiume.data.remote.fireStore.remoteRescueEvent

import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.QueryRescueEvent
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FireStoreRemoteRescueEventFlowsRepositoryForIosDelegateImpl :
    FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate {

    private val _queryRescueEventState: MutableSharedFlow<QueryRescueEvent> =
        MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun updateQueryRescueEvent(queryRescueEvent: QueryRescueEvent) {
        _queryRescueEventState.tryEmit(queryRescueEvent)
    }

    override val queryRescueEventFlow: Flow<QueryRescueEvent> =
        _queryRescueEventState.asSharedFlow()

    private val _remoteRescueEventListState: MutableSharedFlow<List<RemoteRescueEvent>> =
        MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun updateRemoteRescueEventListFlow(delegate: List<RemoteRescueEvent>) {
        _remoteRescueEventListState.tryEmit(delegate)
    }

    override val remoteRescueEventListFlow: Flow<List<RemoteRescueEvent>> =
        _remoteRescueEventListState.asSharedFlow()
}
