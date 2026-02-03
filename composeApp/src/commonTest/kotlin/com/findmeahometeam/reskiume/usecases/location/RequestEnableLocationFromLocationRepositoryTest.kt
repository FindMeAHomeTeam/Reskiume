package com.findmeahometeam.reskiume.usecases.location

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RequestEnableLocationFromLocationRepositoryTest {

    private val locationRepository: LocationRepository = mock {
        every {
            requestEnableLocation(
                any()
            )
        } returns Unit
    }

    private val requestEnableLocationFromLocationRepository =
        RequestEnableLocationFromLocationRepository(locationRepository)

    @Test
    fun `given the location services_when the app requests if they are enabled_then requestEnableLocation is called`() =
        runTest {
            requestEnableLocationFromLocationRepository {}
            verifySuspend {
                locationRepository.requestEnableLocation(any())
            }
        }
}
