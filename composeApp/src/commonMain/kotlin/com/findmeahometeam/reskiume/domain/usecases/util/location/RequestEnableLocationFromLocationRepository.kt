package com.findmeahometeam.reskiume.domain.usecases.util.location

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository

class RequestEnableLocationFromLocationRepository(private val locationRepository: LocationRepository) {

    operator fun invoke(onResult: (isEnabled: Boolean) -> Unit) = locationRepository.requestEnableLocation(onResult)
}
