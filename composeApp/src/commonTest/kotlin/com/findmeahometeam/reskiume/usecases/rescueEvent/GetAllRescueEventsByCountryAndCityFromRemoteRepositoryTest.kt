package com.findmeahometeam.reskiume.usecases.rescueEvent

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.rescueEvent
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllRescueEventsByCountryAndCityFromRemoteRepositoryTest: CoroutineTestDispatcher() {

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {
        every {
            getAllRemoteRescueEventsByCountryAndCity(rescueEvent.country, rescueEvent.city)
        } returns flowOf(listOf(rescueEvent.toData()))
    }

    private val getAllRescueEventsByCountryAndCityFromRemoteRepository =
        GetAllRescueEventsByCountryAndCityFromRemoteRepository(fireStoreRemoteRescueEventRepository)

    @Test
    fun `given remote rescue events_when the app retrieves them to list them by country and city_then the app gets a flow of list of RescueEvent`() =
        runTest {
            getAllRescueEventsByCountryAndCityFromRemoteRepository(
                rescueEvent.country,
                rescueEvent.city
            ).test {
                assertEquals(listOf(rescueEvent), awaitItem())
                awaitComplete()
            }
        }
}
