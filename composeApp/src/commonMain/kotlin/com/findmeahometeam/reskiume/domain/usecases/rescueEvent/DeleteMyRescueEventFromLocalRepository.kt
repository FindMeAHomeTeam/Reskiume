package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository

class DeleteMyRescueEventFromLocalRepository(
    private val localRescueEventRepository: LocalRescueEventRepository,
) {
    suspend operator fun invoke(
        id: String,
        onDeleteRescueEvent: suspend (rowsDeleted: Int) -> Unit
    ) {
        localRescueEventRepository.deleteRescueEvent(id, onDeleteRescueEvent)
    }
}
