package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteAllMyFosterHomesFromRemoteRepository(
    private val authRepository: AuthRepository,
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository,
    private val log: Log
) {

    suspend operator fun invoke(
        ownerId: String,
        coroutineScope: CoroutineScope,
        onDeleteAllMyRemoteFosterHomes: (result: DatabaseResult) -> Unit
    ) {
        val myUid = authRepository.authState.first()!!.uid
        val isSuccess = manageResidents(
            ownerId,
            myUid,
            coroutineScope
        )
        if (isSuccess) {
            fireStoreRemoteFosterHomeRepository.deleteAllMyRemoteFosterHomes(
                ownerId,
                onDeleteAllMyRemoteFosterHomes
            )
        }
    }

    private suspend fun manageResidents(
        ownerId: String,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val remoteFosterHomes: List<RemoteFosterHome?> =
            fireStoreRemoteFosterHomeRepository.getAllMyRemoteFosterHomes(ownerId).first()

        remoteFosterHomes.forEach { remoteFosterHome ->
            remoteFosterHome!!.allResidentNonHumanAnimalIds!!.forEach { residentNonHumanAnimalForFosterHome ->

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
                                    "DeleteAllMyFosterHomesFromRemoteRepository",
                                    "manageResidents: Can not delete the non human animal ${residentNonHumanAnimalForFosterHome.nonHumanAnimalId} in the local data source"
                                )
                            },
                            onComplete = {
                                log.d(
                                    "DeleteAllMyFosterHomesFromRemoteRepository",
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
                                    "DeleteAllMyFosterHomesFromRemoteRepository",
                                    "manageResidents: updated adoption state for the non human animal ${remoteResidentNonHumanAnimal.id} in the remote data source"
                                )
                            } else {
                                log.e(
                                    "DeleteAllMyFosterHomesFromRemoteRepository",
                                    "manageResidents: failed to update the adoption state for the non human animal ${remoteResidentNonHumanAnimal.id} in the remote data source"
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
