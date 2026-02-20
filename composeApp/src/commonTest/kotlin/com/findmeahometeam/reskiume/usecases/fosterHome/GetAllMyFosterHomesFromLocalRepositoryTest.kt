package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllMyFosterHomesFromLocalRepositoryTest: CoroutineTestDispatcher() {

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        everySuspend {
            getAllMyFosterHomes(fosterHome.ownerId)
        } returns flowOf(listOf(fosterHomeWithAllNonHumanAnimalData))
    }

    private val getAllMyFosterHomesFromLocalRepository =
        GetAllMyFosterHomesFromLocalRepository(localFosterHomeRepository)

    @Test
    fun `given my own local foster homes_when the app retrieves them to list them_then app gets a flow of list of FosterHome`() =
        runTest {
            getAllMyFosterHomesFromLocalRepository(
                fosterHome.ownerId
            ).test {
                assertEquals(listOf(fosterHome), awaitItem())
                awaitComplete()
            }
        }
}
