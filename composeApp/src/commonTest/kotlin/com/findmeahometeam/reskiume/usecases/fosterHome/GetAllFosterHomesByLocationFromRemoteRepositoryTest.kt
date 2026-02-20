package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.fosterHome
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllFosterHomesByLocationFromRemoteRepositoryTest: CoroutineTestDispatcher() {

    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {
        every {
            getAllRemoteFosterHomesByLocation(
                activistLongitude,
                activistLatitude,
                fosterHome.longitude,
                fosterHome.latitude
            )
        } returns flowOf(listOf(fosterHome.toData()))
    }

    private val getAllFosterHomesByLocationFromRemoteRepository =
        GetAllFosterHomesByLocationFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

    @Test
    fun `given remote foster homes_when the app retrieves them to list them by location_then app gets a flow of list of FosterHome`() =
        runTest {
            getAllFosterHomesByLocationFromRemoteRepository(
                activistLongitude,
                activistLatitude,
                fosterHome.longitude,
                fosterHome.latitude
            ).test {
                assertEquals(listOf(fosterHome), awaitItem())
                awaitComplete()
            }
        }
}
