package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DeleteAllMyFosterHomesFromRemoteRepository(
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val log: Log
) {

    suspend operator fun invoke(
        ownerId: String,
        onDeleteAllMyRemoteFosterHomes: (result: DatabaseResult) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(ownerId)

        if (isSuccess) {
            fireStoreRemoteFosterHomeRepository.deleteAllMyRemoteFosterHomes(
                ownerId,
                onDeleteAllMyRemoteFosterHomes
            )
        }
    }

    private suspend fun updateAdoptionStates(ownerId: String): Boolean {
        var isSuccess = true

        val remoteFosterHomes: List<RemoteFosterHome?> =
            fireStoreRemoteFosterHomeRepository.getAllMyRemoteFosterHomes(ownerId).first()

        remoteFosterHomes.forEach { fosterHome ->

            fosterHome!!.allResidentNonHumanAnimalIds!!.forEach { residentNonHumanAnimalForFosterHome ->
                if (isSuccess) {
                    val residentNonHumanAnimalForFosterHome =
                        residentNonHumanAnimalForFosterHome.toDomain(
                            onFetchNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->

                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                    nonHumanAnimalId = nonHumanAnimalId,
                                    caregiverId = caregiverId
                                ).map { uiState ->

                                    if (uiState is UiState.Success) {
                                        uiState.data
                                    } else {
                                        null
                                    }
                                }.firstOrNull()
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
                                "DeleteAllMyFosterHomesFromRemoteRepository",
                                "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} to ${AdoptionState.LOOKING_FOR_ADOPTION} in the remote data source"
                            )
                        } else {
                            log.e(
                                "DeleteAllMyFosterHomesFromRemoteRepository",
                                "updateAdoptionStates: failed to update the adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.residentNonHumanAnimal.id} in the remote data source"
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
