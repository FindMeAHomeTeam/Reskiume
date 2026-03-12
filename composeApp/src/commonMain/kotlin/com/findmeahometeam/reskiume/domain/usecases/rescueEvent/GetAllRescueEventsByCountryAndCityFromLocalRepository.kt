package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllRescueEventsByCountryAndCityFromLocalRepository(private val localRescueEventRepository: LocalRescueEventRepository) {
    operator fun invoke(
        country: String,
        city: String
    ): Flow<List<RescueEvent>> =
        localRescueEventRepository.getAllRescueEventsByCountryAndCity(country, city)
            .map { list ->

                list.map { rescueEventWithAllNeedsAndNonHumanAnimalData: RescueEventWithAllNeedsAndNonHumanAnimalData ->
                    rescueEventWithAllNeedsAndNonHumanAnimalData.rescueEventEntity.toDomain(

                        allNonHumanAnimalsToRescue = rescueEventWithAllNeedsAndNonHumanAnimalData.allNonHumanAnimalsToRescue.map { it.toDomain() },
                        allNeedsToCover = rescueEventWithAllNeedsAndNonHumanAnimalData.allNeedsToCover.map { it.toDomain() }
                    )
                }
            }
}
