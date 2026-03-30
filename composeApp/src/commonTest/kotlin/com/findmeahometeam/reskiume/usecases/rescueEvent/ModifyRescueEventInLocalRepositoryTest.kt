package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
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

class ModifyRescueEventInLocalRepositoryTest {

    private val onModifyRescueEvent = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onInsertNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onDeleteNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteSecondNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteNeedToCoverForRescueEvent =
        Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val manageImagePath: ManageImagePath = mock {
        every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

        every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

        every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl
    }

    private val localRescueEventRepository: LocalRescueEventRepository = mock {
        everySuspend {
            modifyRescueEvent(
                rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                capture(onModifyRescueEvent)
            )
        } calls {
            onModifyRescueEvent.get().invoke(1)
        }

        everySuspend {
            modifyRescueEvent(
                rescueEvent.copy(
                    id = "otherRescueEventId",
                    savedBy = authUser.uid,
                    allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ).toEntity(),
                capture(onModifyRescueEvent)
            )
        } calls {
            onModifyRescueEvent.get().invoke(1)
        }

        everySuspend {
            modifyRescueEvent(
                rescueEvent.copy(
                    id = rescueEvent.id + "rescued",
                    savedBy = authUser.uid,
                    allNonHumanAnimalsToRescue = listOf(rescueEvent.allNonHumanAnimalsToRescue[0].copy(
                        nonHumanAnimalId = nonHumanAnimal.id + "rescued")
                    )
                ).toEntity(),
                capture(onModifyRescueEvent)
            )
        } calls {
            onModifyRescueEvent.get().invoke(1)
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

        everySuspend {
            deleteNonHumanAnimalToRescueEntityForRescueEvent(
                rescueEvent.allNonHumanAnimalsToRescue[0].nonHumanAnimalId,
                capture(onDeleteNonHumanAnimalToRescueForRescueEvent)
            )
        } calls {
            onDeleteNonHumanAnimalToRescueForRescueEvent.get().invoke(1)
        }

        everySuspend {
            deleteNonHumanAnimalToRescueEntityForRescueEvent(
                rescueEvent.allNonHumanAnimalsToRescue[0].nonHumanAnimalId + "rescued",
                capture(onDeleteNonHumanAnimalToRescueForRescueEvent)
            )
        } calls {
            onDeleteNonHumanAnimalToRescueForRescueEvent.get().invoke(1)
        }

        everySuspend {
            deleteNonHumanAnimalToRescueEntityForRescueEvent(
                rescueEvent.allNonHumanAnimalsToRescue[1].nonHumanAnimalId,
                capture(onDeleteSecondNonHumanAnimalToRescueForRescueEvent)
            )
        } calls {
            onDeleteSecondNonHumanAnimalToRescueForRescueEvent.get().invoke(1)
        }

        everySuspend {
            deleteNeedToCoverEntityForRescueEvent(
                rescueEvent.allNeedsToCover[1].needToCoverId,
                capture(onDeleteNeedToCoverForRescueEvent)
            )
        } calls {
            onDeleteNeedToCoverForRescueEvent.get().invoke(1)
        }

        everySuspend {
            deleteNonHumanAnimalToRescueEntityForRescueEvent(
                rescueEvent.allNonHumanAnimalsToRescue[0].nonHumanAnimalId + "other",
                capture(onDeleteNonHumanAnimalToRescueForRescueEvent)
            )
        } calls {
            onDeleteNonHumanAnimalToRescueForRescueEvent.get().invoke(1)
        }
    }

    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        everySuspend {
            getNonHumanAnimal(nonHumanAnimal.id)
        } returns nonHumanAnimal.toEntity()

        everySuspend {
            getNonHumanAnimal(nonHumanAnimal.id + "other")
        } returns null

        everySuspend {
            modifyNonHumanAnimal(
                nonHumanAnimal.copy(
                    nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                ).toEntity(),
                any()
            )
        } returns Unit

        everySuspend {
            modifyNonHumanAnimal(
                nonHumanAnimal.copy(
                    nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED
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
                nonHumanAnimal.id + "rescued",
                nonHumanAnimal.caregiverId,
                any()
            )
        } returns flowOf(nonHumanAnimal.copy(nonHumanAnimalState = NonHumanAnimalState.SAVED))

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

    private val modifyRescueEventInLocalRepository =
        ModifyRescueEventInLocalRepository(
            manageImagePath,
            checkNonHumanAnimalUtil,
            localRescueEventRepository,
            localNonHumanAnimalRepository,
            authRepository,
            log
        )

    @Test
    fun `given a local rescue event_when the app modifies it_then modifyRE insertNHAToRescue insertNeedToCover and modifyNHA are called`() =
        runTest {
            val previousRescueEvent = rescueEvent.copy(
                allNonHumanAnimalsToRescue = listOf(rescueEvent.allNonHumanAnimalsToRescue[0]),
                allNeedsToCover = emptyList()
            )

            modifyRescueEventInLocalRepository(rescueEvent, previousRescueEvent, this) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(rescueEvent.imageUrl)

                localRescueEventRepository.modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    onModifyRescueEvent.get()
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
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    any()
                )
            }
        }

