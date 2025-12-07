package com.findmeahometeam.reskiume.data.remote.database.remoteNonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegateImpl :
    RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate {

    private val _nonHumanAnimalIdAndCaregiverIdPairState: MutableSharedFlow<Pair<Int, String>> =
        MutableSharedFlow(extraBufferCapacity = 1)

    override fun updateNonHumanAnimalIdAndCaregiverId(id: Int, caregiverId: String) {
        _nonHumanAnimalIdAndCaregiverIdPairState.tryEmit(Pair(id, caregiverId))
    }

    override val nonHumanAnimalIdAndCaregiverIdPairFlow: Flow<Pair<Int, String>> =
        _nonHumanAnimalIdAndCaregiverIdPairState.asSharedFlow()

    private val _remoteNonHumanAnimalListState: MutableSharedFlow<List<RemoteNonHumanAnimal>> =
        MutableSharedFlow(extraBufferCapacity = 1)

    override fun updateRemoteNonHumanAnimalListFlow(delegate: List<RemoteNonHumanAnimal>) {
        _remoteNonHumanAnimalListState.tryEmit(delegate)
    }

    override val remoteNonHumanAnimalListFlow: Flow<List<RemoteNonHumanAnimal>> =
        _remoteNonHumanAnimalListState.asSharedFlow()
}
