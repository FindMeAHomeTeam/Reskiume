package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteMyFosterHomeFromRemoteRepository(
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val log: Log
) {

    suspend operator fun invoke(
        id: String,
        ownerId: String,
        coroutineScope: CoroutineScope,
        onDeleteRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val myUid = observeAuthStateInAuthDataSource().first()!!.uid
        val isSuccess = manageResidents(
            id,
            myUid,
            coroutineScope
        )

        if (isSuccess) {
            fireStoreRemoteFosterHomeRepository.deleteRemoteFosterHome(
                id,
                ownerId,
                onDeleteRemoteFosterHome
            )
        }
    }

    private suspend fun manageResidents(
        fosterHomeId: String,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val remoteFosterHome = fireStoreRemoteFosterHomeRepository.getRemoteFosterHome(fosterHomeId).first()!!

        remoteFosterHome.allResidentNonHumanAnimalIds!!.forEach { residentNonHumanAnimalForFosterHome ->
            if (isSuccess) {
                val remoteResidentNonHumanAnimal: RemoteNonHumanAnimal? =
                    realtimeDatabaseRemoteNonHumanAnimalRepository.getRemoteNonHumanAnimal(
                        residentNonHumanAnimalForFosterHome.nonHumanAnimalId!!,
                        residentNonHumanAnimalForFosterHome.caregiverId!!,
                    ).firstOrNull()

                if (remoteResidentNonHumanAnimal == null) {
                    deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                        id = residentNonHumanAnimalForFosterHome.nonHumanAnimalId,
                        caregiverId = residentNonHumanAnimalForFosterHome.caregiverId,
                        coroutineScope = coroutineScope,
                        onlyDeleteOnLocal = myUid != residentNonHumanAnimalForFosterHome.caregiverId,
                        onError = {
                            log.e(
                                "DeleteMyFosterHomeFromRemoteRepository",
                                "manageResidents: Can not delete the non human animal ${residentNonHumanAnimalForFosterHome.nonHumanAnimalId} in the local data source"
                            )
                        },
                        onComplete = {
                            log.d(
                                "DeleteMyFosterHomeFromRemoteRepository",
                                "manageResidents: Deleted the non human animal ${residentNonHumanAnimalForFosterHome.nonHumanAnimalId} in the local data source"
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
                                "DeleteFosterHomeFromRemoteRepository",
                                "manageResidents: updated adoption state for the non human animal ${remoteResidentNonHumanAnimal.id} in the remote data source"
                            )
                        } else {
                            log.e(
                                "DeleteFosterHomeFromRemoteRepository",
                                "manageResidents: failed to update the adoption state for the non human animal ${remoteResidentNonHumanAnimal.id} in the remote data source"
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
