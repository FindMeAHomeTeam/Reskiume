package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository

class DeleteNonHumanAnimalFromRemoteRepository(
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository
) {
    suspend operator fun invoke(
        id: Int,
        caregiverId: String,
        onDeleteRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    ) {
        realtimeDatabaseRemoteNonHumanAnimalRepository.deleteRemoteNonHumanAnimal(
            id,
            caregiverId,
            onDeleteRemoteNonHumanAnimal
        )
    }
}
