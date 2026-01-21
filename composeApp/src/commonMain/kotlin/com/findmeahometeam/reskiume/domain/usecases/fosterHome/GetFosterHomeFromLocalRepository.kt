package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

class GetFosterHomeFromLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
) {
    operator fun invoke(
        id: String,
        coroutineScope: CoroutineScope
    ): Flow<FosterHome?> = flow {
        val result: FosterHome? = localFosterHomeRepository.getFosterHome(id)?.let { fosterHomeWithAllNonHumanAnimalData ->
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
                                ).mapNotNull { uiState ->
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
        emit(result)
    }
}
