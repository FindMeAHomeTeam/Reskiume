package com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate {
    fun updateNonHumanAnimalIdAndCaregiverId(id: Int = 0, caregiverId: String)
    @NativeCoroutines
    val nonHumanAnimalIdAndCaregiverIdPairFlow: Flow<Pair<Int, String>>
    fun updateRemoteNonHumanAnimalListFlow(delegate: List<RemoteNonHumanAnimal>)
    val remoteNonHumanAnimalListFlow: Flow<List<RemoteNonHumanAnimal>>
}
