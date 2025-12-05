package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.local.NonHumanAnimalRepository

class DeleteAllNonHumanAnimalsFromLocalRepository(private val nonHumanAnimalRepository: NonHumanAnimalRepository) {
    suspend operator fun invoke(
        id: String,
        caregiverId: String,
        onDeleteAllNonHumanAnimals: (rowsDeleted: Int) -> Unit
    ) {
        nonHumanAnimalRepository.deleteAllNonHumanAnimals(id, caregiverId, onDeleteAllNonHumanAnimals)
    }
}
