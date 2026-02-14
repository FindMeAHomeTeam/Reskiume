package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.firstOrNull

class DeleteFosterHomeFromLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val log: Log
) {
    suspend operator fun invoke(
        id: String,
        onDeleteFosterHome: suspend (rowsDeleted: Int) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(id)

        if (isSuccess) {
            localFosterHomeRepository.deleteFosterHome(id, onDeleteFosterHome)
        }
    }

    private suspend fun updateAdoptionStates(fosterHomeId: String): Boolean {
        var isSuccess = true

        val fosterHome: FosterHomeWithAllNonHumanAnimalData = localFosterHomeRepository.getFosterHome(fosterHomeId) ?: return false

        fosterHome.allResidentNonHumanAnimalIds.forEach { residentNonHumanAnimalForFosterHome ->
            if (isSuccess) {
                val residentNonHumanAnimalForFosterHome = residentNonHumanAnimalForFosterHome.toDomain(
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
                            "DeleteFosterHomeFromLocalRepository",
                            "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} in the local data source"
                        )
                    } else {
                        log.e(
                            "DeleteFosterHomeFromLocalRepository",
                            "updateAdoptionStates: failed to update the adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} in the local data source"
                        )
                        isSuccess = false
                    }
                }
            }
        }
        return isSuccess
    }
}
