package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteNonHumanAnimalFromLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        everySuspend {
            deleteNonHumanAnimal(nonHumanAnimal.id, user.uid, any())
        } returns Unit
    }

    private val deleteNonHumanAnimalFromLocalRepository =
        DeleteNonHumanAnimalFromLocalRepository(localNonHumanAnimalRepository)

    @Test
    fun `given local non human animal_when the app deletes the local non human animal_then deleteNonHumanAnimal is called`() =
        runTest {
            deleteNonHumanAnimalFromLocalRepository(nonHumanAnimal.id, user.uid, {})
            verifySuspend {
                localNonHumanAnimalRepository.deleteNonHumanAnimal(
                    nonHumanAnimal.id,
                    user.uid,
                    any()
                )
            }
        }
}
