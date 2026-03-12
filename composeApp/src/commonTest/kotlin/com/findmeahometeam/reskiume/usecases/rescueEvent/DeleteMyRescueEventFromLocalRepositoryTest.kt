package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.rescueEvent
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteMyRescueEventFromLocalRepositoryTest {

    private val localRescueEventRepository: LocalRescueEventRepository = mock {

        everySuspend {
            deleteRescueEvent(rescueEvent.id, any())
        } returns Unit
    }

    private val deleteMyRescueEventFromLocalRepository =
        DeleteMyRescueEventFromLocalRepository(localRescueEventRepository)

    @Test
    fun `given my local rescue event_when the app deletes it_then deleteRescueEvent is called`() =
        runTest {
            deleteMyRescueEventFromLocalRepository(rescueEvent.id) {}
            verifySuspend {
                localRescueEventRepository.deleteRescueEvent(rescueEvent.id, any())
            }
        }
}
