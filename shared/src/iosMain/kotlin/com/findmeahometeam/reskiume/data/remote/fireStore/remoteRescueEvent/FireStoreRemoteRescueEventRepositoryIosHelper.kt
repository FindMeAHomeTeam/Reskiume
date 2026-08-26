package com.findmeahometeam.reskiume.data.remote.fireStore.remoteRescueEvent

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepositoryForIosDelegateWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FireStoreRemoteRescueEventRepositoryIosHelper: KoinComponent {

    val fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate by inject()

    val fireStoreRemoteRescueEventRepositoryForIosDelegateWrapper: FireStoreRemoteRescueEventRepositoryForIosDelegateWrapper by inject()

    val log: Log by inject()
}
