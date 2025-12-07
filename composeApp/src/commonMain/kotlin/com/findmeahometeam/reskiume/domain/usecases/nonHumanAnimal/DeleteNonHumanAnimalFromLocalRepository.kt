package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository

class DeleteNonHumanAnimalFromLocalRepository(private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository) {
    suspend operator fun invoke(
        id: Int,
        caregiverId: String,
        onDeleteNonHumanAnimal: (rowsDeleted: Int) -> Unit
    ) {
        localNonHumanAnimalRepository.deleteNonHumanAnimal(id, caregiverId, onDeleteNonHumanAnimal)
    }
}
