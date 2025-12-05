package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.remote.database.nonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository

class ModifyNonHumanAnimalInRemoteRepository(
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository
) {
    suspend operator fun invoke(
        nonHumanAnimal: NonHumanAnimal,
        onModifyRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    ) {
        realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
            nonHumanAnimal.toData(),
            onModifyRemoteNonHumanAnimal
        )
    }
}
