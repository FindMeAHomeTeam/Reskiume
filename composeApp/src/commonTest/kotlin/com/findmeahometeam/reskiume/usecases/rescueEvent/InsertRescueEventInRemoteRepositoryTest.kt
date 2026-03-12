package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInRemoteRepository
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertRescueEventInRemoteRepositoryTest {

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {
        everySuspend {
            getRemoteRescueEvent(rescueEvent.id)
        } returns flowOf(rescueEvent.toData())

        everySuspend {
            insertRemoteRescueEvent(rescueEvent.toData(), any())
        } returns Unit

        everySuspend {
            insertRemoteRescueEvent(
                rescueEvent.copy(
                    id = "otherRescueEventId",
                    allNonHumanAnimalsToRescue = emptyList()
                ).toData(),
                any()
            )
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
                modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toData(),
                    any()
                )
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

    private val insertRescueEventInRemoteRepository =
        InsertRescueEventInRemoteRepository(
            authRepository,
            fireStoreRemoteRescueEventRepository,
            realtimeDatabaseRemoteNonHumanAnimalRepository,
            deleteNonHumanAnimalUtil,
            log
        )

    @Test
    fun `given a remote rescue event_when the app inserts it_then modifyRemoteNonHumanAnimal and insertRemoteRescueEvent are called`() =
        runTest {
            insertRescueEventInRemoteRepository(rescueEvent, TestScope()) {}
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toData(),
                    any()
                )
                fireStoreRemoteRescueEventRepository.insertRemoteRescueEvent(
                    rescueEvent.toData(),
                    any()
                )
            }
        }

    @Test
    fun `given a remote rescue event_when the app inserts it but the residents were removed_then only insertRemoteRescueEvent is called`() =
        runTest {
            insertRescueEventInRemoteRepository(
                rescueEvent.copy(
                    id = "otherRescueEventId",
                    allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ),
                TestScope()
            ) {}
            verifySuspend {
                fireStoreRemoteRescueEventRepository.insertRemoteRescueEvent(
                    rescueEvent.copy(
                        id = "otherRescueEventId",
                        allNonHumanAnimalsToRescue = emptyList()
                    ).toData(),
                    any()
                )
            }
            verifySuspend(exactly(0)) {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toData(),
                    any()
                )
            }
        }
}
