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

class ModifyRescueEventInLocalRepository(
    private val manageImagePath: ManageImagePath,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val localRescueEventRepository: LocalRescueEventRepository,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        updatedRescueEvent: RescueEvent,
        previousRescueEvent: RescueEvent,
        coroutineScope: CoroutineScope,
        onModifyRescueEvent: suspend (isUpdated: Boolean) -> Unit
    ) {
        val imageFileName =
            manageImagePath.getFileNameFromLocalImagePath(updatedRescueEvent.imageUrl)
        val modifiedRescueEvent = updatedRescueEvent.copy(
            savedBy = getMyUid(),
            imageUrl = imageFileName
        )
        localRescueEventRepository.modifyRescueEvent(
            modifiedRescueEvent.toEntity(),
            onModifyRescueEvent = { rowsUpdated ->
                if (rowsUpdated > 0) {
                    var isSuccess =
                        manageAllNonHumanAnimalsToRescue(
                            previousRescueEvent,
                            modifiedRescueEvent,
                            coroutineScope
                        )

                    if (isSuccess) {
                        isSuccess = manageAllNeedsToCover(previousRescueEvent, modifiedRescueEvent)
                    }
                    onModifyRescueEvent(isSuccess)
                } else {
                    onModifyRescueEvent(false)
                }
            }
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""

    private suspend fun manageAllNonHumanAnimalsToRescue(
        previousRescueEvent: RescueEvent,
        updatedRescueEvent: RescueEvent,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val previousAllNonHumanAnimalsToRescue =
            previousRescueEvent.allNonHumanAnimalsToRescue.toSet()

        val updatedAllNonHumanAnimalsToRescue =
            updatedRescueEvent.allNonHumanAnimalsToRescue.toSet()

        val nonHumanAnimalsToRescueToManage: Set<NonHumanAnimalToRescue> =
            (previousAllNonHumanAnimalsToRescue - updatedAllNonHumanAnimalsToRescue) +
                    (updatedAllNonHumanAnimalsToRescue - previousAllNonHumanAnimalsToRescue)

        nonHumanAnimalsToRescueToManage.forEach { nonHumanAnimalToRescueToManage ->
            if (isSuccess) {

                val nonHumanAnimal: NonHumanAnimal? =
                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        nonHumanAnimalToRescueToManage.nonHumanAnimalId,
                        nonHumanAnimalToRescueToManage.caregiverId,
                        coroutineScope
                    ).firstOrNull()

                if (nonHumanAnimal == null) {
                    log.d(
                        "ModifyRescueEventInLocalRepository",
                        "manageAllNonHumanAnimalsToRescue: Can not update the adoption state for the non human animal id ${nonHumanAnimalToRescueToManage.nonHumanAnimalId} in the rescue event id ${nonHumanAnimalToRescueToManage.rescueEventId} in the local data source"
                    )
                }

                if (nonHumanAnimal != null
                    && updatedAllNonHumanAnimalsToRescue.contains(nonHumanAnimalToRescueToManage)
                ) {
                    val nonHumanAnimalToRescueEntity =
                        updatedRescueEvent.allNonHumanAnimalsToRescue.first {
                            it.nonHumanAnimalId == nonHumanAnimalToRescueToManage.nonHumanAnimalId
                        }.toEntity()

                    localRescueEventRepository.insertNonHumanAnimalToRescueEntityForRescueEvent(
                        nonHumanAnimalToRescueEntity,
                        onInsertNonHumanAnimalToRescueEntityForRescueEvent = { rowId ->
                            if (rowId > 0) {
                                log.d(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: inserted the non human animal to rescue ${nonHumanAnimalToRescueEntity.nonHumanAnimalId} in the rescue event ${nonHumanAnimalToRescueEntity.rescueEventId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: failed to insert the non human animal to rescue ${nonHumanAnimalToRescueEntity.nonHumanAnimalId} in the rescue event ${nonHumanAnimalToRescueEntity.rescueEventId} in the local data source"
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
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: updated adoption state ${AdoptionState.NEEDS_TO_BE_RESCUED} for the non human animal ${nonHumanAnimal.id} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: failed to update the adoption state ${AdoptionState.NEEDS_TO_BE_RESCUED} for the non human animal ${nonHumanAnimal.id} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    }
                } else if (
                    !updatedAllNonHumanAnimalsToRescue.contains(nonHumanAnimalToRescueToManage)
                ) {
                    val nonHumanAnimalToRescue =
                        previousRescueEvent.allNonHumanAnimalsToRescue.first {
                            it.nonHumanAnimalId == nonHumanAnimalToRescueToManage.nonHumanAnimalId
                        }

                    localRescueEventRepository.deleteNonHumanAnimalToRescueEntityForRescueEvent(
                        nonHumanAnimalToRescue.nonHumanAnimalId,
                        onDeleteNonHumanAnimalToRescueEntityForRescueEvent = { rowsDeleted ->
                            if (rowsDeleted > 0) {
                                log.d(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: deleted the non human animal to rescue ${nonHumanAnimalToRescue.nonHumanAnimalId} in the rescue event ${nonHumanAnimalToRescue.rescueEventId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: failed to delete the non human animal to rescue ${nonHumanAnimalToRescue.nonHumanAnimalId} in the rescue event ${nonHumanAnimalToRescue.rescueEventId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                    if (nonHumanAnimal != null
                        && nonHumanAnimal.adoptionState != AdoptionState.ADOPTED
                        && isSuccess
                    ) {
                        localNonHumanAnimalRepository.modifyNonHumanAnimal(
                            nonHumanAnimal
                                .copy(
                                    adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                                    fosterHomeId = ""
                                )
                                .toEntity()
                        ) { rowsUpdated ->
                            if (rowsUpdated > 0) {
                                log.d(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: updated adoption state ${AdoptionState.LOOKING_FOR_ADOPTION} for the non human animal ${nonHumanAnimal.id} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: failed to update the adoption state ${AdoptionState.LOOKING_FOR_ADOPTION} for the non human animal ${nonHumanAnimal.id} in the local data source"
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

    private suspend fun manageAllNeedsToCover(
        previousRescueEvent: RescueEvent,
        updatedRescueEvent: RescueEvent
    ): Boolean {
        var isSuccess = true

        val previousAllNeedsToCover =
            previousRescueEvent.allNeedsToCover.toSet()

        val updatedAllNeedsToCover =
            updatedRescueEvent.allNeedsToCover.toSet()

        val needsToCoverToManage: Set<NeedToCover> =
            (previousAllNeedsToCover - updatedAllNeedsToCover) +
                    (updatedAllNeedsToCover - previousAllNeedsToCover)

        needsToCoverToManage.forEach { needsToCoverToManage ->
            if (isSuccess) {
                if (updatedAllNeedsToCover.contains(needsToCoverToManage)) {

                    val needToCoverEntity =
                        updatedRescueEvent.allNeedsToCover.first {
                            it.needToCoverId == needsToCoverToManage.needToCoverId
                        }.toEntity()

                    localRescueEventRepository.insertNeedToCoverEntityForRecueEvent(
                        needToCoverEntity,
                        onInsertNeedToCoverEntityForRecueEvent = { rowId ->
                            if (rowId > 0) {
                                log.d(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNeedsToCover: inserted the need to cover id ${needToCoverEntity.needToCoverId} in the rescue event ${needToCoverEntity.rescueEventId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNeedsToCover: failed to insert the need to cover id ${needToCoverEntity.needToCoverId} in the rescue event ${needToCoverEntity.rescueEventId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                } else {
                    val needToCover =
                        previousRescueEvent.allNeedsToCover.first {
                            it.needToCoverId == needsToCoverToManage.needToCoverId
                        }

                    localRescueEventRepository.deleteNeedToCoverEntityForRecueEvent(
                        needToCover.needToCoverId,
                        onDeleteNeedToCoverEntityForRecueEvent = { rowsDeleted ->
                            if (rowsDeleted > 0) {
                                log.d(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNeedsToCover: deleted the need to cover id ${needToCover.needToCoverId} in the rescue event ${needToCover.rescueEventId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNeedsToCover: failed to delete the need to cover id ${needToCover.needToCoverId} in the rescue event ${needToCover.rescueEventId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                }
            }
        }
        return isSuccess
    }
}
