package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllMyNonHumanAnimalsFromLocalRepository(private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository) {
    operator fun invoke(caregiverId: String): Flow<List<NonHumanAnimal>> =
        localNonHumanAnimalRepository.getAllMyNonHumanAnimals(caregiverId)
            .map { list ->
                list.map {
                    it.toDomain()
                }
            }
}
