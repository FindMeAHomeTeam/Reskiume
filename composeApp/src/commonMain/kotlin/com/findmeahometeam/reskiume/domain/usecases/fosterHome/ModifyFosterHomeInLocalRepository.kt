package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.firstOrNull

class ModifyFosterHomeInLocalRepository(
    private val manageImagePath: ManageImagePath,
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome,
        onModifyFosterHome: suspend (isUpdated: Boolean) -> Unit
    ) {
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(updatedFosterHome.imageUrl)
        val modifiedFosterHome = updatedFosterHome.copy(
            savedBy = getMyUid(),
            imageUrl = imageFileName
        )
        localFosterHomeRepository.modifyFosterHome(
            modifiedFosterHome.toEntity(),
            onModifyFosterHome = { rowsUpdated ->
                if (rowsUpdated > 0) {
                    var isSuccess = manageAllAcceptedNonHumanAnimals(modifiedFosterHome, previousFosterHome)

                    if (isSuccess) {
                        isSuccess = manageAllResidentNonHumanAnimals(modifiedFosterHome, previousFosterHome)
                    }
                    onModifyFosterHome(isSuccess)
                } else {
                    onModifyFosterHome(false)
                }
            }
        )
    }

    private suspend fun manageAllAcceptedNonHumanAnimals(
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome
    ): Boolean {
        var isSuccess = true

        val previousAllAcceptedNonHumanAnimals =
            previousFosterHome.allAcceptedNonHumanAnimals.map {
                Pair(
                    it.acceptedNonHumanAnimalType,
                    it.acceptedNonHumanAnimalGender
                )
            }.toSet()

        val updatedAllAcceptedNonHumanAnimals =
            updatedFosterHome.allAcceptedNonHumanAnimals.map {
                Pair(
                    it.acceptedNonHumanAnimalType,
                    it.acceptedNonHumanAnimalGender
                )
            }.toSet()

        val acceptedNonHumanAnimalsToManage: Set<Pair<NonHumanAnimalType, Gender>> =
            (previousAllAcceptedNonHumanAnimals - updatedAllAcceptedNonHumanAnimals) +
                    (updatedAllAcceptedNonHumanAnimals - previousAllAcceptedNonHumanAnimals)

        acceptedNonHumanAnimalsToManage.forEach { acceptedNonHumanAnimalToManage ->
            if (isSuccess) {
                if (updatedAllAcceptedNonHumanAnimals.contains(acceptedNonHumanAnimalToManage)) {

                    val acceptedNonHumanAnimal =
                        updatedFosterHome.allAcceptedNonHumanAnimals.first {
                            it.acceptedNonHumanAnimalType == acceptedNonHumanAnimalToManage.first
                                    && it.acceptedNonHumanAnimalGender == acceptedNonHumanAnimalToManage.second
                        }.toEntity()

                    localFosterHomeRepository.insertAcceptedNonHumanAnimalForFosterHome(
                        acceptedNonHumanAnimal,
                        onInsertAcceptedNonHumanAnimalType = { rowId ->
                            if (rowId <= 0) {
                                isSuccess = false
                            }
                        }
                    )
                } else {
                    val acceptedNonHumanAnimal =
                        previousFosterHome.allAcceptedNonHumanAnimals.first {
                            it.acceptedNonHumanAnimalType == acceptedNonHumanAnimalToManage.first
                                    && it.acceptedNonHumanAnimalGender == acceptedNonHumanAnimalToManage.second
                        }

                    localFosterHomeRepository.deleteAcceptedNonHumanAnimal(
                        acceptedNonHumanAnimal.acceptedNonHumanAnimalId,
                        onDeleteAcceptedNonHumanAnimal = { rowsDeleted ->
                            if (rowsDeleted <= 0) {
                                isSuccess = false
                            }
                        }
                    )
                }
            }
        }
        return isSuccess
    }

    private suspend fun manageAllResidentNonHumanAnimals(
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome
    ): Boolean {
        var isSuccess = true

        val updatedAllResidentNonHumanAnimals =
            updatedFosterHome.allResidentNonHumanAnimals.toSet()

        val previousAllResidentNonHumanAnimals =
            previousFosterHome.allResidentNonHumanAnimals.toSet()

        val allResidentNonHumanAnimalsToManage =
            (previousAllResidentNonHumanAnimals - updatedAllResidentNonHumanAnimals) +
                    (updatedAllResidentNonHumanAnimals - previousAllResidentNonHumanAnimals)

        allResidentNonHumanAnimalsToManage.forEach { residentNonHumanAnimalToManage ->
            if (isSuccess) {

                if (updatedAllResidentNonHumanAnimals.contains(residentNonHumanAnimalToManage)) {

                    localFosterHomeRepository.insertResidentNonHumanAnimalIdForFosterHome(
                        residentNonHumanAnimalToManage.toEntityForId(),
                        onInsertResidentNonHumanAnimalId = { rowId ->
                            if (rowId <= 0) {
                                isSuccess = false
                            }
                        }
                    )

                    localNonHumanAnimalRepository.modifyNonHumanAnimal(
                        residentNonHumanAnimalToManage.residentNonHumanAnimal!!
                            .copy(
                                adoptionState = AdoptionState.REHOMED,
                                fosterHomeId = updatedFosterHome.id
                            )
                            .toEntity()
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "ModifyFosterHomeInLocalRepository",
                                "manageAllResidentNonHumanAnimals: updated adoption state for the non human animal ${residentNonHumanAnimalToManage.residentNonHumanAnimal.id} in the local data source"
                            )
                        } else {
                            log.e(
                                "ModifyFosterHomeInLocalRepository",
                                "manageAllResidentNonHumanAnimals: failed to update the adoption state for the non human animal ${residentNonHumanAnimalToManage.residentNonHumanAnimal.id} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                } else {
                    localFosterHomeRepository.deleteResidentNonHumanAnimal(
                        residentNonHumanAnimalToManage.residentNonHumanAnimal!!.id,
                        onDeleteResidentNonHumanAnimalId = { rowsDeleted ->
                            if (rowsDeleted <= 0) {
                                isSuccess = false
                            }
                        }
                    )

                    localNonHumanAnimalRepository.modifyNonHumanAnimal(
                        residentNonHumanAnimalToManage.residentNonHumanAnimal
                            .copy(
                                adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                                fosterHomeId = ""
                            )
                            .toEntity()
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "ModifyFosterHomeInLocalRepository",
                                "manageAllResidentNonHumanAnimals: updated adoption state for the non human animal ${residentNonHumanAnimalToManage.residentNonHumanAnimal.id} in the local data source"
                            )
                        } else {
                            log.e(
                                "ModifyFosterHomeInLocalRepository",
                                "manageAllResidentNonHumanAnimals: failed to update the adoption state for the non human animal ${residentNonHumanAnimalToManage.residentNonHumanAnimal.id} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                }
            }
        }
        return isSuccess
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
