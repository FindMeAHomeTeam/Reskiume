package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class InsertFosterHomeInLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fosterHome: FosterHome,
        onInsertFosterHome: (isSuccess: Boolean) -> Unit
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
        fosterHome.allResidentNonHumanAnimals.forEach { acceptedNonHumanAnimalGenderForFosterHome ->
            if (isSuccess) {
                localFosterHomeRepository.insertResidentNonHumanAnimalIdForFosterHome(
                    acceptedNonHumanAnimalGenderForFosterHome.toEntityForId(),
                    onInsertResidentNonHumanAnimalId = { rowId ->
                        if (rowId <= 0) {
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
