package com.findmeahometeam.reskiume.usecases.rescueEvent

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.rescueEvent
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRescueEventFromRemoteRepositoryTest: CoroutineTestDispatcher() {

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {
        every {
            getRemoteRescueEvent(
                rescueEvent.id
            )
        } returns flowOf(rescueEvent.toData())
    }

    private val getRescueEventFromRemoteRepository = GetRescueEventFromRemoteRepository(fireStoreRemoteRescueEventRepository)

    @Test
    fun `given a remote rescue event_when the app retrieves it to display it_then the app gets a flow of RescueEvent`() =
        runTest {
            getRescueEventFromRemoteRepository(rescueEvent.id).test {
                assertEquals(rescueEvent, awaitItem())
                awaitComplete()
            }
        }
}
