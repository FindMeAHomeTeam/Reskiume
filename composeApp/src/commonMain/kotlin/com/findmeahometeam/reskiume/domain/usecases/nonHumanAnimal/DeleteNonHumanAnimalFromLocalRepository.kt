package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.local.NonHumanAnimalRepository

class DeleteNonHumanAnimalFromLocalRepository(private val nonHumanAnimalRepository: NonHumanAnimalRepository) {
    suspend operator fun invoke(
        id: String,
        caregiverId: String,
        onDeleteNonHumanAnimal: (rowsDeleted: Int) -> Unit
    ) {
        nonHumanAnimalRepository.deleteNonHumanAnimal(id, caregiverId, onDeleteNonHumanAnimal)
    }
}
