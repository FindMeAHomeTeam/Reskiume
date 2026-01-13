package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalGenderForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class ModifyAcceptedNonHumanAnimalGenderForFosterHomeInLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {
    suspend operator fun invoke(
        acceptedNonHumanAnimalGender: AcceptedNonHumanAnimalGenderForFosterHome,
        onModifyAcceptedNonHumanAnimalGender: (rowsUpdated: Int) -> Unit
    ) {
        localFosterHomeRepository.modifyAcceptedNonHumanAnimalGenderForFosterHome(
            acceptedNonHumanAnimalGender.toEntity(),
            onModifyAcceptedNonHumanAnimalGender
        )
    }
}
