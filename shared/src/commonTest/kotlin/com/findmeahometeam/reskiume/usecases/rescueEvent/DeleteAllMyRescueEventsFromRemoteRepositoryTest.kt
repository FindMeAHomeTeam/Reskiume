package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteAllMyRescueEventsFromRemoteRepositoryTest {

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {
        everySuspend {
            getAllMyRemoteRescueEvents(rescueEvent.creatorId)
        } returns flowOf(listOf(rescueEvent.toData()))

        everySuspend {
            getAllMyRemoteRescueEvents("otherRescueEventCreatorId")
        } returns flowOf(listOf(rescueEvent.copy(allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
        }).toData()))

        everySuspend {
            deleteAllMyRemoteRescueEvents(rescueEvent.creatorId, any())
        } returns Unit

        everySuspend {
            deleteAllMyRemoteRescueEvents("otherRescueEventCreatorId", any())
        } returns Unit
    }

    val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
        mock {
            everySuspend {
                getRemoteNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
            } returns flowOf(nonHumanAnimal.toData())

            everySuspend {
                getRemoteNonHumanAnimal(nonHumanAnimal.id + "second", nonHumanAnimal.caregiverId)
            } returns flowOf(nonHumanAnimal.toData())

            everySuspend {
                getRemoteNonHumanAnimal(nonHumanAnimal.id + "other", nonHumanAnimal.caregiverId)
            } returns flowOf(null)

            everySuspend {
                modifyRemoteNonHumanAnimal(nonHumanAnimal.toData(), any())
            } returns Unit
        }

    val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = mock {
        everySuspend {
            deleteNonHumanAnimal(
                id = nonHumanAnimal.id + "other",
                caregiverId = nonHumanAnimal.caregiverId,
                coroutineScope = any(),
                onlyDeleteOnLocal = false,
                onError = any(),
                onComplete = any()
            )
        } returns Unit
    }

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val deleteAllMyRescueEventsFromRemoteRepository =
        DeleteAllMyRescueEventsFromRemoteRepository(
            authRepository,
            fireStoreRemoteRescueEventRepository,
            realtimeDatabaseRemoteNonHumanAnimalRepository,
            deleteNonHumanAnimalUtil,
            log
        )

    @Test
    fun `given my own remote rescue events_when the app deletes them on account deletion_then modifyRemoteNonHumanAnimal and deleteAllMyRemoteRescueEvents are called`() =
        runTest {
            deleteAllMyRescueEventsFromRemoteRepository(rescueEvent.creatorId, this) {}
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.toData(),
                    any()
                )
                fireStoreRemoteRescueEventRepository.deleteAllMyRemoteRescueEvents(
                    rescueEvent.creatorId,
                    any()
                )
            }
        }

    @Test
    fun `given my own remote rescue events_when the app deletes them on account deletion but the non human animals were deleted_then deleteNonHumanAnimal and deleteAllMyRemoteRescueEvents are called`() =
        runTest {
            deleteAllMyRescueEventsFromRemoteRepository("otherRescueEventCreatorId", TestScope()) {}
            verifySuspend {
                deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                    id = nonHumanAnimal.id + "other",
                    caregiverId = nonHumanAnimal.caregiverId,
                    coroutineScope = any(),
                    onlyDeleteOnLocal = false,
                    onError = any(),
                    onComplete = any()
                )
                fireStoreRemoteRescueEventRepository.deleteAllMyRemoteRescueEvents(
                    "otherRescueEventCreatorId",
                    any()
                )
            }
        }
}
