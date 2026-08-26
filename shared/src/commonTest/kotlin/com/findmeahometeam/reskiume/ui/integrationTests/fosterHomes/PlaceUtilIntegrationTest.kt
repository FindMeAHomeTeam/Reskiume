package com.findmeahometeam.reskiume.ui.integrationTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStringProvider
import com.findmeahometeam.reskiume.ui.util.StringProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaceUtilIntegrationTest {

    private val stringProvider: StringProvider =
        FakeStringProvider("I found a non-human animal in the street. What can I do?")

    @Test
    fun `given a user requesting all countries_when the user clicks on the country selector_then the available countries are displayed`() =
        runTest {
            val placeUtil = PlaceUtil(stringProvider)
            placeUtil.allCountryItems().test {
                val expectedResult: List<Pair<Country, String>> = Country.entries
                    .filter { it != Country.UNSELECTED }
                    .map { it to stringProvider.getStringResource(it.toStringResource()) }
                assertEquals(expectedResult, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user requesting all cities by country_when the user clicks on the city selector_then the available cities are displayed`() =
        runTest {
            val placeUtil = PlaceUtil(stringProvider)
            placeUtil.allCityItems(Country.SPAIN).test {
                val expectedResult: List<Pair<City, String>> = City.entries
                    .filter { it != City.UNSELECTED && it.country == Country.SPAIN }
                    .map { it to stringProvider.getStringResource(it.toStringResource()) }
                assertEquals(expectedResult, awaitItem())
                awaitComplete()
            }
        }
}
