package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.NonHumanAnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNonHumanAnimalFromLocalRepository(private val nonHumanAnimalRepository: NonHumanAnimalRepository) {
    operator fun invoke(
        id: String,
        caregiverId: String
    ): Flow<NonHumanAnimal?> =
        nonHumanAnimalRepository.getNonHumanAnimal(id, caregiverId).map { it?.toDomain() }
}
