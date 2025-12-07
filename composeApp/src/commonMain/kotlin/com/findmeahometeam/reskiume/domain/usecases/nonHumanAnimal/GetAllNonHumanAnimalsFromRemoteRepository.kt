package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllNonHumanAnimalsFromRemoteRepository(
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository
) {
    operator fun invoke(
        caregiverId: String
    ): Flow<List<NonHumanAnimal>> =
        realtimeDatabaseRemoteNonHumanAnimalRepository.getAllRemoteNonHumanAnimals(caregiverId)
            .map { list ->
                list.map {
                    it.toDomain()
                }
            }
}
