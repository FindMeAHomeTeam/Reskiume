package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull

class InsertFosterHomeInLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val manageImagePath: ManageImagePath,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope,
        onInsertFosterHome: suspend (isSuccess: Boolean) -> Unit
    ) {
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(fosterHome.imageUrl)
        val createdFosterHome = fosterHome.copy(
            savedBy = getMyUid(),
            imageUrl = imageFileName
        )
        localFosterHomeRepository.insertFosterHome(
            createdFosterHome.toEntity(),
            onInsertFosterHome = { rowId ->
                if (rowId > 0) {
                    var isSuccess = insertAllAcceptedNonHumanAnimals(createdFosterHome)

                    if (isSuccess) {
                        isSuccess = insertAllResidentNonHumanAnimals(createdFosterHome, coroutineScope)
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
                        if (rowId > 0) {
                            log.d(
                                "InsertFosterHomeInLocalRepository",
                                "insertAllAcceptedNonHumanAnimals: inserted the accepted non human animal ${acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalId} in the foster home ${acceptedNonHumanAnimalForFosterHome.fosterHomeId} in the local data source"
                            )
                        } else {
                            log.e(
                                "InsertFosterHomeInLocalRepository",
                                "insertAllAcceptedNonHumanAnimals: failed to insert the accepted non human animal ${acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalId} in the foster home ${acceptedNonHumanAnimalForFosterHome.fosterHomeId} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }

    private suspend fun insertAllResidentNonHumanAnimals(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true
        fosterHome.allResidentNonHumanAnimals.forEach { residentNonHumanAnimalForFosterHome ->
            if (isSuccess) {

                val residentNonHumanAnimal: NonHumanAnimal? =
                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        residentNonHumanAnimalForFosterHome.nonHumanAnimalId,
                        residentNonHumanAnimalForFosterHome.caregiverId,
                        coroutineScope
                    ).firstOrNull()

                if (residentNonHumanAnimal == null) {
                    log.d(
                        "InsertFosterHomeInLocalRepository",
                        "insertAllResidentNonHumanAnimals: Can not insert the resident nor update the adoption state for the resident id ${residentNonHumanAnimalForFosterHome.nonHumanAnimalId} in the foster home ${residentNonHumanAnimalForFosterHome.fosterHomeId} in the local data source"
                    )
                } else {
                    localFosterHomeRepository.insertResidentNonHumanAnimalIdForFosterHome(
                        residentNonHumanAnimalForFosterHome.toEntity(),
                        onInsertResidentNonHumanAnimalId = { rowId ->
                            if (rowId > 0) {
                                log.d(
                                    "InsertFosterHomeInLocalRepository",
                                    "insertAllResidentNonHumanAnimals: inserted the non human animal ${residentNonHumanAnimal.id} in the foster home ${residentNonHumanAnimal.fosterHomeId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "InsertFosterHomeInLocalRepository",
                                    "insertAllResidentNonHumanAnimals: failed to insert the non human animal ${residentNonHumanAnimal.id} in the foster home ${residentNonHumanAnimal.fosterHomeId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                    if (isSuccess) {
                        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(
                            residentNonHumanAnimal.imageUrl
                        )
                        localNonHumanAnimalRepository.modifyNonHumanAnimal(
                            residentNonHumanAnimal.copy(
                                adoptionState = AdoptionState.REHOMED,
                                fosterHomeId = fosterHome.id,
                                imageUrl = imageFileName
                            ).toEntity()
                        ) { rowsUpdated ->
                            if (rowsUpdated > 0) {
                                log.d(
                                    "InsertFosterHomeInLocalRepository",
                                    "insertAllResidentNonHumanAnimals: updated adoption state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
                                )
                            } else {
                                log.e(
                                    "InsertFosterHomeInLocalRepository",
                                    "insertAllResidentNonHumanAnimals: failed to update the adoption state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
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
