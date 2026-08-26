package com.findmeahometeam.reskiume.ui.unitTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import com.findmeahometeam.reskiume.ui.util.StringProvider
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaceUtilTest {

    private val getStringProvider: StringProvider = mock {
        everySuspend {
            getStringResource(any())
        } returns "I found a non-human animal in the street. What can I do?"
    }

    @Test
    fun `given a user requesting all countries_when the user clicks on the country selector_then the available countries are displayed`() =
        runTest {
            val placeUtil = PlaceUtil(getStringProvider)
            placeUtil.allCountryItems().test {
                val expectedResult: List<Pair<Country, String>> = Country.entries
                    .filter { it != Country.UNSELECTED }
                    .map { it to getStringProvider.getStringResource(it.toStringResource()) }
                assertEquals(expectedResult, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user requesting all cities by country_when the user clicks on the city selector_then the available cities are displayed`() =
        runTest {
            val placeUtil = PlaceUtil(getStringProvider)
            placeUtil.allCityItems(Country.SPAIN).test {
                val expectedResult: List<Pair<City, String>> = City.entries
                    .filter { it != City.UNSELECTED && it.country == Country.SPAIN }
                    .map { it to getStringProvider.getStringResource(it.toStringResource()) }
                assertEquals(expectedResult, awaitItem())
                awaitComplete()
            }
        }
}
