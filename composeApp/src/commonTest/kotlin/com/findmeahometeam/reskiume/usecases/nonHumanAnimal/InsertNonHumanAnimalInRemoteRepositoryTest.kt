package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertNonHumanAnimalInRemoteRepositoryTest {

    val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
        mock {
            everySuspend {
                insertRemoteNonHumanAnimal(nonHumanAnimal.toData(), any())
            } returns Unit
        }

    private val insertNonHumanAnimalInRemoteRepository =
        InsertNonHumanAnimalInRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

    @Test
    fun `given a non human animal_when the user insert them in the remote repository_then insertRemoteNonHumanAnimal is called`() =
        runTest {
            insertNonHumanAnimalInRemoteRepository(nonHumanAnimal, {})
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.insertRemoteNonHumanAnimal(
                    nonHumanAnimal.toData(),
                    any()
                )
            }
        }
}
