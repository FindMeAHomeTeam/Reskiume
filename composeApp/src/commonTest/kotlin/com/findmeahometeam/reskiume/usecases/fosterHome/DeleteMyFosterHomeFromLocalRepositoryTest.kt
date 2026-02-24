package com.findmeahometeam.reskiume.usecases.fosterHome

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteMyFosterHomeFromLocalRepository
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

class DeleteMyFosterHomeFromLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

        everySuspend {
            getNonHumanAnimal(nonHumanAnimal.id)
        } returns nonHumanAnimal.toEntity()

        everySuspend {
            modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
        } returns Unit
    }

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {

        everySuspend {
            getFosterHome(fosterHome.id)
        } returns fosterHomeWithAllNonHumanAnimalData

        everySuspend {
            getFosterHome("otherFosterHomeId")
        } returns fosterHomeWithAllNonHumanAnimalData.copy(allResidentNonHumanAnimalIds = fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.map {
            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
        })

        everySuspend {
            deleteFosterHome(fosterHome.id, any())
        } returns Unit

        everySuspend {
            deleteFosterHome("otherFosterHomeId", any())
        } returns Unit
    }

    val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {

        every {
            getNonHumanAnimalFlow(
                nonHumanAnimal.id,
                nonHumanAnimal.caregiverId,
                any()
            )
        } returns flowOf((nonHumanAnimal))

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

    private val deleteMyFosterHomeFromLocalRepository =
        DeleteMyFosterHomeFromLocalRepository(
            localFosterHomeRepository,
            localNonHumanAnimalRepository,
            checkNonHumanAnimalUtil,
            log
        )

    @Test
    fun `given my local foster home_when the app deletes it_then deleteFosterHome is called`() =
        runTest {
            deleteMyFosterHomeFromLocalRepository(fosterHome.id, TestScope()) {}
            verifySuspend {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
                localFosterHomeRepository.deleteFosterHome(fosterHome.id, any())
            }
        }

    @Test
    fun `given my local foster home_when the app deletes it on account deletion but the residents were deleted_then only deleteFosterHome is called`() =
        runTest {
            deleteMyFosterHomeFromLocalRepository("otherFosterHomeId", TestScope()) {}
            verifySuspend {
                localFosterHomeRepository.deleteFosterHome("otherFosterHomeId", any())
            }
            verifySuspend(exactly(0)) {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
            }
        }
}
