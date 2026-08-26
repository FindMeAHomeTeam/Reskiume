package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllFosterHomesFromLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {
    operator fun invoke(): Flow<List<FosterHome>> =
        localFosterHomeRepository.getAllFosterHomes()
            .map { list ->

                list.map { fosterHomeWithAllNonHumanAnimalData: FosterHomeWithAllNonHumanAnimalData ->
                    fosterHomeWithAllNonHumanAnimalData.fosterHomeEntity.toDomain(
                        allAcceptedNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allAcceptedNonHumanAnimals.map { it.toDomain() },
                        allResidentNonHumanAnimals = fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.map { it.toDomain() }
                    )
                }
            }
}
