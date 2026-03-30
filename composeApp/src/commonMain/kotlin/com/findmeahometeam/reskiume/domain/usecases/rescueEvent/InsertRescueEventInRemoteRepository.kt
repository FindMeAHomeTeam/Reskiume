package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class InsertRescueEventInRemoteRepository(
    private val authRepository: AuthRepository,
    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val log: Log
) {
    suspend operator fun invoke(
        rescueEvent: RescueEvent,
        coroutineScope: CoroutineScope,
        onInsertRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        val myUid = authRepository.authState.first()!!.uid
        val updatedRescueEvent = getUpdatedRescueEventWithUpdatedNonHumanAnimalsToRescue(
            rescueEvent,
            myUid,
            coroutineScope
        )
        fireStoreRemoteRescueEventRepository.insertRemoteRescueEvent(
            updatedRescueEvent.toData(),
            onInsertRemoteRescueEvent
        )
    }

    private suspend fun getUpdatedRescueEventWithUpdatedNonHumanAnimalsToRescue(
        rescueEvent: RescueEvent,
        myUid: String,
        coroutineScope: CoroutineScope
    ): RescueEvent {
        var isSuccess = true
        val allNonHumanAnimalsToRescue =
            rescueEvent.allNonHumanAnimalsToRescue.mapNotNull { nonHumanAnimalToRescue ->

                val remoteResidentNonHumanAnimal: RemoteNonHumanAnimal? =
                    realtimeDatabaseRemoteNonHumanAnimalRepository.getRemoteNonHumanAnimal(
                        nonHumanAnimalToRescue.nonHumanAnimalId,
                        nonHumanAnimalToRescue.caregiverId,
                    ).firstOrNull()

                if (remoteResidentNonHumanAnimal == null) {

                    deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                        id = nonHumanAnimalToRescue.nonHumanAnimalId,
                        caregiverId = nonHumanAnimalToRescue.caregiverId,
                        coroutineScope = coroutineScope,
                        onlyDeleteOnLocal = myUid != nonHumanAnimalToRescue.caregiverId,
                        onError = {
                            log.e(
                                "InsertRescueEventInRemoteRepository",
                                "getUpdatedRescueEventWithUpdatedNonHumanAnimalsToRescue: Can not delete the non human animal ${nonHumanAnimalToRescue.nonHumanAnimalId} in the local data source"
                            )
                        },
                        onComplete = {
                            log.d(
                                "InsertRescueEventInRemoteRepository",
                                "getUpdatedRescueEventWithUpdatedNonHumanAnimalsToRescue: Deleted the non human animal ${nonHumanAnimalToRescue.nonHumanAnimalId} in the local data source"
                            )
                        }
                    )
                    null
                } else {
                    realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                        remoteResidentNonHumanAnimal
                            .copy(
                                nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED,
                                fosterHomeId = ""
                            )
                    ) { databaseResult ->
                        if (databaseResult is DatabaseResult.Success) {
                            log.d(
                                "InsertRescueEventInRemoteRepository",
                                "getUpdatedRescueEventWithUpdatedNonHumanAnimalsToRescue: updated non human animal state ${NonHumanAnimalState.NEEDS_TO_BE_RESCUED} for the non human animal ${nonHumanAnimalToRescue.nonHumanAnimalId} in the remote data source"
                            )
                        } else {
                            log.e(
                                "InsertRescueEventInRemoteRepository",
                                "getUpdatedRescueEventWithUpdatedNonHumanAnimalsToRescue: failed to update the non human animal state ${NonHumanAnimalState.NEEDS_TO_BE_RESCUED} for the non human animal ${nonHumanAnimalToRescue.nonHumanAnimalId} in the remote data source"
                            )
                            isSuccess = false
                        }
                    }
                    if (isSuccess) {
                        nonHumanAnimalToRescue
                    } else {
                        null
                    }
                }
            }
        return rescueEvent.copy(allNonHumanAnimalsToRescue = allNonHumanAnimalsToRescue)
    }
}
