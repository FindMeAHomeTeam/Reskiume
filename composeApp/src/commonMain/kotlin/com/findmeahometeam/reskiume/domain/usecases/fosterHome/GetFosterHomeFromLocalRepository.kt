package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetFosterHomeFromLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
) {
    suspend operator fun invoke(
        id: String,
        coroutineScope: CoroutineScope
    ): FosterHome? =
        localFosterHomeRepository.getFosterHome(id)?.let { fosterHomeWithAllNonHumanAnimalData ->
            fosterHomeWithAllNonHumanAnimalData.fosterHomeEntity.toDomain(
                allAcceptedNonHumanAnimalTypes = fosterHomeWithAllNonHumanAnimalData.allAcceptedNonHumanAnimalTypes.map { it.toDomain() },
                allAcceptedNonHumanAnimalGenders = fosterHomeWithAllNonHumanAnimalData.allAcceptedNonHumanAnimalGenders.map { it.toDomain() },
                allResidentNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.map {
                    it.toDomain(
                        onFetchNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->
                            checkNonHumanAnimalUtil
                                .getNonHumanAnimalFlow(
                                    coroutineScope = coroutineScope,
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
                }
            )
        }
}
