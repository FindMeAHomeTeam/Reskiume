package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFosterHomeFromLocalRepositoryTest: CoroutineTestDispatcher() {

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        everySuspend {
            getFosterHome(fosterHome.id)
        } returns fosterHomeWithAllNonHumanAnimalData
    }

    private val getFosterHomeFromLocalRepository =
        GetFosterHomeFromLocalRepository(localFosterHomeRepository)

    @Test
    fun `given a local foster home_when the app retrieves it to display it_then app gets a flow of FosterHome`() =
        runTest {
            getFosterHomeFromLocalRepository(
                fosterHome.id
            ).test {
                assertEquals(fosterHome, awaitItem())
                awaitComplete()
            }
        }
}
