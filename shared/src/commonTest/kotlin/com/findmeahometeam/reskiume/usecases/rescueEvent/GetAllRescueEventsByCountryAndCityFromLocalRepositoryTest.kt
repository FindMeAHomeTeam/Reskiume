package com.findmeahometeam.reskiume.usecases.rescueEvent

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllRescueEventsByCountryAndCityFromLocalRepositoryTest: CoroutineTestDispatcher() {

    private val localRescueEventRepository: LocalRescueEventRepository = mock {
        every {
            getAllRescueEventsByCountryAndCity(rescueEvent.country, rescueEvent.city)
        } returns flowOf(listOf(rescueEventWithAllNeedsAndNonHumanAnimalData))
    }

    private val getAllRescueEventsByCountryAndCityFromLocalRepository =
        GetAllRescueEventsByCountryAndCityFromLocalRepository(localRescueEventRepository)

    @Test
    fun `given local rescue events_when the app retrieves them to list them by country and city_then the app gets a flow of list of RescueEvent`() =
        runTest {
            getAllRescueEventsByCountryAndCityFromLocalRepository(
                rescueEvent.country,
                rescueEvent.city
            ).test {
                assertEquals(listOf(rescueEvent), awaitItem())
                awaitComplete()
            }
        }
}
