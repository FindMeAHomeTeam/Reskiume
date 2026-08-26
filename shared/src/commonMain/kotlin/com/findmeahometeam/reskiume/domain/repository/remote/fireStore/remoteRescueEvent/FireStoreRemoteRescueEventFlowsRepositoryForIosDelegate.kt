package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent

import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.QueryRescueEvent
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

interface FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate {
    
    fun updateQueryRescueEvent(queryRescueEvent: QueryRescueEvent)
    
    @NativeCoroutines
    val queryRescueEventFlow: Flow<QueryRescueEvent>
    
    fun updateRemoteRescueEventListFlow(delegate: List<RemoteRescueEvent>)
    
    val remoteRescueEventListFlow: Flow<List<RemoteRescueEvent>>
}
