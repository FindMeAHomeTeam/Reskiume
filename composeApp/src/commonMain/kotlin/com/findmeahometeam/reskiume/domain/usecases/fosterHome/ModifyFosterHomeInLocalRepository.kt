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
                    var isSuccess = modifyAllAcceptedNonHumanAnimalTypes(fosterHome)

                    if (isSuccess) {
                        isSuccess = modifyAllAcceptedNonHumanAnimalGenders(fosterHome)

                        if(isSuccess){
                            isSuccess = modifyAllResidentNonHumanAnimals(fosterHome)
                        }
                    }
                    onModifyFosterHome(isSuccess)
                } else {
                    onModifyFosterHome(false)
                }
            }
        )
    }

    private suspend fun modifyAllAcceptedNonHumanAnimalTypes(fosterHome: FosterHome): Boolean {
        var isSuccess = true
        fosterHome.allAcceptedNonHumanAnimalTypes.forEach { acceptedNonHumanAnimalTypeForFosterHome ->
            if (isSuccess) {
                localFosterHomeRepository.modifyAcceptedNonHumanAnimalTypeForFosterHome(
                    acceptedNonHumanAnimalTypeForFosterHome.toEntity(),
                    onModifyAcceptedNonHumanAnimalType = { rowsUpdated ->
                        if (rowsUpdated <= 0) {
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }

    private suspend fun modifyAllAcceptedNonHumanAnimalGenders(fosterHome: FosterHome): Boolean {
        var isSuccess = true
        fosterHome.allAcceptedNonHumanAnimalGenders.forEach { acceptedNonHumanAnimalGenderForFosterHome ->
            if (isSuccess) {
                localFosterHomeRepository.modifyAcceptedNonHumanAnimalGenderForFosterHome(
                    acceptedNonHumanAnimalGenderForFosterHome.toEntity(),
                    onModifyAcceptedNonHumanAnimalGender = { rowsUpdated ->
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
