package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class ModifyFosterHomeInLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fosterHome: FosterHome,
        onModifyFosterHome: (isUpdated: Boolean) -> Unit
    ) {
        localFosterHomeRepository.modifyFosterHome(
            fosterHome.copy(savedBy = getMyUid()).toEntity(),
            onModifyFosterHome = { rowsUpdated ->
                if (rowsUpdated > 0) {
                    var isSuccess = modifyAllAcceptedNonHumanAnimals(fosterHome)

                    if (isSuccess) {
                        isSuccess = modifyAllResidentNonHumanAnimals(fosterHome)
                    }
                    onModifyFosterHome(isSuccess)
                } else {
                    onModifyFosterHome(false)
                }
            }
        )
    }

    private suspend fun modifyAllAcceptedNonHumanAnimals(fosterHome: FosterHome): Boolean {
        var isSuccess = true
        fosterHome.allAcceptedNonHumanAnimals.forEach { acceptedNonHumanAnimalForFosterHome ->
            if (isSuccess) {
                localFosterHomeRepository.modifyAcceptedNonHumanAnimalForFosterHome(
                    acceptedNonHumanAnimalForFosterHome.toEntity(),
                    onModifyAcceptedNonHumanAnimal = { rowsUpdated ->
                        if (rowsUpdated <= 0) {
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }

    private suspend fun modifyAllResidentNonHumanAnimals(fosterHome: FosterHome): Boolean {
        var isSuccess = true
        fosterHome.allResidentNonHumanAnimals.forEach { acceptedNonHumanAnimalGenderForFosterHome ->
            if (isSuccess) {
                localFosterHomeRepository.modifyResidentNonHumanAnimalIdForFosterHome(
                    acceptedNonHumanAnimalGenderForFosterHome.toEntityForId(),
                    onModifyResidentNonHumanAnimalId = { rowsUpdated ->
                        if (rowsUpdated <= 0) {
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
