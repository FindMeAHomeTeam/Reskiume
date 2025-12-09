package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import app.cash.turbine.test
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetNonHumanAnimalFromLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        every {
            getNonHumanAnimal(nonHumanAnimal.id, user.uid)
        } returns flowOf(nonHumanAnimal.toEntity())
    }

    private val getNonHumanAnimalFromLocalRepository =
        GetNonHumanAnimalFromLocalRepository(localNonHumanAnimalRepository)

    @Test
    fun `given a local non human animal_when the app retrieves them_then app gets a flow of NonHumanAnimal`() =
        runTest {
            getNonHumanAnimalFromLocalRepository(nonHumanAnimal.id, user.uid).test {
                assertEquals(nonHumanAnimal, awaitItem())
                awaitComplete()
            }
        }
}
