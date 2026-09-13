package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class InsertFosterHomeInLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val manageImagePath: ManageImagePath,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    operator fun invoke(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope
    ): Flow<Boolean> = flow {

        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(fosterHome.imageUrl)
        val createdFosterHome = fosterHome.copy(
            savedBy = getMyUid(),
            imageUrl = imageFileName
        )
        val isSuccess = localFosterHomeRepository.upsertAllFosterHomeData(
            fosterHomeEntity = createdFosterHome.toEntity(),
            allAcceptedNonHumanAnimals = createdFosterHome.allAcceptedNonHumanAnimals.map { it.toEntity() },
            allResidentNonHumanAnimalIds = createdFosterHome.allResidentNonHumanAnimals.map { it.toEntity() }
        ).first()

        if (isSuccess) {
            val isUpdated = updateAllResidentNonHumanAnimals(createdFosterHome, coroutineScope)
            emit(isUpdated)
        } else {
            emit(false)
        }
    }

    private suspend fun updateAllResidentNonHumanAnimals(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope
    ): Boolean {
        var isSuccess = true
        fosterHome.allResidentNonHumanAnimals.forEach { residentNonHumanAnimalForFosterHome ->
            if (isSuccess) {

                val residentNonHumanAnimal: NonHumanAnimal? =
                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        residentNonHumanAnimalForFosterHome.nonHumanAnimalId,
                        residentNonHumanAnimalForFosterHome.caregiverId,
                        coroutineScope
                    ).firstOrNull()

                if (residentNonHumanAnimal == null) {
                    log.d(
                        "InsertFosterHomeInLocalRepository",
                        "updateAllResidentNonHumanAnimals: Can not update the non human animal state for the resident id ${residentNonHumanAnimalForFosterHome.nonHumanAnimalId} in the foster home ${residentNonHumanAnimalForFosterHome.fosterHomeId} in the local data source"
                    )
                } else {

                    val imageFileName = manageImagePath.getFileNameFromLocalImagePath(
                        residentNonHumanAnimal.imageUrl
                    )
                    localNonHumanAnimalRepository.modifyNonHumanAnimal(
                        residentNonHumanAnimal.copy(
                            nonHumanAnimalState = NonHumanAnimalState.REHOMED,
                            fosterHomeId = fosterHome.id,
                            imageUrl = imageFileName
                        ).toEntity()
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "InsertFosterHomeInLocalRepository",
                                "updateAllResidentNonHumanAnimals: updated non human animal state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
                            )
                        } else {
                            log.e(
                                "InsertFosterHomeInLocalRepository",
                                "updateAllResidentNonHumanAnimals: failed to update the non human animal state for the non human animal ${residentNonHumanAnimal.id} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                }
            }
        }
        return isSuccess
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
