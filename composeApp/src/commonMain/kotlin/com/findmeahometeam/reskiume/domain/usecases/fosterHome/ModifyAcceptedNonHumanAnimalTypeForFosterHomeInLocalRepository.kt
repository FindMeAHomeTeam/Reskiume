package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalTypeForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class ModifyAcceptedNonHumanAnimalTypeForFosterHomeInLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {
    suspend operator fun invoke(
        acceptedNonHumanAnimalType: AcceptedNonHumanAnimalTypeForFosterHome,
        onModifyAcceptedNonHumanAnimalType: (rowsUpdated: Int) -> Unit
    ) {
        localFosterHomeRepository.modifyAcceptedNonHumanAnimalTypeForFosterHome(
            acceptedNonHumanAnimalType.toEntity(),
            onModifyAcceptedNonHumanAnimalType
        )
    }
}
