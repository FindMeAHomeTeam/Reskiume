package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.database.nonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository

class DeleteAllNonHumanAnimalsFromRemoteRepository(
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository
) {
    suspend operator fun invoke(
        caregiverId: String,
        onDeleteAllRemoteNonHumanAnimals: (result: DatabaseResult) -> Unit
    ) {
        realtimeDatabaseRemoteNonHumanAnimalRepository.deleteAllRemoteNonHumanAnimals(
            caregiverId,
            onDeleteAllRemoteNonHumanAnimals
        )
    }
}
