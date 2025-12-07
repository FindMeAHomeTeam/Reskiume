package com.findmeahometeam.reskiume.data.remote.database.remoteNonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapperImpl :
    RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper {

    private val _realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateState: MutableStateFlow<RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate?> =
        MutableStateFlow(null)

    override val realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateState: StateFlow<RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate?> =
        _realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateState.asStateFlow()

    override fun updateRealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate(delegate: RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate?) {
        _realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateState.value = delegate
    }
}
