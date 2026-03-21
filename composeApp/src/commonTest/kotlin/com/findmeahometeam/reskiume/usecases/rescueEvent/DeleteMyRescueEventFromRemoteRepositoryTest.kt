package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromRemoteRepository
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
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteMyRescueEventFromRemoteRepositoryTest {

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {
        everySuspend {
            getRemoteRescueEvent(rescueEvent.id)
        } returns flowOf(rescueEvent.toData())

        everySuspend {
            getRemoteRescueEvent("otherRescueEventId")
        } returns flowOf(rescueEvent.copy(allNonHumanAnimalsToRescue = listOf(
            rescueEvent.allNonHumanAnimalsToRescue[0].copy(nonHumanAnimalId = nonHumanAnimal.id + "firstOther"),
            rescueEvent.allNonHumanAnimalsToRescue[1].copy(nonHumanAnimalId = nonHumanAnimal.id + "secondOther")
        )).toData())

        everySuspend {
            deleteRemoteRescueEvent(rescueEvent.id, any())
        } returns Unit

        everySuspend {
            deleteRemoteRescueEvent("otherRescueEventId", any())
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
                getRemoteNonHumanAnimal(nonHumanAnimal.id + "firstOther", nonHumanAnimal.caregiverId)
            } returns flowOf(null)

            everySuspend {
                getRemoteNonHumanAnimal(nonHumanAnimal.id + "secondOther", nonHumanAnimal.caregiverId)
            } returns flowOf(null)

            everySuspend {
                modifyRemoteNonHumanAnimal(nonHumanAnimal.toData(), any())
            } returns Unit
        }

    val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = mock {
        everySuspend {
            deleteNonHumanAnimal(
                id = nonHumanAnimal.id + "firstOther",
                caregiverId = nonHumanAnimal.caregiverId,
                coroutineScope = any(),
                onlyDeleteOnLocal = false,
                onError = any(),
                onComplete = any()
            )
        } returns Unit

        everySuspend {
            deleteNonHumanAnimal(
                id = nonHumanAnimal.id + "secondOther",
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

    private val deleteMyRescueEventFromRemoteRepository =
        DeleteMyRescueEventFromRemoteRepository(
            authRepository,
            fireStoreRemoteRescueEventRepository,
            realtimeDatabaseRemoteNonHumanAnimalRepository,
            deleteNonHumanAnimalUtil,
            log
        )

    @Test
    fun `given my remote rescue event_when the app deletes it_then modifyRemoteNonHumanAnimal and deleteRemoteRescueEvent are called`() =
        runTest {
            deleteMyRescueEventFromRemoteRepository(
                rescueEvent.id,
                this
            ) {}
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.toData(),
                    any()
                )
                fireStoreRemoteRescueEventRepository.deleteRemoteRescueEvent(
                    rescueEvent.id,
                    any()
                )
            }
        }

    @Test
    fun `given my remote RE_when the app deletes it on account deletion but the NHAs were deleted_then deleteNonHumanAnimal and deleteRemoteRescueEvent are called`() =
        runTest {
            deleteMyRescueEventFromRemoteRepository(
                "otherRescueEventId",
                this
            ) {}
            verifySuspend {
                deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                    id = nonHumanAnimal.id + "firstOther",
                    caregiverId = nonHumanAnimal.caregiverId,
                    coroutineScope = any(),
                    onlyDeleteOnLocal = false,
                    onError = any(),
                    onComplete = any()
                )
                deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                    id = nonHumanAnimal.id + "secondOther",
                    caregiverId = nonHumanAnimal.caregiverId,
                    coroutineScope = any(),
                    onlyDeleteOnLocal = false,
                    onError = any(),
                    onComplete = any()
                )
                fireStoreRemoteRescueEventRepository.deleteRemoteRescueEvent(
                    "otherRescueEventId",
                    any()
                )
            }
        }
}
