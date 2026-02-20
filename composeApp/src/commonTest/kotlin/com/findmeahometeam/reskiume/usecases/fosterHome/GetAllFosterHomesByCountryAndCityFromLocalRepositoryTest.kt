package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllFosterHomesByCountryAndCityFromLocalRepositoryTest: CoroutineTestDispatcher() {

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        every {
            getAllFosterHomesByCountryAndCity(fosterHome.country, fosterHome.city)
        } returns flowOf(listOf(fosterHomeWithAllNonHumanAnimalData))
    }

    private val getAllFosterHomesByCountryAndCityFromLocalRepository =
        GetAllFosterHomesByCountryAndCityFromLocalRepository(localFosterHomeRepository)

    @Test
    fun `given local foster homes_when the app retrieves them to list them by country and city_then app gets a flow of list of FosterHome`() =
        runTest {
            getAllFosterHomesByCountryAndCityFromLocalRepository(
                fosterHome.country,
                fosterHome.city
            ).test {
                assertEquals(listOf(fosterHome), awaitItem())
                awaitComplete()
            }
        }
}
