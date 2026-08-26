package com.findmeahometeam.reskiume.usecases.rescueEvent

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRescueEventFromLocalRepositoryTest: CoroutineTestDispatcher() {

    private val localRescueEventRepository: LocalRescueEventRepository = mock {
        everySuspend {
            getRescueEvent(rescueEvent.id)
        } returns rescueEventWithAllNeedsAndNonHumanAnimalData
    }

    private val getRescueEventFromLocalRepository =
        GetRescueEventFromLocalRepository(localRescueEventRepository)

    @Test
    fun `given a local rescue event_when the app retrieves it to display it_then the app gets a flow of RescueEvent`() =
        runTest {
            getRescueEventFromLocalRepository(
                rescueEvent.id
            ).test {
                assertEquals(rescueEvent, awaitItem())
                awaitComplete()
            }
        }
}
