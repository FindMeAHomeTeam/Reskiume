package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
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

class DeleteMyRescueEventFromLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

        everySuspend {
            getNonHumanAnimal(nonHumanAnimal.id)
        } returns nonHumanAnimal.toEntity()

        everySuspend {
            modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
        } returns Unit

        everySuspend {
            modifyNonHumanAnimal(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity(), any())
        } returns Unit
    }

    private val localRescueEventRepository: LocalRescueEventRepository = mock {

        everySuspend {
            getRescueEvent(rescueEvent.id)
        } returns rescueEventWithAllNeedsAndNonHumanAnimalData

        everySuspend {
            getRescueEvent("otherRescueEventId")
        } returns rescueEventWithAllNeedsAndNonHumanAnimalData.copy(allNonHumanAnimalsToRescue = listOf(
            rescueEventWithAllNeedsAndNonHumanAnimalData.allNonHumanAnimalsToRescue[0].copy(nonHumanAnimalId = nonHumanAnimal.id + "firstOther"),
            rescueEventWithAllNeedsAndNonHumanAnimalData.allNonHumanAnimalsToRescue[1].copy(nonHumanAnimalId = nonHumanAnimal.id + "secondOther")
        ))

        everySuspend {
            deleteRescueEvent(rescueEvent.id, any())
        } returns Unit

        everySuspend {
            deleteRescueEvent("otherRescueEventId", any())
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
                nonHumanAnimal.id + "second",
                nonHumanAnimal.caregiverId,
                any()
            )
        } returns flowOf((nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")))

        every {
            getNonHumanAnimalFlow(
                nonHumanAnimal.id + "firstOther",
                nonHumanAnimal.caregiverId,
                any()
            )
        } returns flowOf()

        every {
            getNonHumanAnimalFlow(
                nonHumanAnimal.id + "secondOther",
                nonHumanAnimal.caregiverId,
                any()
            )
        } returns flowOf()
    }

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val deleteMyRescueEventFromLocalRepository =
        DeleteMyRescueEventFromLocalRepository(
            localRescueEventRepository,
            checkNonHumanAnimalUtil,
            localNonHumanAnimalRepository,
            log
        )

    @Test
    fun `given my local rescue event_when the app deletes it_then modifyNonHumanAnimal and deleteRescueEvent are called`() =
        runTest {
            deleteMyRescueEventFromLocalRepository(rescueEvent.id, TestScope()) {}
            verifySuspend {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
                localRescueEventRepository.deleteRescueEvent(rescueEvent.id, any())
            }
        }

    @Test
    fun `given my local rescue event_when the app deletes it on account deletion but the non human animals to save were deleted_then only deleteRescueEvent is called`() =
        runTest {
            deleteMyRescueEventFromLocalRepository("otherRescueEventId", TestScope()) {}
            verifySuspend {
                localRescueEventRepository.deleteRescueEvent("otherRescueEventId", any())
            }
            verifySuspend(exactly(0)) {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
            }
        }
}
