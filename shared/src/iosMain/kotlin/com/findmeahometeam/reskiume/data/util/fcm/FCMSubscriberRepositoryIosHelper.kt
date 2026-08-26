package com.findmeahometeam.reskiume.data.util.fcm

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepositoryForIosDelegateWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FCMSubscriberRepositoryIosHelper: KoinComponent {
    val fCMSubscriberRepositoryForIosDelegateWrapper: FCMSubscriberRepositoryForIosDelegateWrapper by inject()
    val log: Log by inject()
}
