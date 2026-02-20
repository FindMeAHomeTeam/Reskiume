package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllMyNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllNonHumanAnimalsFromLocalRepositoryTest: CoroutineTestDispatcher() {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        every {
            getAllMyNonHumanAnimals(user.uid)
        } returns flowOf(listOf(nonHumanAnimal.toEntity()))
    }

    private val getAllMyNonHumanAnimalsFromLocalRepository =
        GetAllMyNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

    @Test
    fun `given local non human animals_when the app retrieves them to list them_then app gets a flow of list of NonHumanAnimal`() =
        runTest {
            getAllMyNonHumanAnimalsFromLocalRepository(user.uid).test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }
}
