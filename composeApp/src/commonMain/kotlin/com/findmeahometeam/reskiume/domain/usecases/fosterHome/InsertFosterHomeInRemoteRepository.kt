package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository

class InsertFosterHomeInRemoteRepository(
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val log: Log
) {

    suspend operator fun invoke(
        fosterHome: FosterHome,
        onInsertRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(fosterHome)

        if (isSuccess) {
            fireStoreRemoteFosterHomeRepository.insertRemoteFosterHome(
                fosterHome.toData(),
                onInsertRemoteFosterHome
            )
        }
    }

    private suspend fun updateAdoptionStates(fosterHome: FosterHome): Boolean {
        var isSuccess = true

        fosterHome.allResidentNonHumanAnimals.forEach { residentNonHumanAnimalToManage ->
            if (isSuccess) {
                realtimeDatabaseRemoteNonHumanAnimalRepository.insertRemoteNonHumanAnimal(
                    residentNonHumanAnimalToManage.residentNonHumanAnimal!!
                        .copy(
                            adoptionState = AdoptionState.REHOMED,
                            fosterHomeId = fosterHome.id
                        )
                        .toData()
                ) { databaseResult ->
                    if (databaseResult is DatabaseResult.Success) {
                        log.d(
                            "InsertFosterHomeInRemoteRepository",
                            "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimalToManage.residentNonHumanAnimal.id} in the remote data source"
                        )
                    } else {
                        log.e(
                            "InsertFosterHomeInRemoteRepository",
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
