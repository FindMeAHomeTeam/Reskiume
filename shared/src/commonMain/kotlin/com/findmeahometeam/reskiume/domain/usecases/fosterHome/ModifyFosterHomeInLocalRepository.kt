package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class ModifyFosterHomeInLocalRepository(
    private val manageImagePath: ManageImagePath,
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    operator fun invoke(
        isNonHumanAnimalSaved: Boolean = false,
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome,
        coroutineScope: CoroutineScope
    ): Flow<Boolean> = flow {

        val imageFileName =
            manageImagePath.getFileNameFromLocalImagePath(updatedFosterHome.imageUrl)

        val modifiedFosterHome = updatedFosterHome.copy(
            savedBy = getMyUid(),
            imageUrl = imageFileName
        )
        val isSuccess = localFosterHomeRepository.upsertAllFosterHomeData(
            fosterHomeEntity = modifiedFosterHome.toEntity(),
            allAcceptedNonHumanAnimals = modifiedFosterHome.allAcceptedNonHumanAnimals.map { it.toEntity() },
            allResidentNonHumanAnimalIds = modifiedFosterHome.allResidentNonHumanAnimals.map { it.toEntity() }
        ).first()

        if (isSuccess) {
            val isUpdated = manageAllResidentNonHumanAnimals(
                isNonHumanAnimalSaved,
                modifiedFosterHome,
                previousFosterHome,
                coroutineScope
            )
            emit(isUpdated)
        } else {
            emit(false)
        }
    }

    private suspend fun manageAllResidentNonHumanAnimals(
        isNonHumanAnimalSaved: Boolean,
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome,
        coroutineScope: CoroutineScope
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

                val residentNonHumanAnimal: NonHumanAnimal? =
                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        residentNonHumanAnimalToManage.nonHumanAnimalId,
                        residentNonHumanAnimalToManage.caregiverId,
                        coroutineScope
                    ).firstOrNull()

                if (updatedAllResidentNonHumanAnimals.contains(residentNonHumanAnimalToManage)) {

                    if (residentNonHumanAnimal == null) {
                        log.d(
                            "ModifyFosterHomeInLocalRepository",
                            "manageAllResidentNonHumanAnimals: Can not update the non human animal state for the resident id ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the foster home ${residentNonHumanAnimalToManage.fosterHomeId} in the local data source"
                        )
                    } else {
                        localNonHumanAnimalRepository.modifyNonHumanAnimal(
                            residentNonHumanAnimal
                                .copy(
                                    nonHumanAnimalState = NonHumanAnimalState.REHOMED,
                                    fosterHomeId = updatedFosterHome.id
                                ).toEntity()
                        ) { rowsUpdated ->
                            if (rowsUpdated > 0) {
                                log.d(
                                    "ModifyFosterHomeInLocalRepository",
                                    "manageAllResidentNonHumanAnimals: updated non human animal state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyFosterHomeInLocalRepository",
                                    "manageAllResidentNonHumanAnimals: failed to update the non human animal state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    }
                } else {
                    if (!isSuccess || residentNonHumanAnimal == null) {
                        log.d(
                            "ModifyFosterHomeInLocalRepository",
                            "manageAllResidentNonHumanAnimals: Can not modify the the non human animal state for the resident id ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the foster home ${residentNonHumanAnimalToManage.fosterHomeId} in the local data source"
                        )
                    } else {
                        val nonHumanAnimalState = if (isNonHumanAnimalSaved) {
                            NonHumanAnimalState.SAVED
                        } else {
                            NonHumanAnimalState.NEEDS_TO_BE_REHOMED
                        }
                        localNonHumanAnimalRepository.modifyNonHumanAnimal(
                            residentNonHumanAnimal
                                .copy(
                                    nonHumanAnimalState = nonHumanAnimalState,
                                    fosterHomeId = ""
                                ).toEntity()
                        ) { rowsUpdated ->
                            if (rowsUpdated > 0) {
                                log.d(
                                    "ModifyFosterHomeInLocalRepository",
                                    "manageAllResidentNonHumanAnimals: updated non human animal state $nonHumanAnimalState for the non human animal ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyFosterHomeInLocalRepository",
                                    "manageAllResidentNonHumanAnimals: failed to update the non human animal state $nonHumanAnimalState for the non human animal ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    }
                }
            }
        }
        return isSuccess
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
