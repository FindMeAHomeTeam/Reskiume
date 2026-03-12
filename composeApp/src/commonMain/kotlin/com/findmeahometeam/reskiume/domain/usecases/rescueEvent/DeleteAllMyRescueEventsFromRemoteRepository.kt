package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteAllMyRescueEventsFromRemoteRepository(
    private val authRepository: AuthRepository,
    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val log: Log
) {

    suspend operator fun invoke(
        creatorId: String,
        coroutineScope: CoroutineScope,
        onDeleteAllMyRemoteRescueEvents: (result: DatabaseResult) -> Unit
    ) {
        val myUid = authRepository.authState.first()!!.uid
        val isSuccess = manageResidents(
            creatorId,
            myUid,
            coroutineScope
        )
        if (isSuccess) {
            fireStoreRemoteRescueEventRepository.deleteAllMyRemoteRescueEvents(
                creatorId,
                onDeleteAllMyRemoteRescueEvents
            )
        }
    }

    private suspend fun manageResidents(
        creatorId: String,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val remoteRescueEvents: List<RemoteRescueEvent?> =
            fireStoreRemoteRescueEventRepository.getAllMyRemoteRescueEvents(creatorId).first()

        remoteRescueEvents.forEach { remoteRescueEvent ->
            remoteRescueEvent!!.allNonHumanAnimalsToRescue!!.forEach { remoteNonHumanAnimalToRescue ->

                if (isSuccess) {
                    val remoteResidentNonHumanAnimal: RemoteNonHumanAnimal? =
                        realtimeDatabaseRemoteNonHumanAnimalRepository.getRemoteNonHumanAnimal(
                            remoteNonHumanAnimalToRescue.nonHumanAnimalId!!,
                            remoteNonHumanAnimalToRescue.caregiverId!!,
                        ).firstOrNull()

                    if (remoteResidentNonHumanAnimal == null) {
                        deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                            id = remoteNonHumanAnimalToRescue.nonHumanAnimalId,
                            caregiverId = remoteNonHumanAnimalToRescue.caregiverId,
                            coroutineScope = coroutineScope,
                            onlyDeleteOnLocal = myUid != remoteNonHumanAnimalToRescue.caregiverId,
                            onError = {
                                log.e(
                                    "DeleteAllMyRescueEventsFromRemoteRepository",
                                    "manageResidents: Can not delete the non human animal ${remoteNonHumanAnimalToRescue.nonHumanAnimalId} in the local data source"
                                )
                            },
                            onComplete = {
                                log.d(
                                    "DeleteAllMyRescueEventsFromRemoteRepository",
                                    "manageResidents: Deleted the non human animal ${remoteNonHumanAnimalToRescue.nonHumanAnimalId} in the local data source"
                                )
                            }
                        )
                    } else {
                        realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                            remoteResidentNonHumanAnimal.copy(
                                adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                                fosterHomeId = ""
                            )
                        ) { databaseResult ->
                            if (databaseResult is DatabaseResult.Success) {
                                log.d(
                                    "DeleteAllMyRescueEventsFromRemoteRepository",
                                    "manageResidents: updated adoption state ${AdoptionState.LOOKING_FOR_ADOPTION} for the non human animal ${remoteResidentNonHumanAnimal.id} in the remote data source"
                                )
                            } else {
                                log.e(
                                    "DeleteAllMyRescueEventsFromRemoteRepository",
                                    "manageResidents: failed to update the adoption state ${AdoptionState.LOOKING_FOR_ADOPTION} for the non human animal ${remoteResidentNonHumanAnimal.id} in the remote data source"
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
