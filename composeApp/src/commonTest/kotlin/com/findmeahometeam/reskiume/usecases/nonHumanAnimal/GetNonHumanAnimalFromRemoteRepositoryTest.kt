package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import app.cash.turbine.test
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetNonHumanAnimalFromRemoteRepositoryTest {

    val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = mock {
        every {
            getRemoteNonHumanAnimal(nonHumanAnimal.id, user.uid)
        } returns flowOf(nonHumanAnimal.toData())
    }

    private val getNonHumanAnimalFromRemoteRepository =
        GetNonHumanAnimalFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

    @Test
    fun `given a remote non human animal_when the app retrieves them_then app gets a flow of NonHumanAnimal`() =
        runTest {
            getNonHumanAnimalFromRemoteRepository(nonHumanAnimal.id, user.uid).test {
                assertEquals(nonHumanAnimal.copy(savedBy = ""), awaitItem())
                awaitComplete()
            }
        }
}
