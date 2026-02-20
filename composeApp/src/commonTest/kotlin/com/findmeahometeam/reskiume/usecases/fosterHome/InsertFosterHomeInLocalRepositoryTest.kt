package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.nonHumanAnimal
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
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertFosterHomeInLocalRepositoryTest {

    private val onInsertFosterHome = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertAcceptedNonHumanAnimalForFosterHome = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertAcceptedSecondNonHumanAnimalForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    val manageImagePath: ManageImagePath = mock {

        every { getImagePathForFileName(fosterHome.imageUrl) } returns fosterHome.imageUrl

        every { getFileNameFromLocalImagePath(fosterHome.imageUrl) } returns fosterHome.imageUrl

        every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl
    }

    val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        everySuspend {
            insertFosterHome(
                fosterHome.copy(savedBy = authUser.uid).toEntity(),
                capture(onInsertFosterHome)
            )
        } calls {
            onInsertFosterHome.get().invoke(1L)
        }

        everySuspend {
            insertFosterHome(
                fosterHome.copy(
                    id = "otherFosterHomeId",
                    savedBy = authUser.uid,
                    allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ).toEntity(),
                capture(onInsertFosterHome)
            )
        } calls {
            onInsertFosterHome.get().invoke(1L)
        }

        everySuspend {
            insertAcceptedNonHumanAnimalForFosterHome(
                fosterHome.allAcceptedNonHumanAnimals[0].toEntity(),
                capture(onInsertAcceptedNonHumanAnimalForFosterHome)
            )
        } calls {
            onInsertAcceptedNonHumanAnimalForFosterHome.get().invoke(1L)
        }

        everySuspend {
            insertAcceptedNonHumanAnimalForFosterHome(
                fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                capture(onInsertAcceptedSecondNonHumanAnimalForFosterHome)
            )
        } calls {
            onInsertAcceptedSecondNonHumanAnimalForFosterHome.get().invoke(1L)
        }

        everySuspend {
            insertResidentNonHumanAnimalIdForFosterHome(
                fosterHome.allResidentNonHumanAnimals[0].toEntity(),
                capture(onInsertResidentNonHumanAnimalIdForFosterHome)
            )
        } calls {
            onInsertResidentNonHumanAnimalIdForFosterHome.get().invoke(1L)
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
                    adoptionState = AdoptionState.REHOMED,
                    fosterHomeId = fosterHome.id
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

    private val insertFosterHomeInLocalRepository =
        InsertFosterHomeInLocalRepository(
            localFosterHomeRepository,
            manageImagePath,
            localNonHumanAnimalRepository,
            checkNonHumanAnimalUtil,
            authRepository,
            log
        )

    @Test
    fun `given a local foster home_when the app inserts it_then inserts methods are called`() =
        runTest {
            insertFosterHomeInLocalRepository(fosterHome, TestScope()) {}

            verifySuspend {
                localFosterHomeRepository.insertFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    onInsertFosterHome.get()
                )
                localFosterHomeRepository.insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[0].toEntity(),
                    onInsertAcceptedNonHumanAnimalForFosterHome.get()
                )
                localFosterHomeRepository.insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                    onInsertAcceptedSecondNonHumanAnimalForFosterHome.get()
                )
                localFosterHomeRepository.insertResidentNonHumanAnimalIdForFosterHome(
                    fosterHome.allResidentNonHumanAnimals[0].toEntity(),
                    onInsertResidentNonHumanAnimalIdForFosterHome.get()
                )
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.REHOMED,
                        fosterHomeId = fosterHome.id
                    ).toEntity(),
                    any()
                )
            }
        }

    @Test
    fun `given a local foster home_when the app inserts it but the residents were deleted_then inserts methods are called except insertResidentNonHumanAnimalIdForFosterHome`() =
        runTest {
            insertFosterHomeInLocalRepository(
                fosterHome.copy(
                    id = "otherFosterHomeId",
                    savedBy = authUser.uid,
                    allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ),
                TestScope()
            ) {}

            verifySuspend {
                localFosterHomeRepository.insertFosterHome(
                    fosterHome.copy(
                        id = "otherFosterHomeId",
                        savedBy = authUser.uid,
                        allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                        }
                    ).toEntity(),
                    onInsertFosterHome.get()
                )
                localFosterHomeRepository.insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[0].toEntity(),
                    onInsertAcceptedNonHumanAnimalForFosterHome.get()
                )
                localFosterHomeRepository.insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                    onInsertAcceptedSecondNonHumanAnimalForFosterHome.get()
                )
            }
            verifySuspend(exactly(0)) {
                localFosterHomeRepository.insertResidentNonHumanAnimalIdForFosterHome(
                    fosterHome.allResidentNonHumanAnimals[0].toEntity(),
                    any()
                )
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.REHOMED,
                        fosterHomeId = fosterHome.id
                    ).toEntity(),
                    any()
                )
            }
        }
}
