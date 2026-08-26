package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
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

class DeleteAllMyFosterHomesFromLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

        everySuspend {
            modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
        } returns Unit
    }

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {

        everySuspend {
            getAllMyFosterHomes(fosterHome.ownerId)
        } returns flowOf(listOf(fosterHomeWithAllNonHumanAnimalData))

        everySuspend {
            getAllMyFosterHomes("otherFosterHomeOwnerId")
        } returns flowOf(listOf(fosterHomeWithAllNonHumanAnimalData.copy(allResidentNonHumanAnimalIds = fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.map {
            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
        })))

        everySuspend {
            deleteAllMyFosterHomes(fosterHome.ownerId, any())
        } returns Unit

        everySuspend {
            deleteAllMyFosterHomes("otherFosterHomeOwnerId", any())
        } returns Unit
    }

    val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {

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

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val deleteAllMyFosterHomesFromLocalRepository =
        DeleteAllMyFosterHomesFromLocalRepository(
            localFosterHomeRepository,
            checkNonHumanAnimalUtil,
            localNonHumanAnimalRepository,
            log
        )

    @Test
    fun `given my own local foster homes_when the app deletes them on account deletion_then modifyNonHumanAnimal and deleteAllMyFosterHomes are called`() =
        runTest {
            deleteAllMyFosterHomesFromLocalRepository(fosterHome.ownerId, TestScope()) {}
            verifySuspend {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
                localFosterHomeRepository.deleteAllMyFosterHomes(fosterHome.ownerId, any())
            }
        }

    @Test
    fun `given my own local foster homes_when the app deletes them on account deletion but the residents were deleted_then only deleteAllMyFosterHomes is called`() =
        runTest {
            deleteAllMyFosterHomesFromLocalRepository("otherFosterHomeOwnerId", TestScope()) {}
            verifySuspend {
                localFosterHomeRepository.deleteAllMyFosterHomes("otherFosterHomeOwnerId", any())
            }
            verifySuspend(exactly(0)) {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
            }
        }
}
