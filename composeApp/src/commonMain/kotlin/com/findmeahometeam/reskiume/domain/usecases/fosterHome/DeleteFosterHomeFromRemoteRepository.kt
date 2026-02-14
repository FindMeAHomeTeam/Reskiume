package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.firstOrNull

class DeleteFosterHomeFromRemoteRepository(
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val log: Log
) {

    suspend operator fun invoke(
        id: String,
        ownerId: String,
        onDeleteRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(id, ownerId)

        if (isSuccess) {
            fireStoreRemoteFosterHomeRepository.deleteRemoteFosterHome(
                id,
                ownerId,
                onDeleteRemoteFosterHome
            )
        }
    }

    private suspend fun updateAdoptionStates(
        fosterHomeId: String,
        ownerId: String
    ): Boolean {
        var isSuccess = true

        val remoteFosterHome = fireStoreRemoteFosterHomeRepository.getRemoteFosterHome(fosterHomeId, ownerId).firstOrNull() ?: return false

        remoteFosterHome.allResidentNonHumanAnimalIds!!.forEach { residentNonHumanAnimalForFosterHome ->
            if (isSuccess) {
                val residentNonHumanAnimalForFosterHome = residentNonHumanAnimalForFosterHome.toDomain(
                    onFetchNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->

                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                            nonHumanAnimalId = nonHumanAnimalId,
                            caregiverId = caregiverId
                        ).firstOrNull()
                    }
                )
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    residentNonHumanAnimalForFosterHome.residentNonHumanAnimal!!.copy(
                        adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                        fosterHomeId = ""
                    ).toData()
                ) { databaseResult ->
                    if (databaseResult is DatabaseResult.Success) {
                        log.d(
                            "DeleteFosterHomeFromRemoteRepository",
                            "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} in the remote data source"
                        )
                    } else {
                        log.e(
                            "DeleteFosterHomeFromRemoteRepository",
                            "updateAdoptionStates: failed to update the adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} in the remote data source"
                        )
                        isSuccess = false
                    }
                }
            }
        }
        return isSuccess
    }
}
