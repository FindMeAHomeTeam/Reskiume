package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class GetFosterHomeFromLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
) {
    operator fun invoke(id: String): Flow<FosterHome?> = flow {

        val result: FosterHome? = localFosterHomeRepository
            .getFosterHome(id)?.let { fosterHomeWithAllNonHumanAnimalData ->

                fosterHomeWithAllNonHumanAnimalData.fosterHomeEntity.toDomain(
                    allAcceptedNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allAcceptedNonHumanAnimals.map { it.toDomain() },
                    allResidentNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.map {

                        it.toDomain(
                            onFetchNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->

                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                    nonHumanAnimalId = nonHumanAnimalId,
                                    caregiverId = caregiverId
                                ).firstOrNull()
                            }
                        )
                    }
                )
            }
        emit(result)
    }
}
