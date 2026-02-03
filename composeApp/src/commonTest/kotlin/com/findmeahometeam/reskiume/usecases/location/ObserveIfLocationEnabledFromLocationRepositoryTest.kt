package com.findmeahometeam.reskiume.usecases.location

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveIfLocationEnabledFromLocationRepositoryTest: CoroutineTestDispatcher() {

    private val locationRepository: LocationRepository = mock {
        everySuspend {
            observeIfLocationEnabledFlow()
        } returns flowOf(true)
    }

    private val observeIfLocationEnabledFromLocationRepository =
        ObserveIfLocationEnabledFromLocationRepository(locationRepository)

    @Test
    fun `given the location services_when the app cheks if they are enabled_then the app gets a flow of its state`() =
        runTest {
            observeIfLocationEnabledFromLocationRepository().test {
                assertEquals(true, awaitItem())
                awaitComplete()
            }
        }
}
