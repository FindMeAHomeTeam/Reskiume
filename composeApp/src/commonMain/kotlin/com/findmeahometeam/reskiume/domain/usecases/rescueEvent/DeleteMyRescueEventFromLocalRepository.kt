package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull

class DeleteMyRescueEventFromLocalRepository(
    private val localRescueEventRepository: LocalRescueEventRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        id: String,
        coroutineScope: CoroutineScope,
        onDeleteRescueEvent: suspend (rowsDeleted: Int) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(
            id,
            coroutineScope
        )
        if (isSuccess) {
            localRescueEventRepository.deleteRescueEvent(id, onDeleteRescueEvent)
        }
    }

    private suspend fun updateAdoptionStates(
        rescueEventId: String,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val rescueEventWithAllNeedsAndNonHumanAnimalData: RescueEventWithAllNeedsAndNonHumanAnimalData =
            localRescueEventRepository.getRescueEvent(rescueEventId)!!

        rescueEventWithAllNeedsAndNonHumanAnimalData.allNonHumanAnimalsToRescue.forEach { nonHumanAnimalToRescueEntity ->

            if (isSuccess) {
                val nonHumanAnimal: NonHumanAnimal? =
                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        nonHumanAnimalToRescueEntity.nonHumanAnimalId,
                        nonHumanAnimalToRescueEntity.caregiverId,
                        coroutineScope
                    ).firstOrNull()

                if (nonHumanAnimal == null) {
                    log.d(
                        "DeleteMyRescueEventFromLocalRepository",
                        "updateAdoptionStates: Can not update the adoption state for the non human animal ${nonHumanAnimalToRescueEntity.nonHumanAnimalId} in the rescue event ${nonHumanAnimalToRescueEntity.rescueEventId} because the non human animal has been unregistered in the local data source"
                    )
                } else {
                    localNonHumanAnimalRepository.modifyNonHumanAnimal(
                        nonHumanAnimal.copy(
                            adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                            fosterHomeId = ""
                        ).toEntity()
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "DeleteMyRescueEventFromLocalRepository",
                                "updateAdoptionStates: updated adoption state ${AdoptionState.LOOKING_FOR_ADOPTION} for the non human animal ${nonHumanAnimal.id} in the local data source"
                            )
                        } else {
                            log.e(
                                "DeleteMyRescueEventFromLocalRepository",
                                "updateAdoptionStates: failed to update the adoption state ${AdoptionState.LOOKING_FOR_ADOPTION} for the non human animal ${nonHumanAnimal.id} in the local data source"
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
