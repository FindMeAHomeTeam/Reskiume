package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class ModifyResidentNonHumanAnimalForFosterHomeInLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {
    suspend operator fun invoke(
        residentNonHumanAnimal: ResidentNonHumanAnimalForFosterHome,
        onModifyResidentNonHumanAnimalId: (rowsUpdated: Int) -> Unit
    ) {
        localFosterHomeRepository.modifyResidentNonHumanAnimalIdForFosterHome(
            residentNonHumanAnimal.toEntityForId(),
            onModifyResidentNonHumanAnimalId
        )
    }
}
