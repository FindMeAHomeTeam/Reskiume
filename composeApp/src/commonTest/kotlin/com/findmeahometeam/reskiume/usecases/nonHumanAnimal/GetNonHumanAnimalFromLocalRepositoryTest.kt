package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetNonHumanAnimalFromLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        everySuspend {
            getNonHumanAnimal(nonHumanAnimal.id)
        } returns nonHumanAnimal.toEntity()
    }

    private val getNonHumanAnimalFromLocalRepository =
        GetNonHumanAnimalFromLocalRepository(localNonHumanAnimalRepository)

    @Test
    fun `given a local non human animal_when the app retrieves them_then getNonHumanAnimal is called`() =
        runTest {
            getNonHumanAnimalFromLocalRepository(nonHumanAnimal.id)
            verifySuspend {
                localNonHumanAnimalRepository.getNonHumanAnimal(nonHumanAnimal.id)
            }
        }
}
