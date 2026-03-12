package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllMyRescueEventsFromRemoteRepository(private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository) {

    operator fun invoke(creatorId: String): Flow<List<RescueEvent>> =
        fireStoreRemoteRescueEventRepository.getAllMyRemoteRescueEvents(creatorId)
            .map { list: List<RemoteRescueEvent?> ->
                list.mapNotNull {
                    it?.toDomain()
                }
            }
}
