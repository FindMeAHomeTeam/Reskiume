package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetAllFosterHomesByLocationFromLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
) {
    operator fun invoke(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<FosterHome>> =
        localFosterHomeRepository.getAllFosterHomesByLocation(
            activistLongitude,
            activistLatitude,
            rangeLongitude,
            rangeLatitude
        ).map { list ->
            list.map { fosterHomeWithAllNonHumanAnimalData: FosterHomeWithAllNonHumanAnimalData ->

                fosterHomeWithAllNonHumanAnimalData.fosterHomeEntity.toDomain(
                    allAcceptedNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allAcceptedNonHumanAnimals.map { it.toDomain() },
                    allResidentNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.map {

                        it.toDomain(
                            onFetchNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->

                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
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
}
