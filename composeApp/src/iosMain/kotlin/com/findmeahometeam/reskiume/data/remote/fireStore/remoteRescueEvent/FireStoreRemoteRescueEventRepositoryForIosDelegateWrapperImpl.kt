package com.findmeahometeam.reskiume.data.remote.fireStore.remoteRescueEvent

import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FireStoreRemoteRescueEventRepositoryForIosDelegateWrapperImpl :
    FireStoreRemoteRescueEventRepositoryForIosDelegateWrapper {

    private val _fireStoreRemoteRescueEventRepositoryForIosDelegateState: MutableStateFlow<FireStoreRemoteRescueEventRepositoryForIosDelegate?> =
        MutableStateFlow(null)

    override val fireStoreRemoteRescueEventRepositoryForIosDelegateState: StateFlow<FireStoreRemoteRescueEventRepositoryForIosDelegate?> =
        _fireStoreRemoteRescueEventRepositoryForIosDelegateState.asStateFlow()

    override fun updateFireStoreRemoteRescueEventRepositoryForIosDelegate(delegate: FireStoreRemoteRescueEventRepositoryForIosDelegate?) {
        _fireStoreRemoteRescueEventRepositoryForIosDelegateState.value = delegate
    }
}
