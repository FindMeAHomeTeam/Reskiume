package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocationRepository(
    private val location: Pair<Double, Double> = Pair(0.0, 0.0)
): LocationRepository {

    override fun observeIfLocationEnabledFlow(): Flow<Boolean> = flowOf(true)

    override fun requestEnableLocation(onResult: (isEnabled: Boolean) -> Unit) {
        onResult(true)
    }

    override suspend fun getLocation(): Pair<Double, Double> = location
}
