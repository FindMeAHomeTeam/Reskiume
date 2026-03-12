package com.findmeahometeam.reskiume.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteAllMyRescueEventsFromLocalRepository
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
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteAllMyRescueEventsFromLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

        everySuspend {
            modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
        } returns Unit
    }

    private val localRescueEventRepository: LocalRescueEventRepository = mock {

        everySuspend {
            getAllMyRescueEvents(rescueEvent.creatorId)
        } returns flowOf(listOf(rescueEventWithAllNeedsAndNonHumanAnimalData))

        everySuspend {
            getAllMyRescueEvents("otherRescueEventCreatorId")
        } returns flowOf(listOf(rescueEventWithAllNeedsAndNonHumanAnimalData.copy(allNonHumanAnimalsToRescue = rescueEventWithAllNeedsAndNonHumanAnimalData.allNonHumanAnimalsToRescue.map {
            it.copy(nonHumanAnimalId = nonHumanAnimal.id + "other")
        })))

        everySuspend {
            deleteAllMyRescueEvents(rescueEvent.creatorId, any())
        } returns Unit

        everySuspend {
            deleteAllMyRescueEvents("otherRescueEventCreatorId", any())
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
                nonHumanAnimal.id + "second",
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

    private val deleteAllMyRescueEventsFromLocalRepository =
        DeleteAllMyRescueEventsFromLocalRepository(
            localRescueEventRepository,
            checkNonHumanAnimalUtil,
            localNonHumanAnimalRepository,
            log
        )

    @Test
    fun `given my own local rescue events_when the app deletes them on account deletion_then modifyNonHumanAnimal and deleteAllMyRescueEvents are called`() =
        runTest {
            deleteAllMyRescueEventsFromLocalRepository(rescueEvent.creatorId, this) {}
            verifySuspend {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
                localRescueEventRepository.deleteAllMyRescueEvents(rescueEvent.creatorId, any())
            }
        }

    @Test
    fun `given my own local rescue events_when the app deletes them on account deletion but the non human animals were deleted_then only deleteAllMyRescueEvents is called`() =
        runTest {
            deleteAllMyRescueEventsFromLocalRepository("otherRescueEventCreatorId", this) {}
            verifySuspend {
                localRescueEventRepository.deleteAllMyRescueEvents("otherRescueEventCreatorId", any())
            }
            verifySuspend(exactly(0)) {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
            }
        }
}
