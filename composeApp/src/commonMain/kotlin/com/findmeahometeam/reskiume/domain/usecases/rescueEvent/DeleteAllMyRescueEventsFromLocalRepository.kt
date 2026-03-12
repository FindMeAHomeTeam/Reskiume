package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteAllMyRescueEventsFromLocalRepository(
    private val localRescueEventRepository: LocalRescueEventRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val log: Log,
) {
    suspend operator fun invoke(
        creatorId: String,
        coroutineScope: CoroutineScope,
        onDeleteAllMyRescueEvents: (rowsDeleted: Int) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(
            creatorId,
            coroutineScope
        )
        if (isSuccess) {
            localRescueEventRepository.deleteAllMyRescueEvents(creatorId, onDeleteAllMyRescueEvents)
        }
    }

    private suspend fun updateAdoptionStates(
        creatorId: String,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val rescueEventWithAllNeedsAndNonHumanAnimalData: List<RescueEventWithAllNeedsAndNonHumanAnimalData> =
            localRescueEventRepository.getAllMyRescueEvents(creatorId).first()

        rescueEventWithAllNeedsAndNonHumanAnimalData.forEach { rescueEventWithAllNonHumanAnimalData ->
            rescueEventWithAllNonHumanAnimalData.allNonHumanAnimalsToRescue.forEach { nonHumanAnimalToRescueEntity ->

                if (isSuccess) {
                    val residentNonHumanAnimal: NonHumanAnimal? =
                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                            nonHumanAnimalToRescueEntity.nonHumanAnimalId,
                            nonHumanAnimalToRescueEntity.caregiverId,
                            coroutineScope
                        ).firstOrNull()

                    if (residentNonHumanAnimal == null) {
                        log.d(
                            "DeleteAllMyRescueEventsFromLocalRepository",
                            "updateAdoptionStates: Can not update the adoption state for the non human animal ${nonHumanAnimalToRescueEntity.nonHumanAnimalId} in the rescue event ${nonHumanAnimalToRescueEntity.rescueEventId} because the non human animal has been unregistered in the local data source"
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
                                    "DeleteAllMyRescueEventsFromLocalRepository",
                                    "updateAdoptionStates: updated adoption state ${AdoptionState.LOOKING_FOR_ADOPTION} for the non human animal ${residentNonHumanAnimal.id} in the local data source"
                                )
                            } else {
                                log.e(
                                    "DeleteAllMyRescueEventsFromLocalRepository",
                                    "updateAdoptionStates: failed to update the adoption state ${AdoptionState.LOOKING_FOR_ADOPTION} for the non human animal ${residentNonHumanAnimal.id} in the local data source"
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
}
