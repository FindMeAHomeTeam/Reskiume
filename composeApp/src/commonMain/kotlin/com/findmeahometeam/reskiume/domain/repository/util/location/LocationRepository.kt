package com.findmeahometeam.reskiume.domain.repository.util.location

interface LocationRepository {

    fun isLocationEnabled(): Boolean
    fun requestEnableLocation(onResult: (Boolean) -> Unit)
    suspend fun getLocation(): Pair<Double, Double>
}
