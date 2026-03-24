package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertRescueEventInLocalRepositoryTest {

    private val onInsertRescueEvent = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertNonHumanAnimalToRescueForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNeedToCoverForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNeedToCoverForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    val manageImagePath: ManageImagePath = mock {

        every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

        every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

        every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl
    }

    val localRescueEventRepository: LocalRescueEventRepository = mock {
        everySuspend {
            insertRescueEvent(
                rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                capture(onInsertRescueEvent)
            )
        } calls {
            onInsertRescueEvent.get().invoke(1L)
        }

        everySuspend {
            insertRescueEvent(
                rescueEvent.copy(
                    id = "otherRescueEventId",
                    savedBy = authUser.uid,
                    allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ).toEntity(),
                capture(onInsertRescueEvent)
            )
        } calls {
            onInsertRescueEvent.get().invoke(1L)
        }

        everySuspend {
            insertNonHumanAnimalToRescueEntityForRescueEvent(
                rescueEvent.allNonHumanAnimalsToRescue[0].toEntity(),
                capture(onInsertNonHumanAnimalToRescueForRescueEvent)
            )
        } calls {
            onInsertNonHumanAnimalToRescueForRescueEvent.get().invoke(1L)
        }

        everySuspend {
            insertNonHumanAnimalToRescueEntityForRescueEvent(
                rescueEvent.allNonHumanAnimalsToRescue[1].toEntity(),
                capture(onInsertSecondNonHumanAnimalToRescueForRescueEvent)
            )
        } calls {
            onInsertSecondNonHumanAnimalToRescueForRescueEvent.get().invoke(1L)
        }

        everySuspend {
            insertNeedToCoverEntityForRescueEvent(
                rescueEvent.allNeedsToCover[0].toEntity(),
                capture(onInsertNeedToCoverForRescueEvent)
            )
        } calls {
            onInsertNeedToCoverForRescueEvent.get().invoke(1L)
        }

        everySuspend {
            insertNeedToCoverEntityForRescueEvent(
                rescueEvent.allNeedsToCover[1].toEntity(),
                capture(onInsertSecondNeedToCoverForRescueEvent)
            )
        } calls {
            onInsertSecondNeedToCoverForRescueEvent.get().invoke(1L)
        }
    }

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        everySuspend {
            getNonHumanAnimal(nonHumanAnimal.id)
        } returns nonHumanAnimal.toEntity()

        everySuspend {
            getNonHumanAnimal(nonHumanAnimal.id + "other")
        } returns null

        everySuspend {
            modifyNonHumanAnimal(
                nonHumanAnimal.copy(
                    adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                ).toEntity(),
                any()
            )
        } returns Unit
    }

    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {
        every {
            getNonHumanAnimalFlow(
                nonHumanAnimal.id,
                nonHumanAnimal.caregiverId,
                any()
            )
        } returns flowOf(nonHumanAnimal)

        every {
            getNonHumanAnimalFlow(
                nonHumanAnimal.id + "second",
                nonHumanAnimal.caregiverId,
                any()
            )
        } returns flowOf(nonHumanAnimal)

        every {
            getNonHumanAnimalFlow(
                nonHumanAnimal.id + "other",
                nonHumanAnimal.caregiverId,
                any()
            )
        } returns flowOf()
    }

    private val authRepository: AuthRepository = mock {
        everySuspend {
            authState
        } returns flowOf(authUser)
    }

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val insertRescueEventInLocalRepository =
        InsertRescueEventInLocalRepository(
            checkNonHumanAnimalUtil,
            localRescueEventRepository,
            localNonHumanAnimalRepository,
            manageImagePath,
            authRepository,
            log
        )

    @Test
    fun `given a local rescue event_when the app inserts it_then inserts methods are called`() =
        runTest {
            insertRescueEventInLocalRepository(rescueEvent, this) {}

            verifySuspend {
                localRescueEventRepository.insertRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    onInsertRescueEvent.get()
                )
                localRescueEventRepository.insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].toEntity(),
                    onInsertNonHumanAnimalToRescueForRescueEvent.get()
                )
                localRescueEventRepository.insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[1].toEntity(),
                    onInsertSecondNonHumanAnimalToRescueForRescueEvent.get()
                )
                localRescueEventRepository.insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[0].toEntity(),
                    onInsertNeedToCoverForRescueEvent.get()
                )
                localRescueEventRepository.insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[1].toEntity(),
                    onInsertSecondNeedToCoverForRescueEvent.get()
                )
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    any()
                )
            }
        }

    @Test
    fun `given a local rescue event_when the app inserts it but the residents were deleted_then inserts methods are called except insertNeedToCoverEntityForRescueEvent and modifyNonHumanAnimal`() =
        runTest {
            insertRescueEventInLocalRepository(
                rescueEvent.copy(
                    id = "otherRescueEventId",
                    savedBy = authUser.uid,
                    allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ),
                this
            ) {}

            verifySuspend {
                localRescueEventRepository.insertRescueEvent(
                    rescueEvent.copy(
                        id = "otherRescueEventId",
                        savedBy = authUser.uid,
                        allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                        }
                    ).toEntity(),
                    onInsertRescueEvent.get()
                )
                localRescueEventRepository.insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[0].toEntity(),
                    onInsertNeedToCoverForRescueEvent.get()
                )
                localRescueEventRepository.insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[1].toEntity(),
                    onInsertSecondNeedToCoverForRescueEvent.get()
                )
            }
            verifySuspend(exactly(0)) {
                localRescueEventRepository.insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].toEntity(),
                    any()
                )
                localRescueEventRepository.insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[1].toEntity(),
                    any()
                )
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    any()
                )
            }
        }
}
