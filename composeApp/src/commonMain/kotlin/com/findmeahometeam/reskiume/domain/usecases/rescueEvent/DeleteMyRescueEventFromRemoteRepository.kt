package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteMyRescueEventFromRemoteRepository(
    private val authRepository: AuthRepository,
    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val log: Log
) {

    suspend operator fun invoke(
        id: String,
        coroutineScope: CoroutineScope,
        onDeleteRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        val myUid = authRepository.authState.first()!!.uid
        val isSuccess = manageNonHumanAnimals(
            id,
            myUid,
            coroutineScope
        )

        if (isSuccess) {
            fireStoreRemoteRescueEventRepository.deleteRemoteRescueEvent(
                id,
                onDeleteRemoteRescueEvent
            )
        }
    }

    private suspend fun manageNonHumanAnimals(
        id: String,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val remoteRescueEvents: RemoteRescueEvent =
            fireStoreRemoteRescueEventRepository.getRemoteRescueEvent(id).first()!!

        remoteRescueEvents.allNonHumanAnimalsToRescue!!.forEach { remoteNonHumanAnimalToRescue ->

            if (isSuccess) {
                val remoteNonHumanAnimal: RemoteNonHumanAnimal? =
                    realtimeDatabaseRemoteNonHumanAnimalRepository.getRemoteNonHumanAnimal(
                        remoteNonHumanAnimalToRescue.nonHumanAnimalId!!,
                        remoteNonHumanAnimalToRescue.caregiverId!!,
                    ).firstOrNull()

                if (remoteNonHumanAnimal == null) {
                    deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                        id = remoteNonHumanAnimalToRescue.nonHumanAnimalId,
                        caregiverId = remoteNonHumanAnimalToRescue.caregiverId,
                        coroutineScope = coroutineScope,
                        onlyDeleteOnLocal = myUid != remoteNonHumanAnimalToRescue.caregiverId,
                        onError = {
                            log.e(
                                "DeleteMyRescueEventFromRemoteRepository",
                                "manageNonHumanAnimals: Can not delete the non human animal ${remoteNonHumanAnimalToRescue.nonHumanAnimalId} in the local data source"
                            )
                        },
                        onComplete = {
                            log.d(
                                "DeleteMyRescueEventFromRemoteRepository",
                                "manageNonHumanAnimals: Deleted the non human animal ${remoteNonHumanAnimalToRescue.nonHumanAnimalId} in the local data source"
                            )
                        }
                    )
                } else {
                    realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                        remoteNonHumanAnimal.copy(
                            nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED,
                            fosterHomeId = ""
                        )
                    ) { databaseResult ->
                        if (databaseResult is DatabaseResult.Success) {
                            log.d(
                                "DeleteMyRescueEventFromRemoteRepository",
                                "manageNonHumanAnimals: updated non human animal state ${NonHumanAnimalState.NEEDS_TO_BE_REHOMED} for the non human animal ${remoteNonHumanAnimal.id} in the remote data source"
                            )
                        } else {
                            log.e(
                                "DeleteMyRescueEventFromRemoteRepository",
                                "manageNonHumanAnimals: failed to update the non human animal state ${NonHumanAnimalState.NEEDS_TO_BE_REHOMED} for the non human animal ${remoteNonHumanAnimal.id} in the remote data source"
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
