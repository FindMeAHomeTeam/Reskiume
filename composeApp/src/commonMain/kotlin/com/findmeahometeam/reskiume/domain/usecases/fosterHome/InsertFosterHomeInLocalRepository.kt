package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class InsertFosterHomeInLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        fosterHome: FosterHome,
        onInsertFosterHome: suspend (isSuccess: Boolean) -> Unit
    ) {
        localFosterHomeRepository.insertFosterHome(
            fosterHome.copy(savedBy = getMyUid()).toEntity(),
            onInsertFosterHome = { rowId ->
                if (rowId > 0) {
                    var isSuccess = insertAllAcceptedNonHumanAnimals(fosterHome)

                    if (isSuccess) {
                        isSuccess = insertAllResidentNonHumanAnimals(fosterHome)
                    }
                    onInsertFosterHome(isSuccess)
                } else {
                    onInsertFosterHome(false)
                }
            }
        )
    }

    private suspend fun insertAllAcceptedNonHumanAnimals(fosterHome: FosterHome): Boolean {
        var isSuccess = true
        fosterHome.allAcceptedNonHumanAnimals.forEach { acceptedNonHumanAnimalForFosterHome ->
            if (isSuccess) {
                localFosterHomeRepository.insertAcceptedNonHumanAnimalForFosterHome(
                    acceptedNonHumanAnimalForFosterHome.toEntity(),
                    onInsertAcceptedNonHumanAnimalType = { rowId ->
                        if (rowId <= 0) {
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }

    private suspend fun insertAllResidentNonHumanAnimals(fosterHome: FosterHome): Boolean {
        var isSuccess = true
        fosterHome.allResidentNonHumanAnimals.forEach { residentNonHumanAnimalForFosterHome ->
            if (isSuccess) {
                localFosterHomeRepository.insertResidentNonHumanAnimalIdForFosterHome(
                    residentNonHumanAnimalForFosterHome.toEntityForId(),
                    onInsertResidentNonHumanAnimalId = { rowId ->
                        if (rowId <= 0) {
                            isSuccess = false
                        }
                    }
                )
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    residentNonHumanAnimalForFosterHome.residentNonHumanAnimal!!.copy(
                        adoptionState = AdoptionState.REHOMED,
                        fosterHomeId = fosterHome.id
                    ).toEntity()
                ) { rowsUpdated ->
                    if (rowsUpdated > 0) {
                        log.d(
                            "InsertFosterHomeInLocalRepository",
                            "insertAllResidentNonHumanAnimals: updated adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} in the local data source"
                        )
                    } else {
                        log.e(
                            "InsertFosterHomeInLocalRepository",
                            "insertAllResidentNonHumanAnimals: failed to update the adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} in the local data source"
                        )
                        isSuccess = false
                    }
                }
            }
        }
        return isSuccess
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
