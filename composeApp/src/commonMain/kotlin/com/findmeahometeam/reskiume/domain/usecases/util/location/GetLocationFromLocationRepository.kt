package com.findmeahometeam.reskiume.domain.usecases.util.location

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository

class GetLocationFromLocationRepository(private val locationRepository: LocationRepository) {

    suspend operator fun invoke(): Pair<Double, Double> = locationRepository.getLocation()
}
