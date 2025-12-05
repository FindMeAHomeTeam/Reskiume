package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository

class DeleteAllNonHumanAnimalsFromLocalRepository(private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository) {
    suspend operator fun invoke(
        id: String,
        caregiverId: String,
        onDeleteAllNonHumanAnimals: (rowsDeleted: Int) -> Unit
    ) {
        localNonHumanAnimalRepository.deleteAllNonHumanAnimals(id, caregiverId, onDeleteAllNonHumanAnimals)
    }
}
