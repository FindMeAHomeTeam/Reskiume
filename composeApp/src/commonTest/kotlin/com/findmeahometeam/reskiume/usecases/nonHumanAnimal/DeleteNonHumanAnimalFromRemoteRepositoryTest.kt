package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteNonHumanAnimalFromRemoteRepositoryTest {

    val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
        mock {
            everySuspend {
                deleteRemoteNonHumanAnimal(nonHumanAnimal.id, user.uid, any())
            } returns Unit
        }

    private val deleteNonHumanAnimalFromRemoteRepository =
        DeleteNonHumanAnimalFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

    @Test
    fun `given a remote non human animal_when the app deletes the non human animal_then deleteRemoteNonHumanAnimal is called`() =
        runTest {
            deleteNonHumanAnimalFromRemoteRepository(nonHumanAnimal.id, user.uid, {})
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.deleteRemoteNonHumanAnimal(
                    nonHumanAnimal.id,
                    user.uid,
                    any()
                )
            }
        }
}
