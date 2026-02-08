package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFosterHomeFromRemoteRepositoryTest: CoroutineTestDispatcher() {

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        every {
            getRemoteFosterHome(
                fosterHome.id,
                fosterHome.ownerId
            )
        } returns flowOf(fosterHome.toData())
    }
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {
        every {
            getNonHumanAnimalFlow(
                nonHumanAnimalId = nonHumanAnimal.id,
                caregiverId = nonHumanAnimal.caregiverId
            )
        } returns flowOf(nonHumanAnimal).toUiState()
    }

    private val getFosterHomeFromRemoteRepository =
        GetFosterHomeFromRemoteRepository(
            fireStoreRemoteFosterHomeRepository,
            checkNonHumanAnimalUtil
        )

    @Test
    fun `given a remote foster home_when the app retrieves it to display it_then app gets a flow of FosterHome`() =
        runTest {
            getFosterHomeFromRemoteRepository(
                fosterHome.id,
                fosterHome.ownerId
            ).test {
                assertEquals(fosterHome, awaitItem())
                awaitComplete()
            }
        }
}
