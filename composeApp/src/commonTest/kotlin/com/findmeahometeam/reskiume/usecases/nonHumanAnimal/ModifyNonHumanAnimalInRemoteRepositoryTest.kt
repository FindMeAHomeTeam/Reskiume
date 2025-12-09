package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyNonHumanAnimalInRemoteRepositoryTest {

    val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
        mock {
            everySuspend {
                modifyRemoteNonHumanAnimal(nonHumanAnimal.toData(), any())
            } returns Unit
        }

    private val modifyNonHumanAnimalInRemoteRepository =
        ModifyNonHumanAnimalInRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

    @Test
    fun `given a non human animal_when the user modify them in the remote repository_then modifyRemoteNonHumanAnimal is called`() =
        runTest {
            modifyNonHumanAnimalInRemoteRepository(nonHumanAnimal, {})
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.modifyRemoteNonHumanAnimal(
                    nonHumanAnimal.toData(),
                    any()
                )
            }
        }
}
