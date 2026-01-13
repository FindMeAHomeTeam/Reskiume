package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalGenderForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class InsertAcceptedNonHumanAnimalGenderForFosterHomeInLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {
    suspend operator fun invoke(
        acceptedNonHumanAnimalGender: AcceptedNonHumanAnimalGenderForFosterHome,
        onInsertAcceptedNonHumanAnimalGender: (rowId: Long) -> Unit
    ) {
        localFosterHomeRepository.insertAcceptedNonHumanAnimalGenderForFosterHome(
            acceptedNonHumanAnimalGender.toEntity(),
            onInsertAcceptedNonHumanAnimalGender
        )
    }
}
