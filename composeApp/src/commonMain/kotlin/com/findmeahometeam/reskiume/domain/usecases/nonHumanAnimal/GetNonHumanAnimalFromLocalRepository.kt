package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository

class GetNonHumanAnimalFromLocalRepository(private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository) {
    suspend operator fun invoke(
        id: String
    ): NonHumanAnimal? =
        localNonHumanAnimalRepository.getNonHumanAnimal(id)?.toDomain()
}
