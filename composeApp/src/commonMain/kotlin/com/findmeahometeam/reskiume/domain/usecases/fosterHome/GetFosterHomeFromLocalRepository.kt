package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFosterHomeFromLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {

    operator fun invoke(id: String): Flow<FosterHome?> = flow {
        val result: FosterHome? = localFosterHomeRepository
            .getFosterHome(id)?.let { fosterHomeWithAllNonHumanAnimalData ->

                fosterHomeWithAllNonHumanAnimalData.fosterHomeEntity.toDomain(
                    allAcceptedNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allAcceptedNonHumanAnimals.map { it.toDomain() },
                    allResidentNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.map { it.toDomain()}
                )
            }
        emit(result)
    }
}
