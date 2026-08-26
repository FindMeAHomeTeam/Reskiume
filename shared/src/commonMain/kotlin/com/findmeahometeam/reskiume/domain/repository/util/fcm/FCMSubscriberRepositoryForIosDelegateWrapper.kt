package com.findmeahometeam.reskiume.domain.repository.util.fcm

import kotlinx.coroutines.flow.StateFlow

interface FCMSubscriberRepositoryForIosDelegateWrapper {

    val fCMSubscriberRepositoryForIosDelegateState: StateFlow<FCMSubscriberRepositoryForIosDelegate?>

    fun updateFCMSubscriberRepositoryForIosDelegate(delegate: FCMSubscriberRepositoryForIosDelegate?)
}
