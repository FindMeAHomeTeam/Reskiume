package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteAllMyFosterHomesFromLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val log: Log
) {

    suspend operator fun invoke(
        ownerId: String,
        onDeleteAllMyFosterHomes: (rowsDeleted: Int) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(ownerId)

        if (isSuccess) {
            localFosterHomeRepository.deleteAllMyFosterHomes(ownerId, onDeleteAllMyFosterHomes)
        }
    }

    private suspend fun updateAdoptionStates(ownerId: String): Boolean {
        var isSuccess = true

        val fosterHomesWithAllNonHumanAnimalData: List<FosterHomeWithAllNonHumanAnimalData> =
            localFosterHomeRepository.getAllMyFosterHomes(ownerId).first()

        fosterHomesWithAllNonHumanAnimalData.forEach { fosterHome ->

            fosterHome.allResidentNonHumanAnimalIds.forEach { residentNonHumanAnimalForFosterHome ->
                if (isSuccess) {
                    val residentNonHumanAnimalForFosterHome =
                        residentNonHumanAnimalForFosterHome.toDomain(
                            onFetchNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->

                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                    nonHumanAnimalId = nonHumanAnimalId,
                                    caregiverId = caregiverId
                                ).firstOrNull()
                            }
                        )
                    localNonHumanAnimalRepository.modifyNonHumanAnimal(
                        residentNonHumanAnimalForFosterHome.residentNonHumanAnimal!!.copy(
                            adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                            fosterHomeId = ""
                        ).toEntity()
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "DeleteAllMyFosterHomesFromLocalRepository",
                                "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} to ${AdoptionState.LOOKING_FOR_ADOPTION} in the local data source"
                            )
                        } else {
                            log.e(
                                "DeleteAllMyFosterHomesFromLocalRepository",
                                "updateAdoptionStates: failed to update the adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                }
            }
        }
        return isSuccess
    }
}
