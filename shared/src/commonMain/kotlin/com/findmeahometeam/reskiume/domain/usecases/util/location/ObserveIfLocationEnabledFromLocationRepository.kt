package com.findmeahometeam.reskiume.domain.usecases.util.location

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import kotlinx.coroutines.flow.Flow

class ObserveIfLocationEnabledFromLocationRepository(private val locationRepository: LocationRepository) {

    operator fun invoke(): Flow<Boolean> = locationRepository.observeIfLocationEnabledFlow()
}
