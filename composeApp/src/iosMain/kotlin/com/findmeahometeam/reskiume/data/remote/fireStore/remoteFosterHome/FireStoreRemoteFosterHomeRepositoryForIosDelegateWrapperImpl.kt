package com.findmeahometeam.reskiume.data.remote.fireStore.remoteFosterHome

import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FireStoreRemoteFosterHomeRepositoryForIosDelegateWrapperImpl :
    FireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper {

    private val _fireStoreRemoteFosterHomeRepositoryForIosDelegateState: MutableStateFlow<FireStoreRemoteFosterHomeRepositoryForIosDelegate?> =
        MutableStateFlow(null)

    override val fireStoreRemoteFosterHomeRepositoryForIosDelegateState: StateFlow<FireStoreRemoteFosterHomeRepositoryForIosDelegate?> =
        _fireStoreRemoteFosterHomeRepositoryForIosDelegateState.asStateFlow()

    override fun updateFireStoreRemoteFosterHomeRepositoryForIosDelegate(delegate: FireStoreRemoteFosterHomeRepositoryForIosDelegate?) {
        _fireStoreRemoteFosterHomeRepositoryForIosDelegateState.value = delegate
    }
}
