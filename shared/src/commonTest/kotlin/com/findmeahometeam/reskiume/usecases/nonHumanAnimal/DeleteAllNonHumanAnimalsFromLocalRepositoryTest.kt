package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteAllNonHumanAnimalsFromLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        everySuspend {
            deleteAllNonHumanAnimals(user.uid, any())
        } returns Unit
    }

    private val deleteAllNonHumanAnimalsFromLocalRepository =
        DeleteAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

    @Test
    fun `given local non human animals_when the app deletes them on account deletion_then deleteAllNonHumanAnimals is called`() =
        runTest {
            deleteAllNonHumanAnimalsFromLocalRepository(user.uid, {})
            verifySuspend {
                localNonHumanAnimalRepository.deleteAllNonHumanAnimals(user.uid, any())
            }
        }
}
