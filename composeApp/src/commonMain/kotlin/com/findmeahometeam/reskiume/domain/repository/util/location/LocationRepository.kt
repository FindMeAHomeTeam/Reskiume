package com.findmeahometeam.reskiume.domain.repository.util.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun observeIfLocationEnabledFlow(): Flow<Boolean>
    fun requestEnableLocation(onResult: (isEnabled: Boolean) -> Unit)
    suspend fun getLocation(): Pair<Double, Double>
}
