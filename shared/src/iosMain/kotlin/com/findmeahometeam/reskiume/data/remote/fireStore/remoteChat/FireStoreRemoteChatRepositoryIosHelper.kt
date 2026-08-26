package com.findmeahometeam.reskiume.data.remote.fireStore.remoteChat

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepositoryForIosDelegateWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FireStoreRemoteChatRepositoryIosHelper: KoinComponent {

    val fireStoreRemoteChatFlowsRepositoryForIosDelegate: FireStoreRemoteChatFlowsRepositoryForIosDelegate by inject()

    val fireStoreRemoteChatRepositoryForIosDelegateWrapper: FireStoreRemoteChatRepositoryForIosDelegateWrapper by inject()

    val log: Log by inject()
}
