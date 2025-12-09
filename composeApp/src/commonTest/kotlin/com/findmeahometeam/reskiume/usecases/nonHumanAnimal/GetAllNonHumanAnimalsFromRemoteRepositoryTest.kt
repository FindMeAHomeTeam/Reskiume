package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import app.cash.turbine.test
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllNonHumanAnimalsFromRemoteRepositoryTest {

    val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = mock {
        every {
            getAllRemoteNonHumanAnimals(user.uid)
        } returns flowOf(listOf(nonHumanAnimal.toData()))
    }

    private val getAllNonHumanAnimalsFromRemoteRepository =
        GetAllNonHumanAnimalsFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

    @Test
    fun `given remote non human animals_when the app retrieves them to list them_then app gets a flow of list of NonHumanAnimal`() =
        runTest {
            getAllNonHumanAnimalsFromRemoteRepository(user.uid).test {
                assertEquals(listOf(nonHumanAnimal.copy(savedBy = "")), awaitItem())
                awaitComplete()
            }
        }
}
