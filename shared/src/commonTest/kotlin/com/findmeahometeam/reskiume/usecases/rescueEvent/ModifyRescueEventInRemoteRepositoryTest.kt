package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInRemoteRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
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
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyRescueEventInRemoteRepositoryTest {

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {
        everySuspend {
            getRemoteRescueEvent(rescueEvent.id)
        } returns flowOf(
            rescueEvent.copy(
                allNonHumanAnimalsToRescue = listOf(rescueEvent.allNonHumanAnimalsToRescue[0]),
                allNeedsToCover = emptyList()
            ).toData()
        )

        everySuspend {
            getRemoteRescueEvent(rescueEvent.id + "rescued")
        } returns flowOf(
            rescueEvent.copy(
                allNonHumanAnimalsToRescue = listOf(
                    rescueEvent.allNonHumanAnimalsToRescue[0].copy(
                        nonHumanAnimalId = nonHumanAnimal.id + "rescued"
                    )
                )
            ).toData()
        )

        everySuspend {
            getRemoteRescueEvent("otherRescueEventId")
        } returns flowOf(
            rescueEvent.copy(
                allNonHumanAnimalsToRescue = listOf(rescueEvent.allNonHumanAnimalsToRescue[0]),
                allNeedsToCover = emptyList()
            ).toData()
        )

        everySuspend {
            getRemoteRescueEvent("wrongId")
        } returns flowOf()

        everySuspend {
            modifyRemoteRescueEvent(rescueEvent.toData(), any())
        } returns Unit

        everySuspend {
            modifyRemoteRescueEvent(
                rescueEvent.copy(
                    id = rescueEvent.id + "rescued",
                    allNonHumanAnimalsToRescue = emptyList(),
                    allNeedsToCover = emptyList()
                ).toData(),
                any()
            )
        } returns Unit

        everySuspend {
            modifyRemoteRescueEvent(
                rescueEvent.copy(
                    id = "otherRescueEventId",
                    allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
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
                getRemoteNonHumanAnimal(nonHumanAnimal.id + "rescued", nonHumanAnimal.caregiverId)
            } returns flowOf(nonHumanAnimal.copy(nonHumanAnimalState = NonHumanAnimalState.SAVED).toData())

            everySuspend {
                modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toData(),
                    any()
                )
            } returns Unit

            everySuspend {
                modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED
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

    private val modifyRescueEventInRemoteRepository =
        ModifyRescueEventInRemoteRepository(
            authRepository,
            fireStoreRemoteRescueEventRepository,
            realtimeDatabaseRemoteNonHumanAnimalRepository,
            deleteNonHumanAnimalUtil,
            log
        )

    @Test
    fun `given a remote rescue event_when the app modifies it_then modifyRemoteNonHumanAnimal and modifyRemoteRescueEvent are called`() =
        runTest {
            modifyRescueEventInRemoteRepository(
                rescueEvent,
                coroutineScope = this
            ) {}
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toData(),
                    any()
                )
                fireStoreRemoteRescueEventRepository.modifyRemoteRescueEvent(
                    rescueEvent.toData(),
                    any()
                )
            }
        }

    @Test
    fun `given a remote rescue event_when the app try to modify it but fails retrieving the previous rescue event_then it will not call anything`() =
        runTest {
            modifyRescueEventInRemoteRepository(
                rescueEvent.copy(id = "wrongId"),
                coroutineScope = this
            ) {}
            verifySuspend(exactly(0)) {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toData(),
                    any()
                )
                fireStoreRemoteRescueEventRepository.modifyRemoteRescueEvent(
                    rescueEvent.toData(),
                    any()
                )
            }
        }

    @Test
    fun `given a remote rescue event_when the app modifies it but the resident was deleted_then only deleteNonHumanAnimal is called`() =
        runTest {
            modifyRescueEventInRemoteRepository(
                rescueEvent.copy(
                    id = "otherRescueEventId",
                    allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ),
                coroutineScope = this
            ) {}
            verifySuspend {
                deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
                    id = nonHumanAnimal.id + "other",
                    caregiverId = nonHumanAnimal.caregiverId,
                    coroutineScope = any(),
                    onlyDeleteOnLocal = false,
                    onError = any(),
                    onComplete = any()
                )
            }
            verifySuspend(exactly(0)) {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toData(),
                    any()
                )
                fireStoreRemoteRescueEventRepository.modifyRemoteRescueEvent(
                    rescueEvent.toData(),
                    any()
                )
            }
        }

    @Test
    fun `given a remote rescue event_when the app modifies it but the non human animal was adopted_then the app only calls to modifyRemoteRescueEvent`() =
        runTest {
            modifyRescueEventInRemoteRepository(
                rescueEvent.copy(
                    id = rescueEvent.id + "rescued",
                    allNonHumanAnimalsToRescue = emptyList(),
                    allNeedsToCover = emptyList()
                ),
                coroutineScope = this
            ) {}
            verifySuspend {
                fireStoreRemoteRescueEventRepository.modifyRemoteRescueEvent(
                    rescueEvent.copy(
                        id = rescueEvent.id + "rescued",
                        allNonHumanAnimalsToRescue = emptyList(),
                        allNeedsToCover = emptyList()
                    ).toData(),
                    any()
                )
            }
            verifySuspend(exactly(0)) {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toData(),
                    any()
                )
            }
        }
}
