package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteAllMyFosterHomesFromLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val log: Log
) {

    suspend operator fun invoke(
        ownerId: String,
        coroutineScope: CoroutineScope,
        onDeleteAllMyFosterHomes: (rowsDeleted: Int) -> Unit
    ) {
        val isSuccess = updateAdoptionStates(
            ownerId,
            coroutineScope
        )
        if (isSuccess) {
            localFosterHomeRepository.deleteAllMyFosterHomes(ownerId, onDeleteAllMyFosterHomes)
        }
    }

    private suspend fun updateAdoptionStates(
        ownerId: String,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true

        val allFosterHomesWithAllNonHumanAnimalData: List<FosterHomeWithAllNonHumanAnimalData> =
            localFosterHomeRepository.getAllMyFosterHomes(ownerId).first()

        allFosterHomesWithAllNonHumanAnimalData.forEach { fosterHomeWithAllNonHumanAnimalData ->
            fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.forEach { residentNonHumanAnimalForFosterHome ->

                if (isSuccess) {
                    val residentNonHumanAnimal: NonHumanAnimal? =
                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                            residentNonHumanAnimalForFosterHome.nonHumanAnimalId,
                            residentNonHumanAnimalForFosterHome.caregiverId,
                            coroutineScope
                        ).firstOrNull()

                    if (residentNonHumanAnimal == null) {
                        log.d(
                            "DeleteAllMyFosterHomesFromLocalRepository",
                            "updateAdoptionStates: Can not update the adoption state for the non human animal ${residentNonHumanAnimalForFosterHome.nonHumanAnimalId} in the foster home ${residentNonHumanAnimalForFosterHome.fosterHomeId} because the non human animal has been unregistered in the local data source"
                        )
                    } else {
                        localNonHumanAnimalRepository.modifyNonHumanAnimal(
                            residentNonHumanAnimal.copy(
                                adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                                fosterHomeId = ""
                            ).toEntity()
                        ) { rowsUpdated ->
                            if (rowsUpdated > 0) {
                                log.d(
                                    "DeleteAllMyFosterHomesFromLocalRepository",
                                    "updateAdoptionStates: updated adoption state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
                                )
                            } else {
                                log.e(
                                    "DeleteAllMyFosterHomesFromLocalRepository",
                                    "updateAdoptionStates: failed to update the adoption state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
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