    @Test
    fun `given a local rescue event_when the app modifies it but the NHA was removed and it is included on the list to rescue NHA_then modifyRescueEvent and insertNeedToCoverEntityForRecueEvent are called`() =
        runTest {
            val previousRescueEvent = rescueEvent.copy(
                allNonHumanAnimalsToRescue = emptyList(),
                allNeedsToCover = emptyList()
            )
            modifyRescueEventInLocalRepository(
                rescueEvent.copy(
                    id = "otherRescueEventId",
                    savedBy = authUser.uid,
                    allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ),
                previousRescueEvent,
                this
            ) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(rescueEvent.imageUrl)

                localRescueEventRepository.modifyRescueEvent(
                    rescueEvent.copy(
                        id = "otherRescueEventId",
                        savedBy = authUser.uid,
                        allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                        }
                    ).toEntity(),
                    onModifyRescueEvent.get()
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
                    rescueEvent.allNonHumanAnimalsToRescue[0].copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                        .toEntity(),
                    any()
                )
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    any()
                )
            }
        }

    @Test
    fun `given a local rescue event_when the app has to delete non human animals to rescue and needs to cover_then modifyRE deleteNHAToRescue deleteNeedToCover and modifyNHA are called`() =
        runTest {
            val updatedRescueEvent = rescueEvent.copy(
                allNonHumanAnimalsToRescue = listOf(rescueEvent.allNonHumanAnimalsToRescue[1]),
                allNeedsToCover = listOf(rescueEvent.allNeedsToCover[0])
            )
            val previousRescueEvent = rescueEvent

            modifyRescueEventInLocalRepository(
                updatedRescueEvent,
                previousRescueEvent,
                this
            ) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(rescueEvent.imageUrl)

                localRescueEventRepository.modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    onModifyRescueEvent.get()
                )
                localRescueEventRepository.deleteNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].nonHumanAnimalId,
                    onDeleteNonHumanAnimalToRescueForRescueEvent.get()
                )
                localRescueEventRepository.deleteNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[1].needToCoverId,
                    onDeleteNeedToCoverForRescueEvent.get()
                )
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED
                    ).toEntity(),
                    any()
                )
            }
        }

    @Test
    fun `given a local rescue event_when the app has to delete a non human animal to rescue but it was deleted_then only modifyRescueEvent and deleteNonHumanAnimalToRescueEntityForRescueEvent are called`() =
        runTest {
            val updatedRescueEvent = rescueEvent.copy(
                allNonHumanAnimalsToRescue = listOf(rescueEvent.allNonHumanAnimalsToRescue[1]),
                allNeedsToCover = listOf(rescueEvent.allNeedsToCover[0])
            )
            val previousRescueEvent = rescueEvent.copy(
                id = "otherRescueEventId",
                allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                    it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                }
            )

            modifyRescueEventInLocalRepository(
                updatedRescueEvent,
                previousRescueEvent,
                this
            ) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(rescueEvent.imageUrl)

                localRescueEventRepository.modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    onModifyRescueEvent.get()
                )
                localRescueEventRepository.deleteNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].nonHumanAnimalId + "other",
                    onDeleteNonHumanAnimalToRescueForRescueEvent.get()
                )
            }
            verifySuspend(exactly(0)) {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED,
                    ).toEntity(),
                    any()
                )
            }
        }

    @Test
    fun `given a local rescue event_when the user wants to remove a rescued NHA_then only modifyRescueEvent and deleteNonHumanAnimalToRescueEntityForRescueEvent are called`() =
        runTest {
            val updatedRescueEvent = rescueEvent.copy(
                id = rescueEvent.id + "rescued",
                allNonHumanAnimalsToRescue = emptyList()
            )
            val previousRescueEvent = rescueEvent.copy(
                id = rescueEvent.id + "rescued",
                allNonHumanAnimalsToRescue = listOf(rescueEvent.allNonHumanAnimalsToRescue[0].copy(
                    nonHumanAnimalId = nonHumanAnimal.id + "rescued")
                )
            )

            modifyRescueEventInLocalRepository(
                updatedRescueEvent,
                previousRescueEvent,
                this
            ) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(rescueEvent.imageUrl)

                localRescueEventRepository.modifyRescueEvent(
                    rescueEvent.copy(
                        id = rescueEvent.id + "rescued",
                        savedBy = authUser.uid,
                        allNonHumanAnimalsToRescue = listOf(rescueEvent.allNonHumanAnimalsToRescue[0].copy(
                            nonHumanAnimalId = nonHumanAnimal.id + "rescued")
                        )
                    ).toEntity(),
                    onModifyRescueEvent.get()
                )
                localRescueEventRepository.deleteNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].nonHumanAnimalId + "rescued",
                    onDeleteNonHumanAnimalToRescueForRescueEvent.get()
                )
            }
            verifySuspend(exactly(0)) {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED,
                    ).toEntity(),
                    any()
                )
            }
        }
}
