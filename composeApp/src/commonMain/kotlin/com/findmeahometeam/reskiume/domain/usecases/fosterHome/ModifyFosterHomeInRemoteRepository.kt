package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository

class ModifyFosterHomeInRemoteRepository(
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome,
        onModifyRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(updatedFosterHome, previousFosterHome)

        if (isSuccess) {
            fireStoreRemoteFosterHomeRepository.modifyRemoteFosterHome(
                updatedFosterHome.toData(),
                onModifyRemoteFosterHome
            )
        } else {
            onModifyRemoteFosterHome(DatabaseResult.Error())
        }
    }

    private suspend fun updateAdoptionStates(
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome
    ): Boolean {
        var isSuccess = true

        val updatedAllResidentNonHumanAnimals =
            updatedFosterHome.allResidentNonHumanAnimals.toSet()

        val previousAllResidentNonHumanAnimals =
            previousFosterHome.allResidentNonHumanAnimals.toSet()

        val allResidentNonHumanAnimalsToManage =
            (previousAllResidentNonHumanAnimals - updatedAllResidentNonHumanAnimals) +
                    (updatedAllResidentNonHumanAnimals - previousAllResidentNonHumanAnimals)

        allResidentNonHumanAnimalsToManage.forEach { residentNonHumanAnimalToManage ->
            if (isSuccess) {
                val containsResidentNonHumanAnimal =
                    updatedAllResidentNonHumanAnimals.contains(residentNonHumanAnimalToManage)

                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    residentNonHumanAnimalToManage.residentNonHumanAnimal!!
                        .copy(
                            adoptionState = if (containsResidentNonHumanAnimal) {
                                AdoptionState.REHOMED
                            } else {
                                AdoptionState.LOOKING_FOR_ADOPTION
                            },
                            fosterHomeId = if (containsResidentNonHumanAnimal) {
                                updatedFosterHome.id
                            } else {
                                ""
                            },
                        )
                        .toData()
                ) { databaseResult ->
                    if (databaseResult is DatabaseResult.Success) {
                        log.d(
                            "ModifyFosterHomeInRemoteRepository",
                            "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimalToManage.residentNonHumanAnimal.id} in the remote data source"
                        )
                    } else {
                        log.e(
                            "ModifyFosterHomeInRemoteRepository",
                            "updateAdoptionStates: failed to update the adoption state for the non human animal ${residentNonHumanAnimalToManage.residentNonHumanAnimal.id} in the remote data source"
                        )
                        isSuccess = false
                    }
                }
            }
        }
        return isSuccess
    }
}
