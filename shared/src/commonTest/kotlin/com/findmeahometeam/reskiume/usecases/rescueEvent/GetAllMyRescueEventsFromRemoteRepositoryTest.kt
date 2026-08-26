package com.findmeahometeam.reskiume.usecases.rescueEvent

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.rescueEvent
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllMyRescueEventsFromRemoteRepositoryTest : CoroutineTestDispatcher() {

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {
        every {
            getAllMyRemoteRescueEvents(rescueEvent.creatorId)
        } returns flowOf(listOf(rescueEvent.toData()))
    }

    private val getAllMyRescueEventsFromRemoteRepository =
        GetAllMyRescueEventsFromRemoteRepository(fireStoreRemoteRescueEventRepository)

    @Test
    fun `given my own remote rescue events_when the app retrieves them to list them_then the app gets a flow of list of RescueEvent`() =
        runTest {
            getAllMyRescueEventsFromRemoteRepository(
                rescueEvent.creatorId
            ).test {
                assertEquals(listOf(rescueEvent), awaitItem())
                awaitComplete()
            }
        }
}
