package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNonHumanAnimalFromRemoteRepository(
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository
) {
    operator fun invoke(
        id: String,
        caregiverId: String
    ): Flow<NonHumanAnimal?> =
        realtimeDatabaseRemoteNonHumanAnimalRepository.getRemoteNonHumanAnimal(
            id,
            caregiverId
        ).map {
            it?.toDomain()
        }
}
