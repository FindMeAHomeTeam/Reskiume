package com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate {
    fun updateNonHumanAnimalIdAndCaregiverId(id: String = "", caregiverId: String)
    @NativeCoroutines
    val nonHumanAnimalIdAndCaregiverIdPairFlow: Flow<Pair<String, String>>
    fun updateRemoteNonHumanAnimalListFlow(delegate: List<RemoteNonHumanAnimal>)
    val remoteNonHumanAnimalListFlow: Flow<List<RemoteNonHumanAnimal>>
}
