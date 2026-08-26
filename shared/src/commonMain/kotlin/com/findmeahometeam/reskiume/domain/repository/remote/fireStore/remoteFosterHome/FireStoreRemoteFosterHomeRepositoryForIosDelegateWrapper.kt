package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome

import kotlinx.coroutines.flow.StateFlow

interface FireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper {
    
    val fireStoreRemoteFosterHomeRepositoryForIosDelegateState: StateFlow<FireStoreRemoteFosterHomeRepositoryForIosDelegate?>
    
    fun updateFireStoreRemoteFosterHomeRepositoryForIosDelegate(delegate: FireStoreRemoteFosterHomeRepositoryForIosDelegate?)
}
