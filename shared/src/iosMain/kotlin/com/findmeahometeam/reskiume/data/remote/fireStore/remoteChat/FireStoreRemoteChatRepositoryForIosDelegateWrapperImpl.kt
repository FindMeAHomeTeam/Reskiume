package com.findmeahometeam.reskiume.data.remote.fireStore.remoteChat

import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FireStoreRemoteChatRepositoryForIosDelegateWrapperImpl :
    FireStoreRemoteChatRepositoryForIosDelegateWrapper {

    private val _fireStoreRemoteChatRepositoryForIosDelegateState: MutableStateFlow<FireStoreRemoteChatRepositoryForIosDelegate?> =
        MutableStateFlow(null)

    override val fireStoreRemoteChatRepositoryForIosDelegateState: StateFlow<FireStoreRemoteChatRepositoryForIosDelegate?> =
        _fireStoreRemoteChatRepositoryForIosDelegateState.asStateFlow()

    override fun updateFireStoreRemoteChatRepositoryForIosDelegate(delegate: FireStoreRemoteChatRepositoryForIosDelegate?) {
        _fireStoreRemoteChatRepositoryForIosDelegateState.value = delegate
    }
}
