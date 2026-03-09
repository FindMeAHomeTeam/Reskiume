package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent

import kotlinx.coroutines.flow.StateFlow

interface FireStoreRemoteRescueEventRepositoryForIosDelegateWrapper {
    
    val fireStoreRemoteRescueEventRepositoryForIosDelegateState: StateFlow<FireStoreRemoteRescueEventRepositoryForIosDelegate?>
    
    fun updateFireStoreRemoteRescueEventRepositoryForIosDelegate(delegate: FireStoreRemoteRescueEventRepositoryForIosDelegate?)
}
