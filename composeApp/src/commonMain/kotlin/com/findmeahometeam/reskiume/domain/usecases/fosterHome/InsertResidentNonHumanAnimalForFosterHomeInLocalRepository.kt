package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class InsertResidentNonHumanAnimalForFosterHomeInLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {
    suspend operator fun invoke(
        residentNonHumanAnimal: ResidentNonHumanAnimalForFosterHome,
        onInsertResidentNonHumanAnimalId: (rowId: Long) -> Unit
    ) {
        localFosterHomeRepository.insertResidentNonHumanAnimalIdForFosterHome(
            residentNonHumanAnimal.toEntityForId(),
            onInsertResidentNonHumanAnimalId
        )
    }
}
