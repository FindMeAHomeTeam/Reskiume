package com.findmeahometeam.reskiume.usecases.rescueEvent

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.rescueEvent
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllRescueEventsByLocationFromRemoteRepositoryTest: CoroutineTestDispatcher() {

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {
        every {
            getAllRemoteRescueEventsByLocation(
                activistLongitude,
                activistLatitude,
                rescueEvent.longitude,
                rescueEvent.latitude
            )
        } returns flowOf(listOf(rescueEvent.toData()))
    }

    private val getAllRescueEventsByLocationFromRemoteRepository =
        GetAllRescueEventsByLocationFromRemoteRepository(fireStoreRemoteRescueEventRepository)

    @Test
    fun `given remote rescue events_when the app retrieves them to list them by location_then the app gets a flow of list of RescueEvent`() =
        runTest {
            getAllRescueEventsByLocationFromRemoteRepository(
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
