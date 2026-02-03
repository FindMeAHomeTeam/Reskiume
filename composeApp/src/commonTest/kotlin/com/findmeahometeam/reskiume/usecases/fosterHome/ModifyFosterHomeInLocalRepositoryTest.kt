package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
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

class ModifyFosterHomeInLocalRepositoryTest {

    private val onModifyFosterHome = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyAcceptedNonHumanAnimalForFosterHome = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onModifyAcceptedSecondNonHumanAnimalForFosterHome = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onModifyResidentNonHumanAnimalIdForFosterHome = Capture.slot<(rowsUpdated: Int) -> Unit>()

    val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        everySuspend {
            modifyFosterHome(
                fosterHome.copy(savedBy = authUser.uid).toEntity(),
                capture(onModifyFosterHome)
            )
        } calls {
            onModifyFosterHome.get().invoke(1)
        }

        everySuspend {
            modifyAcceptedNonHumanAnimalForFosterHome(
                fosterHome.allAcceptedNonHumanAnimals[0].toEntity(),
                capture(onModifyAcceptedNonHumanAnimalForFosterHome)
            )
        } calls {
            onModifyAcceptedNonHumanAnimalForFosterHome.get().invoke(1)
        }

        everySuspend {
            modifyAcceptedNonHumanAnimalForFosterHome(
                fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                capture(onModifyAcceptedSecondNonHumanAnimalForFosterHome)
            )
        } calls {
            onModifyAcceptedSecondNonHumanAnimalForFosterHome.get().invoke(1)
        }

        everySuspend {
            modifyResidentNonHumanAnimalIdForFosterHome(
                fosterHome.allResidentNonHumanAnimals[0].toEntityForId(),
                capture(onModifyResidentNonHumanAnimalIdForFosterHome)
            )
        } calls {
            onModifyResidentNonHumanAnimalIdForFosterHome.get().invoke(1)
        }
    }

    private val authRepository: AuthRepository = mock {
        everySuspend {
            authState
        } returns flowOf(authUser)
    }

    private val modifyFosterHomeInLocalRepository =
        ModifyFosterHomeInLocalRepository(localFosterHomeRepository, authRepository)

    @Test
    fun `given a local foster home_when the app modifies it_then modifies methods are called`() =
        runTest {
            modifyFosterHomeInLocalRepository(fosterHome) {}

            verifySuspend {
                localFosterHomeRepository.modifyFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    onModifyFosterHome.get()
                )
                localFosterHomeRepository.modifyAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[0].toEntity(),
                    onModifyAcceptedNonHumanAnimalForFosterHome.get()
                )
                localFosterHomeRepository.modifyAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                    onModifyAcceptedSecondNonHumanAnimalForFosterHome.get()
                )
                localFosterHomeRepository.modifyResidentNonHumanAnimalIdForFosterHome(
                    fosterHome.allResidentNonHumanAnimals[0].toEntityForId(),
                    onModifyResidentNonHumanAnimalIdForFosterHome.get()
                )
            }
        }
}
