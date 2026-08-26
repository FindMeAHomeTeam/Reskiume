package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllFosterHomesFromLocalRepositoryTest: CoroutineTestDispatcher() {

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        every {
            getAllFosterHomes()
        } returns flowOf(listOf(fosterHomeWithAllNonHumanAnimalData))
    }

    private val getAllFosterHomesFromLocalRepository =
        GetAllFosterHomesFromLocalRepository(localFosterHomeRepository)

    @Test
    fun `given local foster homes_when the app retrieves them to list them_then the app gets a flow of list of FosterHome`() =
        runTest {
            getAllFosterHomesFromLocalRepository().test {
                assertEquals(listOf(fosterHome), awaitItem())
                awaitComplete()
            }
        }
}
