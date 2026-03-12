package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository

class DeleteMyRescueEventFromRemoteRepository(private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository) {

    suspend operator fun invoke(
        id: String,
        onDeleteRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        fireStoreRemoteRescueEventRepository.deleteRemoteRescueEvent(
            id,
            onDeleteRemoteRescueEvent
        )
    }
}
