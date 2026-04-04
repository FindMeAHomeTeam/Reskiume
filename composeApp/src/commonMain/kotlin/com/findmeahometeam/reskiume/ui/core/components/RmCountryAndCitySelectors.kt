package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.country_city_selector_city
import reskiume.composeapp.generated.resources.country_city_selector_country

@Composable
fun RmCountryAndCitySelectors(
    placeUtil: PlaceUtil,
    selectedCountry: Country,
    selectedCity: City = City.UNSELECTED,
    onSelectedCountry: (country: Country) -> Unit = {},
    onSelectedCity: (city: City) -> Unit = {}
) {
    var isCountryVisible: Boolean by rememberSaveable { mutableStateOf(true) }
    val countryFieldState = rememberTextFieldState(if (selectedCountry == Country.UNSELECTED) {
        ""
    } else {
        stringResource(selectedCountry.toStringResource())
    })
    val countryItems: List<Pair<Country, String>> by placeUtil.allCountryItems()
        .collectAsState(initial = emptyList())
    var isCityVisible: Boolean by rememberSaveable { mutableStateOf(selectedCountry != Country.UNSELECTED) }
    val cityFieldState = rememberTextFieldState(if (selectedCity == City.UNSELECTED) {
        ""
    } else {
        stringResource(selectedCity.toStringResource())
    })
    val cityItems: List<Pair<City, String>> by placeUtil.allCityItems(
        selectedCountry
    ).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.heightIn(max = 300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCountryVisible) {
                RmSearchBarWithSuggestions(
                    modifier = Modifier.weight(1f),
                    onFocusChanged = {
                        if (it.isFocused) {
                            isCityVisible = false
                        }
                    },
                    textFieldState = countryFieldState,
                    placeholder = stringResource(Res.string.country_city_selector_country),
                    items = countryItems,
                    onSearch = { country: Country? ->
                        country?.let { onSelectedCountry(it) }
                        if (country == null) {
                            onSelectedCountry(Country.UNSELECTED)
                            onSelectedCity(City.UNSELECTED)
                            cityFieldState.setTextAndPlaceCursorAtEnd("")
                        }
                        isCityVisible = country != null
                    }
                )
            }

            if (isCountryVisible && isCityVisible) {
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (isCityVisible && cityItems.isNotEmpty()) {
                RmSearchBarWithSuggestions(
                    modifier = Modifier.weight(1f),
                    onFocusChanged = {
                        if (it.isFocused) {
                            isCountryVisible = false
                        }
                    },
                    textFieldState = cityFieldState,
                    placeholder = stringResource(Res.string.country_city_selector_city),
                    items = cityItems,
                    onSearch = { city: City? ->
                        city?.let { onSelectedCity(it) }
                        if (city == null) {
                            onSelectedCity(City.UNSELECTED)
                            cityFieldState.setTextAndPlaceCursorAtEnd("")
                        }
                        isCountryVisible = true
                    }
                )
            }
        }
    }
}
