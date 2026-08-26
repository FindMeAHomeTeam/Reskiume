package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteAllNonHumanAnimalsFromRemoteRepositoryTest {

    val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = mock {
        everySuspend {
            deleteAllRemoteNonHumanAnimals(user.uid, any())
        } returns Unit
    }

    private val deleteAllNonHumanAnimalsFromRemoteRepository =
        DeleteAllNonHumanAnimalsFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

    @Test
    fun `given remote non human animals_when the app deletes them on account deletion_then deleteAllRemoteNonHumanAnimals is called`() =
        runTest {
            deleteAllNonHumanAnimalsFromRemoteRepository(user.uid, {})
            verifySuspend {
                realtimeDatabaseRemoteNonHumanAnimalRepository.deleteAllRemoteNonHumanAnimals(user.uid, any())
            }
        }
}
