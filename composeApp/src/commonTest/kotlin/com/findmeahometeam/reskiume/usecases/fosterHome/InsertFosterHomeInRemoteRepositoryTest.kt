package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
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

class InsertFosterHomeInRemoteRepositoryTest {

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        everySuspend {
            getRemoteFosterHome(fosterHome.id)
        } returns flowOf(fosterHome.toData())

        everySuspend {
            insertRemoteFosterHome(fosterHome.toData(), any())
        } returns Unit

        everySuspend {
            insertRemoteFosterHome(
                fosterHome.copy(
                    id = "otherFosterHomeId",
                    allResidentNonHumanAnimals = emptyList()
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
                getRemoteNonHumanAnimal(nonHumanAnimal.id + "other", nonHumanAnimal.caregiverId)
            } returns flowOf(null)

            everySuspend {
                modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.REHOMED,
                        fosterHomeId = fosterHome.id
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

    private val insertFosterHomeInRemoteRepository =
        InsertFosterHomeInRemoteRepository(
            authRepository,
            deleteNonHumanAnimalUtil,
            fireStoreRemoteFosterHomeRepository,
            realtimeDatabaseRemoteNonHumanAnimalRepository,
            log
        )

    @Test
    fun `given a remote foster home_when the app inserts it_then modifyRemoteNonHumanAnimal and insertRemoteFosterHome are called`() =
        runTest {
            insertFosterHomeInRemoteRepository(fosterHome, TestScope()) {}
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.REHOMED,
                        fosterHomeId = fosterHome.id
                    ).toData(),
                    any()
                )
                fireStoreRemoteFosterHomeRepository.insertRemoteFosterHome(
                    fosterHome.toData(),
                    any()
                )
            }
        }

    @Test
    fun `given a remote foster home_when the app inserts it but the residents were removed_then only insertRemoteFosterHome is called`() =
        runTest {
            insertFosterHomeInRemoteRepository(
                fosterHome.copy(
                    id = "otherFosterHomeId",
                    allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                        it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
                    }
                ),
                TestScope()
            ) {}
            verifySuspend {
                fireStoreRemoteFosterHomeRepository.insertRemoteFosterHome(
                    fosterHome.copy(
                        id = "otherFosterHomeId",
                        allResidentNonHumanAnimals = emptyList()
                    ).toData(),
                    any()
                )
            }
            verifySuspend(exactly(0)) {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.REHOMED,
                        fosterHomeId = fosterHome.id
                    ).toData(),
                    any()
                )
            }
        }
}
