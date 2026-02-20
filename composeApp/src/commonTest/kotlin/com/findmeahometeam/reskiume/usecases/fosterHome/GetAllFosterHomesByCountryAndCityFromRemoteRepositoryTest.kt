package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllFosterHomesByCountryAndCityFromRemoteRepositoryTest: CoroutineTestDispatcher() {

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        every {
            getAllRemoteFosterHomesByCountryAndCity(fosterHome.country, fosterHome.city)
        } returns flowOf(listOf(fosterHome.toData()))
    }

    private val getAllFosterHomesByCountryAndCityFromRemoteRepository =
        GetAllFosterHomesByCountryAndCityFromRemoteRepository(
            fireStoreRemoteFosterHomeRepository
        )

    @Test
    fun `given remote foster homes_when the app retrieves them to list them by country and city_then app gets a flow of list of FosterHome`() =
        runTest {
            getAllFosterHomesByCountryAndCityFromRemoteRepository(
                fosterHome.country,
                fosterHome.city
            ).test {
                assertEquals(listOf(fosterHome), awaitItem())
                awaitComplete()
            }
        }
}
