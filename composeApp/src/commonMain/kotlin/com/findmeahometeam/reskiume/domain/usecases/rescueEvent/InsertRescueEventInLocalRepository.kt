package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull

class InsertRescueEventInLocalRepository(
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val localRescueEventRepository: LocalRescueEventRepository,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val manageImagePath: ManageImagePath,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        rescueEvent: RescueEvent,
        coroutineScope: CoroutineScope,
        onInsertRescueEvent: suspend (isSuccess: Boolean) -> Unit
    ) {
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(rescueEvent.imageUrl)
        val createdRescueEvent = rescueEvent.copy(
            savedBy = getMyUid(),
            imageUrl = imageFileName
        )
        localRescueEventRepository.insertRescueEvent(
            createdRescueEvent.toEntity(),
            onInsertRescueEvent = { rowId ->
                if (rowId > 0) {
                    var isSuccess = insertAllNonHumanAnimalsToRescue(createdRescueEvent, coroutineScope)

                    if (isSuccess) {
                        isSuccess = insertAllNeedsToCover(createdRescueEvent)
                    }
                    onInsertRescueEvent(isSuccess)
                } else {
                    onInsertRescueEvent(false)
                }
            }
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""

    private suspend fun insertAllNonHumanAnimalsToRescue(
        rescueEvent: RescueEvent,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true
        rescueEvent.allNonHumanAnimalsToRescue.forEach { nonHumanAnimalToRescue: NonHumanAnimalToRescue ->
            if (isSuccess) {

                val nonHumanAnimal: NonHumanAnimal? =
                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        nonHumanAnimalToRescue.nonHumanAnimalId,
                        nonHumanAnimalToRescue.caregiverId,
                        coroutineScope
                    ).firstOrNull()

                if (nonHumanAnimal == null) {
                    log.d(
                        "InsertRescueEventInLocalRepository",
                        "insertAllNonHumanAnimalsToRescue: Can not insert nor update the adoption state for the non human animal id ${nonHumanAnimalToRescue.nonHumanAnimalId} in the rescue event id ${nonHumanAnimalToRescue.rescueEventId} in the local data source"
                    )
                } else {
                    localRescueEventRepository.insertNonHumanAnimalToRescueEntityForRescueEvent(
                        nonHumanAnimalToRescue.toEntity(),
                        onInsertNonHumanAnimalToRescueEntityForRescueEvent = { rowId ->
                            if (rowId > 0) {
                                log.d(
                                    "InsertRescueEventInLocalRepository",
                                    "insertAllNonHumanAnimalsToRescue: inserted the non human animal to rescue ${nonHumanAnimalToRescue.nonHumanAnimalId} in the rescue event ${nonHumanAnimalToRescue.rescueEventId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "InsertRescueEventInLocalRepository",
                                    "insertAllNonHumanAnimalsToRescue: failed to insert the non human animal to rescue ${nonHumanAnimalToRescue.nonHumanAnimalId} in the rescue event ${nonHumanAnimalToRescue.rescueEventId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                    if (isSuccess) {
                        localNonHumanAnimalRepository.modifyNonHumanAnimal(
                            nonHumanAnimal
                                .copy(
                                    adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED,
                                    fosterHomeId = ""
                                )
                                .toEntity()
                        ) { rowsUpdated ->
                            if (rowsUpdated > 0) {
                                log.d(
                                    "InsertRescueEventInLocalRepository",
                                    "insertAllNonHumanAnimalsToRescue: updated adoption state ${AdoptionState.NEEDS_TO_BE_RESCUED} for the non human animal ${nonHumanAnimal.id} in the local data source"
                                )
                            } else {
                                log.e(
                                    "InsertRescueEventInLocalRepository",
                                    "insertAllNonHumanAnimalsToRescue: failed to update the adoption state ${AdoptionState.NEEDS_TO_BE_RESCUED} for the non human animal ${nonHumanAnimal.id} in the local data source"
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

    private suspend fun insertAllNeedsToCover(rescueEvent: RescueEvent): Boolean {
        var isSuccess = true
        rescueEvent.allNeedsToCover.forEach { needToCover: NeedToCover ->
            if (isSuccess) {
                localRescueEventRepository.insertNeedToCoverEntityForRecueEvent(
                    needToCover.toEntity(),
                    onInsertNeedToCoverEntityForRecueEvent = { rowId ->
                        if (rowId > 0) {
                            log.d(
                                "InsertRescueEventInLocalRepository",
                                "insertAllNeedsToCover: inserted the need to cover ${needToCover.needToCoverId} in the rescue event ${needToCover.rescueEventId} in the local data source"
                            )
                        } else {
                            log.e(
                                "InsertRescueEventInLocalRepository",
                                "insertAllNeedsToCover: failed to insert the need to cover ${needToCover.needToCoverId} in the rescue event ${needToCover.rescueEventId} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }
}
