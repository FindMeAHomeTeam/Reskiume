package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRescueEventFromRemoteRepository(private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository) {
    operator fun invoke(id: String): Flow<RescueEvent?> =
        fireStoreRemoteRescueEventRepository.getRemoteRescueEvent(id).map { it?.toDomain() }
}
