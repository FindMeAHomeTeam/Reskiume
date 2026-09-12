package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class ModifyRescueEventInLocalRepository(
    private val manageImagePath: ManageImagePath,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val localRescueEventRepository: LocalRescueEventRepository,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    operator fun invoke(
        isNonHumanAnimalSaved: Boolean = false,
        updatedRescueEvent: RescueEvent,
        previousRescueEvent: RescueEvent,
        coroutineScope: CoroutineScope
    ): Flow<Boolean> = flow {
        val imageFileName =
            manageImagePath.getFileNameFromLocalImagePath(updatedRescueEvent.imageUrl)
        val modifiedRescueEvent = updatedRescueEvent.copy(
            savedBy = getMyUid(),
            imageUrl = imageFileName
        )
        val isSuccess = localRescueEventRepository.upsertAllRescueEventData(
            rescueEventEntity = modifiedRescueEvent.toEntity(),
            allNonHumanAnimals = modifiedRescueEvent.allNonHumanAnimalsToRescue.map { it.toEntity() },
            allNeedsToCover = modifiedRescueEvent.allNeedsToCover.map { it.toEntity() }
        ).first()

        if (isSuccess) {
            val isUpdated = manageAllNonHumanAnimalsToRescue(
                isNonHumanAnimalSaved,
                previousRescueEvent,
                modifiedRescueEvent,
                coroutineScope
            )
            emit(isUpdated)
        } else {
            emit(false)
        }
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""

    private suspend fun manageAllNonHumanAnimalsToRescue(
        isNonHumanAnimalSaved: Boolean,
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
                        "manageAllNonHumanAnimalsToRescue: Can not update the non human animal state for the non human animal id ${nonHumanAnimalToRescueToManage.nonHumanAnimalId} in the rescue event id ${nonHumanAnimalToRescueToManage.rescueEventId} in the local data source"
                    )
                }

                if (nonHumanAnimal != null
                    && updatedAllNonHumanAnimalsToRescue.contains(nonHumanAnimalToRescueToManage)
                ) {
                    localNonHumanAnimalRepository.modifyNonHumanAnimal(
                        nonHumanAnimal
                            .copy(
                                nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED,
                                fosterHomeId = ""
                            )
                            .toEntity()
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "ModifyRescueEventInLocalRepository",
                                "manageAllNonHumanAnimalsToRescue: updated non human animal state ${NonHumanAnimalState.NEEDS_TO_BE_RESCUED} for the non human animal ${nonHumanAnimal.id} in the local data source"
                            )
                        } else {
                            log.e(
                                "ModifyRescueEventInLocalRepository",
                                "manageAllNonHumanAnimalsToRescue: failed to update the non human animal state ${NonHumanAnimalState.NEEDS_TO_BE_RESCUED} for the non human animal ${nonHumanAnimal.id} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                } else if (
                    !updatedAllNonHumanAnimalsToRescue.contains(nonHumanAnimalToRescueToManage)
                ) {
                    if (nonHumanAnimal != null
                        && nonHumanAnimal.nonHumanAnimalState != NonHumanAnimalState.SAVED
                        && isSuccess
                    ) {
                        localNonHumanAnimalRepository.modifyNonHumanAnimal(
                            nonHumanAnimal
                                .copy(
                                    nonHumanAnimalState = if (isNonHumanAnimalSaved) {
                                        NonHumanAnimalState.SAVED
                                    } else {
                                        NonHumanAnimalState.NEEDS_TO_BE_REHOMED
                                    },
                                    fosterHomeId = ""
                                )
                                .toEntity()
                        ) { rowsUpdated ->
                            if (rowsUpdated > 0) {
                                log.d(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: updated non human animal state ${NonHumanAnimalState.NEEDS_TO_BE_REHOMED} for the non human animal ${nonHumanAnimal.id} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyRescueEventInLocalRepository",
                                    "manageAllNonHumanAnimalsToRescue: failed to update the non human animal state ${NonHumanAnimalState.NEEDS_TO_BE_REHOMED} for the non human animal ${nonHumanAnimal.id} in the local data source"
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
