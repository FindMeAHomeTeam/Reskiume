package com.findmeahometeam.reskiume.usecases.location

import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetLocationFromLocationRepositoryTest {

    private val locationRepository: LocationRepository = mock {
        everySuspend {
            getLocation()
        } returns Pair(activistLongitude, activistLatitude)
    }

    private val getLocationFromLocationRepository =
        GetLocationFromLocationRepository(locationRepository)

    @Test
    fun `given a location_when the app retrieves it_then getLocation is called`() =
        runTest {
            getLocationFromLocationRepository()
            verifySuspend {
                locationRepository.getLocation()
            }
        }
}
