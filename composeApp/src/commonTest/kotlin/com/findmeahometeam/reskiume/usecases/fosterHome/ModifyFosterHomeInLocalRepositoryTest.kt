package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
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

class ModifyFosterHomeInLocalRepositoryTest {

    private val onModifyFosterHome = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onInsertAcceptedNonHumanAnimalForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onDeleteAcceptedNonHumanAnimalForFosterHome =
        Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val manageImagePath: ManageImagePath = mock {
        every { getImagePathForFileName(fosterHome.imageUrl) } returns fosterHome.imageUrl

        every { getFileNameFromLocalImagePath(fosterHome.imageUrl) } returns fosterHome.imageUrl

        every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl
    }

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        everySuspend {
            modifyFosterHome(
                fosterHome.copy(savedBy = authUser.uid).toEntity(),
                capture(onModifyFosterHome)
            )
        } calls {
            onModifyFosterHome.get().invoke(1)
        }

        everySuspend {
            modifyFosterHome(
                fosterHome.copy(
                    id = "otherFosterHomeId",
                    savedBy = authUser.uid,
                    allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ).toEntity(),
                capture(onModifyFosterHome)
            )
        } calls {
            onModifyFosterHome.get().invoke(1)
        }

        everySuspend {
            insertAcceptedNonHumanAnimalForFosterHome(
                fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                capture(onInsertAcceptedNonHumanAnimalForFosterHome)
            )
        } calls {
            onInsertAcceptedNonHumanAnimalForFosterHome.get().invoke(1L)
        }

        everySuspend {
            insertResidentNonHumanAnimalIdForFosterHome(
                fosterHome.allResidentNonHumanAnimals[0].toEntity(),
                capture(onInsertResidentNonHumanAnimalIdForFosterHome)
            )
        } calls {
            onInsertResidentNonHumanAnimalIdForFosterHome.get().invoke(1L)
        }

        everySuspend {
            deleteAcceptedNonHumanAnimal(
                fosterHome.allAcceptedNonHumanAnimals[1].acceptedNonHumanAnimalId,
                capture(onDeleteAcceptedNonHumanAnimalForFosterHome)
            )
        } calls {
            onDeleteAcceptedNonHumanAnimalForFosterHome.get().invoke(1)
        }

        everySuspend {
            deleteResidentNonHumanAnimal(
                fosterHome.allResidentNonHumanAnimals[0].nonHumanAnimalId,
                capture(onDeleteResidentNonHumanAnimalIdForFosterHome)
            )
        } calls {
            onDeleteResidentNonHumanAnimalIdForFosterHome.get().invoke(1)
        }

        everySuspend {
            deleteResidentNonHumanAnimal(
                fosterHome.allResidentNonHumanAnimals[0].nonHumanAnimalId + "other",
                capture(onDeleteResidentNonHumanAnimalIdForFosterHome)
            )
        } calls {
            onDeleteResidentNonHumanAnimalIdForFosterHome.get().invoke(1)
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
                    adoptionState = AdoptionState.REHOMED,
                    fosterHomeId = fosterHome.id
                ).toEntity(),
                any()
            )
        } returns Unit

        everySuspend {
            modifyNonHumanAnimal(
                nonHumanAnimal.copy(
                    adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                    fosterHomeId = ""
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

    private val modifyFosterHomeInLocalRepository =
        ModifyFosterHomeInLocalRepository(
            manageImagePath,
            localFosterHomeRepository,
            localNonHumanAnimalRepository,
            checkNonHumanAnimalUtil,
            authRepository,
            log
        )

    @Test
    fun `given a local foster home_when the app modifies it_then modifyFH insertAcceptedNHA insertResidentNHA and modifyNHA are called`() =
        runTest {
            val previousFosterHome = fosterHome.copy(
                allAcceptedNonHumanAnimals = listOf(fosterHome.allAcceptedNonHumanAnimals[0]),
                allResidentNonHumanAnimals = emptyList()
            )

            modifyFosterHomeInLocalRepository(fosterHome, previousFosterHome, TestScope()) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(fosterHome.imageUrl)

                localFosterHomeRepository.modifyFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    onModifyFosterHome.get()
                )
                localFosterHomeRepository.insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                    onInsertAcceptedNonHumanAnimalForFosterHome.get()
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
    fun `given a local foster home_when the app modifies it but the resident was removed_then modifies methods are called`() =
        runTest {
            val previousFosterHome = fosterHome.copy(
                allAcceptedNonHumanAnimals = listOf(fosterHome.allAcceptedNonHumanAnimals[0]),
                allResidentNonHumanAnimals = emptyList()
            )
            modifyFosterHomeInLocalRepository(
                fosterHome.copy(
                    id = "otherFosterHomeId",
                    savedBy = authUser.uid,
                    allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ),
                previousFosterHome,
                TestScope()
            ) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(fosterHome.imageUrl)

                localFosterHomeRepository.modifyFosterHome(
                    fosterHome.copy(
                        id = "otherFosterHomeId",
                        savedBy = authUser.uid,
                        allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                        }
                    ).toEntity(),
                    onModifyFosterHome.get()
                )
                localFosterHomeRepository.insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                    onInsertAcceptedNonHumanAnimalForFosterHome.get()
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

    @Test
    fun `given a local foster home_when the app has to delete accepted and resident non human animals_then modifyFH deleteAcceptedNHA deleteResidentNHA and modifyNHA are called`() =
        runTest {
            val updatedFosterHome = fosterHome.copy(
                allAcceptedNonHumanAnimals = listOf(fosterHome.allAcceptedNonHumanAnimals[0]),
                allResidentNonHumanAnimals = emptyList()
            )
            val previousFosterHome = fosterHome

            modifyFosterHomeInLocalRepository(
                updatedFosterHome,
                previousFosterHome,
                TestScope()
            ) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(fosterHome.imageUrl)

                localFosterHomeRepository.modifyFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    onModifyFosterHome.get()
                )
                localFosterHomeRepository.deleteAcceptedNonHumanAnimal(
                    fosterHome.allAcceptedNonHumanAnimals[1].acceptedNonHumanAnimalId,
                    onDeleteAcceptedNonHumanAnimalForFosterHome.get()
                )
                localFosterHomeRepository.deleteResidentNonHumanAnimal(
                    fosterHome.allResidentNonHumanAnimals[0].nonHumanAnimalId,
                    onDeleteResidentNonHumanAnimalIdForFosterHome.get()
                )
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                        fosterHomeId = ""
                    ).toEntity(),
                    any()
                )
            }
        }

    @Test
    fun `given a local foster home_when the app has to delete a resident non human animal but it was deleted_then only modifyFosterHome and deleteResidentNonHumanAnimal are called`() =
        runTest {
            val updatedFosterHome = fosterHome.copy(
                allResidentNonHumanAnimals = emptyList()
            )
            val previousFosterHome = fosterHome.copy(
                id = "otherFosterHomeId",
                savedBy = authUser.uid,
                allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                    it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                }
            )

            modifyFosterHomeInLocalRepository(
                updatedFosterHome,
                previousFosterHome,
                TestScope()
            ) {}

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(fosterHome.imageUrl)

                localFosterHomeRepository.modifyFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    onModifyFosterHome.get()
                )
                localFosterHomeRepository.deleteResidentNonHumanAnimal(
                    fosterHome.allResidentNonHumanAnimals[0].nonHumanAnimalId + "other",
                    onDeleteResidentNonHumanAnimalIdForFosterHome.get()
                )
            }
            verifySuspend(exactly(0)) {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
                        fosterHomeId = ""
                    ).toEntity(),
                    any()
                )
            }
        }
}
