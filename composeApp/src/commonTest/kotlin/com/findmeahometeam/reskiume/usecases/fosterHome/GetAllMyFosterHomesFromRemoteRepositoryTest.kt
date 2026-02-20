package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllMyFosterHomesFromRemoteRepositoryTest : CoroutineTestDispatcher() {

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        every {
            getAllMyRemoteFosterHomes(fosterHome.ownerId)
        } returns flowOf(listOf(fosterHome.toData()))
    }

    private val getAllMyFosterHomesFromRemoteRepository =
        GetAllMyFosterHomesFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

    @Test
    fun `given my own remote foster homes_when the app retrieves them to list them_then app gets a flow of list of FosterHome`() =
        runTest {
            getAllMyFosterHomesFromRemoteRepository(
                fosterHome.ownerId
            ).test {
                assertEquals(listOf(fosterHome), awaitItem())
                awaitComplete()
            }
        }
}
