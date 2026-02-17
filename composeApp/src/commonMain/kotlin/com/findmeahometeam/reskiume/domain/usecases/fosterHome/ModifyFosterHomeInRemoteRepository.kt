package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class ModifyFosterHomeInRemoteRepository(
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val log: Log
) {
    suspend operator fun invoke(
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome,
        coroutineScope: CoroutineScope,
        onModifyRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val myUid = observeAuthStateInAuthDataSource().first()!!.uid
        val isSuccess =
            manageResidents(
                updatedFosterHome,
                previousFosterHome,
                myUid,
                coroutineScope
            )

        if (isSuccess) {
            fireStoreRemoteFosterHomeRepository.modifyRemoteFosterHome(
                updatedFosterHome.toData(),
                onModifyRemoteFosterHome
            )
        } else {
            onModifyRemoteFosterHome(DatabaseResult.Error())
        }
    }

    private suspend fun manageResidents(
        updatedFosterHome: FosterHome,
        previousFosterHome: FosterHome,
        myUid: String,
        coroutineScope: CoroutineScope
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

                val remoteResidentNonHumanAnimal: RemoteNonHumanAnimal? =
                    realtimeDatabaseRemoteNonHumanAnimalRepository.getRemoteNonHumanAnimal(
                        residentNonHumanAnimalToManage.nonHumanAnimalId,
                        residentNonHumanAnimalToManage.caregiverId,
                    ).firstOrNull()

                isSuccess = updateAdoptionState(
                    remoteResidentNonHumanAnimal,
                    updatedFosterHome.id,
                    containsResidentNonHumanAnimal
                )
                if (!isSuccess) {
                    deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                        id = residentNonHumanAnimalToManage.nonHumanAnimalId,
                        caregiverId = residentNonHumanAnimalToManage.caregiverId,
                        coroutineScope = coroutineScope,
                        onlyDeleteOnLocal = myUid != residentNonHumanAnimalToManage.caregiverId,
                        onError = {
                            log.e(
                                "ModifyFosterHomeInRemoteRepository",
                                "getUpdatedFosterHomeWithUpdatedResidents: Can not delete the non human animal ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the local data source"
                            )
                        },
                        onComplete = {
                            log.d(
                                "ModifyFosterHomeInRemoteRepository",
                                "getUpdatedFosterHomeWithUpdatedResidents: Deleted the non human animal ${residentNonHumanAnimalToManage.nonHumanAnimalId} in the local data source"
                            )
                        }
                    )
                }
            }
        }
        return isSuccess
    }

    private suspend fun updateAdoptionState(
        remoteResidentNonHumanAnimal: RemoteNonHumanAnimal?,
        fosterHomeId: String,
        containsResidentNonHumanAnimal: Boolean
    ): Boolean {
        var isSuccess = true
        if (remoteResidentNonHumanAnimal == null) {
            log.d(
                "ModifyFosterHomeInRemoteRepository",
                "updateAdoptionStates: Can not update the adoption state for the non human animal in the foster home $fosterHomeId because the non human animal has been unregistered in the remote data source"
            )
            isSuccess = false
        } else {
            realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                remoteResidentNonHumanAnimal
                    .copy(
                        adoptionState = if (containsResidentNonHumanAnimal) {
                            AdoptionState.REHOMED
                        } else {
                            AdoptionState.LOOKING_FOR_ADOPTION
                        },
                        fosterHomeId = if (containsResidentNonHumanAnimal) {
                            fosterHomeId
                        } else {
                            ""
                        },
                    )
            ) { databaseResult ->
                if (databaseResult is DatabaseResult.Success) {
                    log.d(
                        "ModifyFosterHomeInRemoteRepository",
                        "updateAdoptionStates: updated adoption state for the non human animal ${remoteResidentNonHumanAnimal.id} in the remote data source"
                    )
                } else {
                    log.e(
                        "ModifyFosterHomeInRemoteRepository",
                        "updateAdoptionStates: failed to update the adoption state for the non human animal ${remoteResidentNonHumanAnimal.id} in the remote data source"
                    )
                    isSuccess = false
                }
            }
        }
        return isSuccess
    }
}
