package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.remote.database.nonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository

class InsertNonHumanAnimalInRemoteRepository(
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository
) {
    suspend operator fun invoke(
        nonHumanAnimal: NonHumanAnimal,
        onInsertRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    ) {
        realtimeDatabaseRemoteNonHumanAnimalRepository.insertRemoteNonHumanAnimal(
            nonHumanAnimal.toData(),
            onInsertRemoteNonHumanAnimal
        )
    }
}
