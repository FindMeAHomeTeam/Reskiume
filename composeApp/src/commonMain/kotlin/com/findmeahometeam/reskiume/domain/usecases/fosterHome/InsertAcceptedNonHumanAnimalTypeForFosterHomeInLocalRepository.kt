package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalTypeForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class InsertAcceptedNonHumanAnimalTypeForFosterHomeInLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {
    suspend operator fun invoke(
        acceptedNonHumanAnimalType: AcceptedNonHumanAnimalTypeForFosterHome,
        onInsertAcceptedNonHumanAnimalType: (rowId: Long) -> Unit
    ) {
        localFosterHomeRepository.insertAcceptedNonHumanAnimalTypeForFosterHome(
            acceptedNonHumanAnimalType.toEntity(),
            onInsertAcceptedNonHumanAnimalType
        )
    }
}
