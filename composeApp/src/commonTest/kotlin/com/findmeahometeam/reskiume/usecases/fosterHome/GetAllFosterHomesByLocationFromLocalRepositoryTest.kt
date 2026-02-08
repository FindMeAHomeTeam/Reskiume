package com.findmeahometeam.reskiume.usecases.fosterHome

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllFosterHomesByLocationFromLocalRepositoryTest: CoroutineTestDispatcher() {

    private val localFosterHomeRepository: LocalFosterHomeRepository = mock {
        everySuspend {
            getAllFosterHomesByLocation(
                activistLongitude,
                activistLatitude,
                fosterHome.longitude,
                fosterHome.latitude
            )
        } returns flowOf(listOf(fosterHomeWithAllNonHumanAnimalData))
    }
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {
        every {
            getNonHumanAnimalFlow(
                nonHumanAnimalId = nonHumanAnimal.id,
                caregiverId = nonHumanAnimal.caregiverId
            )
        } returns flowOf(nonHumanAnimal).toUiState()
    }

    private val getAllFosterHomesByLocationFromLocalRepository =
        GetAllFosterHomesByLocationFromLocalRepository(localFosterHomeRepository, checkNonHumanAnimalUtil)

    @Test
    fun `given local foster homes_when the app retrieves them to list them by location_then app gets a flow of list of FosterHome`() =
        runTest {
            getAllFosterHomesByLocationFromLocalRepository(
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
