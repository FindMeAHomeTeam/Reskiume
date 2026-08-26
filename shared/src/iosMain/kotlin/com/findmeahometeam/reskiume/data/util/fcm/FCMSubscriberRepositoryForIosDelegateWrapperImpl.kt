package com.findmeahometeam.reskiume.data.util.fcm

import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FCMSubscriberRepositoryForIosDelegateWrapperImpl : FCMSubscriberRepositoryForIosDelegateWrapper {

    private val _fCMSubscriberRepositoryForIosState: MutableStateFlow<FCMSubscriberRepositoryForIosDelegate?> =
        MutableStateFlow(null)

    override val fCMSubscriberRepositoryForIosDelegateState: StateFlow<FCMSubscriberRepositoryForIosDelegate?> =
        _fCMSubscriberRepositoryForIosState.asStateFlow()

    override fun updateFCMSubscriberRepositoryForIosDelegate(delegate: FCMSubscriberRepositoryForIosDelegate?) {
        _fCMSubscriberRepositoryForIosState.value = delegate
    }
}
