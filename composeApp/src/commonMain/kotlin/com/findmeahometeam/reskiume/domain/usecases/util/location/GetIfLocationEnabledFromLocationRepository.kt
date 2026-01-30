package com.findmeahometeam.reskiume.domain.usecases.util.location

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository

class GetIfLocationEnabledFromLocationRepository(private val locationRepository: LocationRepository) {

    operator fun invoke(): Boolean = locationRepository.isLocationEnabled()
}
