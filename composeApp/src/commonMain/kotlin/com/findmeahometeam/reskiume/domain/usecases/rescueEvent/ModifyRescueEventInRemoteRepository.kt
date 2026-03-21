package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class ModifyRescueEventInRemoteRepository(
    private val authRepository: AuthRepository,
    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val log: Log
) {
    suspend operator fun invoke(
        updatedRescueEvent: RescueEvent,
        coroutineScope: CoroutineScope,
        onModifyRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        val myUid = authRepository.authState.first()!!.uid

        val previousRescueEvent = fireStoreRemoteRescueEventRepository.getRemoteRescueEvent(
            updatedRescueEvent.id
        ).firstOrNull()?.toDomain() ?: return

        val isSuccess = manageAllNonHumanAnimalsToRescue(
            myUid = myUid,
            previousRescueEvent = previousRescueEvent,
            updatedRescueEvent = updatedRescueEvent,
            coroutineScope = coroutineScope
        )
        if (isSuccess) {
            fireStoreRemoteRescueEventRepository.modifyRemoteRescueEvent(
                updatedRescueEvent.toData(),
                onModifyRemoteRescueEvent
            )
        }
    }

    private suspend fun manageAllNonHumanAnimalsToRescue(
        myUid: String,
        previousRescueEvent: RescueEvent,
        updatedRescueEvent: RescueEvent,
        coroutineScope: CoroutineScope
    ): Boolean {
        val previousAllNonHumanAnimalsToRescue =
            previousRescueEvent.allNonHumanAnimalsToRescue.toSet()

        val updatedAllNonHumanAnimalsToRescue =
            updatedRescueEvent.allNonHumanAnimalsToRescue.toSet()

        val nonHumanAnimalsToRescueToManage: Set<NonHumanAnimalToRescue> =
            (previousAllNonHumanAnimalsToRescue - updatedAllNonHumanAnimalsToRescue) +
                    (updatedAllNonHumanAnimalsToRescue - previousAllNonHumanAnimalsToRescue)

        nonHumanAnimalsToRescueToManage.forEach { nonHumanAnimalToRescue ->

            val remoteNonHumanAnimal: RemoteNonHumanAnimal =
                realtimeDatabaseRemoteNonHumanAnimalRepository.getRemoteNonHumanAnimal(
                    nonHumanAnimalToRescue.nonHumanAnimalId,
                    nonHumanAnimalToRescue.caregiverId,
                ).firstOrNull() ?: return false.also {
                    deleteNonHumanAnimal(
                        nonHumanAnimalToRescue,
                        myUid,
                        coroutineScope
                    )
                }

            val containsNonHumanAnimalToRescue =
                updatedAllNonHumanAnimalsToRescue.contains(nonHumanAnimalToRescue)
            updateAdoptionState(
                remoteNonHumanAnimal,
                containsNonHumanAnimalToRescue
            )

        }
        return true
    }

    private fun deleteNonHumanAnimal(
        nonHumanAnimalToRescue: NonHumanAnimalToRescue,
        myUid: String,
        coroutineScope: CoroutineScope
    ) {
        log.d(
            "ModifyRescueEventInRemoteRepository",
            "deleteNonHumanAnimal: Can not update the adoption state for the non human animal in the rescue event ${nonHumanAnimalToRescue.rescueEventId} because the non human animal has been unregistered in the remote data source"
        )

        deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
            id = nonHumanAnimalToRescue.nonHumanAnimalId,
            caregiverId = nonHumanAnimalToRescue.caregiverId,
            coroutineScope = coroutineScope,
            onlyDeleteOnLocal = myUid != nonHumanAnimalToRescue.caregiverId,
            onError = {
                log.e(
                    "ModifyRescueEventInRemoteRepository",
                    "deleteNonHumanAnimal: Can not delete the non human animal ${nonHumanAnimalToRescue.nonHumanAnimalId} in the local data source"
                )
            },
            onComplete = {
                log.d(
                    "ModifyRescueEventInRemoteRepository",
                    "deleteNonHumanAnimal: Deleted the non human animal ${nonHumanAnimalToRescue.nonHumanAnimalId} in the local data source"
                )
            }
        )
    }

    private suspend fun updateAdoptionState(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        containsNonHumanAnimalToRescue: Boolean
    ) {
        val adoptionState = when {
            containsNonHumanAnimalToRescue -> {
                AdoptionState.NEEDS_TO_BE_RESCUED
            }
            remoteNonHumanAnimal.adoptionState != AdoptionState.ADOPTED -> {
                AdoptionState.LOOKING_FOR_ADOPTION
            }
            else -> return // is adopted
        }
        realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
            remoteNonHumanAnimal.copy(
                adoptionState = adoptionState,
                fosterHomeId = ""
            )
        ) { databaseResult ->

            if (databaseResult is DatabaseResult.Success) {
                log.d(
                    "ModifyRescueEventInRemoteRepository",
                    "updateAdoptionState: updated adoption state $adoptionState for the non human animal ${remoteNonHumanAnimal.id} in the remote data source"
                )
            } else {
                log.e(
                    "ModifyRescueEventInRemoteRepository",
                    "updateAdoptionState: failed to update the adoption state $adoptionState for the non human animal ${remoteNonHumanAnimal.id} in the remote data source"
                )
            }
        }
    }
}
