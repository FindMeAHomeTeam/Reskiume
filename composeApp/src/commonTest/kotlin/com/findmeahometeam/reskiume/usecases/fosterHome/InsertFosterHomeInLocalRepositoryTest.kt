package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertFosterHomeInLocalRepositoryTest {

    private val onInsertFosterHome = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertAcceptedNonHumanAnimalForFosterHome = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertAcceptedSecondNonHumanAnimalForFosterHome = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertResidentNonHumanAnimalIdForFosterHome = Capture.slot<(rowId: Long) -> Unit>()

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
                fosterHome.allResidentNonHumanAnimals[0].toEntityForId(),
                capture(onInsertResidentNonHumanAnimalIdForFosterHome)
            )
        } calls {
            onInsertResidentNonHumanAnimalIdForFosterHome.get().invoke(1L)
        }
    }

    private val authRepository: AuthRepository = mock {
        everySuspend {
            authState
        } returns flowOf(authUser)
    }

    private val insertFosterHomeInLocalRepository =
        InsertFosterHomeInLocalRepository(localFosterHomeRepository, authRepository)

    @Test
    fun `given a local foster home_when the app inserts it_then inserts methods are called`() =
        runTest {
            insertFosterHomeInLocalRepository(fosterHome) {}

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
                    fosterHome.allResidentNonHumanAnimals[0].toEntityForId(),
                    onInsertResidentNonHumanAnimalIdForFosterHome.get()
                )
            }
        }
}
