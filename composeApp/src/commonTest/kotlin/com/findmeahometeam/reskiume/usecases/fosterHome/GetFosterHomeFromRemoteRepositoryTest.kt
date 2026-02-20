package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
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
                fosterHome.id
            )
        } returns flowOf(fosterHome.toData())
    }

    private val getFosterHomeFromRemoteRepository = GetFosterHomeFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

    @Test
    fun `given a remote foster home_when the app retrieves it to display it_then app gets a flow of FosterHome`() =
        runTest {
            getFosterHomeFromRemoteRepository(fosterHome.id).test {
                assertEquals(fosterHome, awaitItem())
                awaitComplete()
            }
        }
}
