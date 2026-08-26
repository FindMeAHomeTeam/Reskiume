package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat

import kotlinx.coroutines.flow.StateFlow

interface FireStoreRemoteChatRepositoryForIosDelegateWrapper {
    
    val fireStoreRemoteChatRepositoryForIosDelegateState: StateFlow<FireStoreRemoteChatRepositoryForIosDelegate?>
    
    fun updateFireStoreRemoteChatRepositoryForIosDelegate(delegate: FireStoreRemoteChatRepositoryForIosDelegate?)
}
