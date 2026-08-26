package com.findmeahometeam.reskiume.data.remote.fireStore.remoteFosterHome

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FireStoreRemoteFosterHomeRepositoryIosHelper: KoinComponent {

    val fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate by inject()

    val fireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper: FireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper by inject()

    val log: Log by inject()
}
