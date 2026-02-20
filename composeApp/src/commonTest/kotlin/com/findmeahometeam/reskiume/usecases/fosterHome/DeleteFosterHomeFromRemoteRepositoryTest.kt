package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteMyFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteFosterHomeFromRemoteRepositoryTest {

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        everySuspend {
            getRemoteFosterHome(fosterHome.id)
        } returns flowOf(fosterHome.toData())

        everySuspend {
            getRemoteFosterHome("otherFosterHomeId")
        } returns flowOf(fosterHome.copy(allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
        }).toData())

        everySuspend {
            deleteRemoteFosterHome(fosterHome.id, fosterHome.ownerId, any())
        } returns Unit

        everySuspend {
            deleteRemoteFosterHome("otherFosterHomeId", "otherFosterHomeOwnerId", any())
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
                modifyRemoteNonHumanAnimal(nonHumanAnimal.toData(), any())
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

    private val deleteMyFosterHomeFromRemoteRepository =
        DeleteMyFosterHomeFromRemoteRepository(
            authRepository,
            fireStoreRemoteFosterHomeRepository,
            realtimeDatabaseRemoteNonHumanAnimalRepository,
            deleteNonHumanAnimalUtil,
            log
        )

    @Test
    fun `given a remote foster home_when the app deletes it_then modifyRemoteNonHumanAnimal and deleteRemoteFosterHome are called`() =
        runTest {
            deleteMyFosterHomeFromRemoteRepository(
                fosterHome.id,
                fosterHome.ownerId,
                TestScope()
            ) {}
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.toData(),
                    any()
                )
                fireStoreRemoteFosterHomeRepository.deleteRemoteFosterHome(
                    fosterHome.id,
                    fosterHome.ownerId,
                    any()
                )
            }
        }

    @Test
    fun `given a remote FH_when the app deletes it on ac deletion but the residents were deleted_then deleteNonHumanAnimal and deleteRemoteFosterHome are called`() =
        runTest {
            deleteMyFosterHomeFromRemoteRepository(
                "otherFosterHomeId",
                "otherFosterHomeOwnerId",
                TestScope()
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
                fireStoreRemoteFosterHomeRepository.deleteRemoteFosterHome(
                    "otherFosterHomeId",
                    "otherFosterHomeOwnerId",
                    any()
                )
            }
        }
}
