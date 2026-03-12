package com.findmeahometeam.reskiume.usecases.rescueEvent

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromLocalRepository
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllRescueEventsByLocationFromLocalRepositoryTest: CoroutineTestDispatcher() {

    private val localRescueEventRepository: LocalRescueEventRepository = mock {
        every {
            getAllRescueEventsByLocation(
                activistLongitude,
                activistLatitude,
                rescueEvent.longitude,
                rescueEvent.latitude
            )
        } returns flowOf(listOf(rescueEventWithAllNeedsAndNonHumanAnimalData))
    }

    private val getAllRescueEventsByLocationFromLocalRepository =
        GetAllRescueEventsByLocationFromLocalRepository(localRescueEventRepository)

    @Test
    fun `given local rescue events_when the app retrieves them to list them by location_then the app gets a flow of list of RescueEvent`() =
        runTest {
            getAllRescueEventsByLocationFromLocalRepository(
                activistLongitude,
                activistLatitude,
                rescueEvent.longitude,
                rescueEvent.latitude
            ).test {
                assertEquals(listOf(rescueEvent), awaitItem())
                awaitComplete()
            }
        }
}
