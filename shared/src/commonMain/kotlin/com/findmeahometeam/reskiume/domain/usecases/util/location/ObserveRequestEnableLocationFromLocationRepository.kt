package com.findmeahometeam.reskiume.domain.usecases.util.location

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository

class ObserveRequestEnableLocationFromLocationRepository(private val locationRepository: LocationRepository) {

    operator fun invoke() = locationRepository.observeRequestEnableLocation()
}
