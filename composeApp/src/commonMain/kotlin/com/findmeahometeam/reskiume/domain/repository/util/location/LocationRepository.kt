package com.findmeahometeam.reskiume.domain.repository.util.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun observeIfLocationEnabledFlow(): Flow<Boolean>
    fun requestEnableLocation(onResult: (Boolean) -> Unit)
    suspend fun getLocation(): Pair<Double, Double>
}
