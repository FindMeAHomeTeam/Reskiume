package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class InsertFosterHomeInRemoteRepository(
    private val authRepository: AuthRepository,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val log: Log
) {

    suspend operator fun invoke(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope,
        onInsertRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val myUid = authRepository.authState.first()!!.uid
        val updatedFosterHome = getUpdatedFosterHomeWithUpdatedResidents(
            fosterHome,
            myUid,
            coroutineScope
        )
        fireStoreRemoteFosterHomeRepository.insertRemoteFosterHome(
            updatedFosterHome.toData(),
            onInsertRemoteFosterHome
        )
    }

    private suspend fun getUpdatedFosterHomeWithUpdatedResidents(
        fosterHome: FosterHome,
        myUid: String,
        coroutineScope: CoroutineScope
    ): FosterHome {
        var isSuccess = true
        val allResidentNonHumanAnimals =
            fosterHome.allResidentNonHumanAnimals.mapNotNull { residentNonHumanAnimalToManage ->

                val remoteResidentNonHumanAnimal: RemoteNonHumanAnimal? =
                    realtimeDatabaseRemoteNonHumanAnimalRepository.getRemoteNonHumanAnimal(
                        residentNonHumanAnimalToManage.nonHumanAnimalId,
                        residentNonHumanAnimalToManage.caregiverId,
                    ).firstOrNull()

                if (remoteResidentNonHumanAnimal == null) {
                    deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                        id = residentNonHumanAnimalToManage.nonHumanAnimalId,
                        caregiverId = residentNonHumanAnimalToManage.caregiverId,
                        coroutineScope = coroutineScope,
                        onlyDeleteOnLocal = myUid != residentNonHumanAnimalToManage.caregiverId,
                        onError = {
                            log.e(
                                "InsertFosterHomeInRemoteRepository",
                                "getUpdatedFosterHomeWithUpdatedResidents: Can not delete the non human animal ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the local data source"
                            )
                        },
                        onComplete = {
                            log.d(
                                "InsertFosterHomeInRemoteRepository",
                                "getUpdatedFosterHomeWithUpdatedResidents: Deleted the non human animal ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the local data source"
                            )
                        }
                    )
                    null
                } else {
                    realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                        remoteResidentNonHumanAnimal
                            .copy(
                                adoptionState = AdoptionState.REHOMED,
                                fosterHomeId = fosterHome.id
                            )
                    ) { databaseResult ->
                        if (databaseResult is DatabaseResult.Success) {
                            log.d(
                                "InsertFosterHomeInRemoteRepository",
                                "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the remote data source"
                            )
                        } else {
                            log.e(
                                "InsertFosterHomeInRemoteRepository",
                                "updateAdoptionStates: failed to update the adoption state for the non human animal ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the remote data source"
                            )
                            isSuccess = false
                        }
                    }
                    if (isSuccess) {
                        residentNonHumanAnimalToManage
                    } else {
                        null
                    }
                }
            }
        return fosterHome.copy(allResidentNonHumanAnimals = allResidentNonHumanAnimals)
    }
}
