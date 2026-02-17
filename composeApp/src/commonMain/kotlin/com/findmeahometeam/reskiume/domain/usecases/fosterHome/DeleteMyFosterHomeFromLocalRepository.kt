package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull

class DeleteMyFosterHomeFromLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val log: Log
) {
    suspend operator fun invoke(
        id: String,
        coroutineScope: CoroutineScope,
        onDeleteFosterHome: suspend (rowsDeleted: Int) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(
            id,
            coroutineScope
        )

        if (isSuccess) {
            localFosterHomeRepository.deleteFosterHome(id, onDeleteFosterHome)
        }
    }

    private suspend fun updateAdoptionStates(
        fosterHomeId: String,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val fosterHome: FosterHomeWithAllNonHumanAnimalData = localFosterHomeRepository.getFosterHome(fosterHomeId)!!

        fosterHome.allResidentNonHumanAnimalIds.forEach { residentNonHumanAnimalForFosterHome ->
            if (isSuccess) {
                val residentNonHumanAnimal: NonHumanAnimal? =
                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        residentNonHumanAnimalForFosterHome.nonHumanAnimalId,
                        residentNonHumanAnimalForFosterHome.caregiverId,
                        coroutineScope
                    ).firstOrNull()

                if (residentNonHumanAnimal == null) {
                    log.d(
                        "DeleteMyFosterHomeFromLocalRepository",
                        "updateAdoptionStates: Can not update the adoption state for the non human animal in the foster home $fosterHomeId because the non human animal has been unregistered in the local data source"
                    )
                } else {
                    localNonHumanAnimalRepository.modifyNonHumanAnimal(
                        residentNonHumanAnimal.copy(
                            adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                            fosterHomeId = ""
                        ).toEntity()
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "DeleteMyFosterHomeFromLocalRepository",
                                "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
                            )
                        } else {
                            log.e(
                                "DeleteMyFosterHomeFromLocalRepository",
                                "updateAdoptionStates: failed to update the adoption state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
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
