package com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes

import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.ui.util.StringProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaceUtil(
    private val getStringProvider: StringProvider,
) {
    fun allCountryItems(): Flow<List<Pair<Country, String>>> = flow {

        val value = Country.entries
            .filter { it != Country.UNSELECTED }
            .map { it to getStringProvider.getStringResource(it.toStringResource()) }
        emit(value)
    }

    fun allCityItems(selectedCountry: Country): Flow<List<Pair<City, String>>> = flow {

        val list: List<Pair<City, String>> = if (selectedCountry == Country.UNSELECTED) {
            emptyList()
        } else {
            City.entries
                .filter { it != City.UNSELECTED && it.country == selectedCountry }
                .map { it to getStringProvider.getStringResource(it.toStringResource()) }
        }
        emit(list)
    }
}
