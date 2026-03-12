package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.rescueEvent
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteMyRescueEventFromRemoteRepositoryTest {

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {

        everySuspend {
            deleteRemoteRescueEvent(rescueEvent.id, any())
        } returns Unit
    }

    private val deleteMyRescueEventFromRemoteRepository =
        DeleteMyRescueEventFromRemoteRepository(fireStoreRemoteRescueEventRepository)

    @Test
    fun `given my remote rescue event_when the app deletes it_then deleteRemoteRescueEvent is called`() =
        runTest {
            deleteMyRescueEventFromRemoteRepository(rescueEvent.id) {}
            verifySuspend {
                fireStoreRemoteRescueEventRepository.deleteRemoteRescueEvent(rescueEvent.id, any())
            }
        }
}
